package com.example.field.fieldtest;

import android.app.Instrumentation;
import android.database.sqlite.SQLiteDatabase;
import android.net.TrafficStats;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 * Created by wangshiqiandemac on 2018/4/27.   发送前的填充未做
 */
@RunWith(AndroidJUnit4.class)
public class WeiXinVoice {

    UiDevice uiDevice;
    Instrumentation instrumentation;
    long startMili = 0;// 开始发送时间，单位毫秒
    long endMili = 0;// 结束发送时间，单位毫秒
    String DestID = "Ma";
    int RptTimes = 9;
    int time = 2000;
    int RptInterval = 1000;
    SQLiteDatabase db = null;
    long deltaTxBytes, deltaRxBytes;
    long startTotalTxBytes;
    long startTotalRxBytes;
    String fixedPath ;
    String adbName;
    @Before
    public void setUp() throws Exception{
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);
        //解析参数
        fixedPath= Environment.getExternalStorageDirectory()+"/adbtestcase/parameter.json";
        StringBuilder builder = new StringBuilder();
        File file= new File(fixedPath);
        InputStream is= new FileInputStream(file);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line=br.readLine()) != null) {
            builder.append(line);
        }

        String[] st = builder.toString().split("###");
        JSONObject ob1= new JSONArray(st[0]).getJSONObject(0);
        JSONObject ob2= new JSONObject(st[1]);

        time=1000*Integer.parseInt(ob1.getString("WeiXin_Voice_Time"));
        DestID = ob1.getString("WeiXin_Voice_DestID").toString();
        RptTimes = Integer.parseInt(ob1.getString("WeiXin_Voice_RptTimes"));
        RptInterval = 1000 * Integer.parseInt(ob1
                .getString("WeiXin_Voice_RptInterval"));
        adbName = ob2.getString("logpath");
    }
    //开始运行微信语音测试
    @Test
    public void runWeixin()throws UiObjectNotFoundException {
        createSqlite();
        uiDevice.pressHome();
        uiDevice.pressHome();
        uiDevice.pressHome();
        solveFlashBack();

    }
    public void sleep(int mint){
        try{
            Thread.sleep(mint);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    //  解决打开微信可能出现的闪退问题
    public void solveFlashBack() throws UiObjectNotFoundException {
        UiObject weChat=uiDevice.findObject(new UiSelector().text("微信"));
        for(int i =0;i<2;i++){
            weChat.click();
            uiDevice.pressBack();
            uiDevice.pressBack();
            uiDevice.pressHome();
        }
        weChat.click();
        sleep(3000);
        findContacts();

    }
    //寻找微信联系人
    public void findContacts() throws UiObjectNotFoundException {
        UiObject targetContact=uiDevice.findObject(new UiSelector().text("Ma"));
        if(targetContact.exists()){
            targetContact.click();
        }else if(!targetContact.exists()&&DestID.endsWith("Ma")){

            UiObject tongXunLuTab = uiDevice.findObject(new UiSelector().text("通讯录"));
            tongXunLuTab.clickAndWaitForNewWindow();
            UiScrollable tongXunLuList = new UiScrollable(
                    new UiSelector().scrollable(true));
            if (tongXunLuList.exists()) {
                tongXunLuList.scrollIntoView(new UiSelector().text("群聊"));
            }

            UiObject groupChat = uiDevice.findObject(new UiSelector().text("群聊"));
            groupChat.clickAndWaitForNewWindow();

            UiObject pandaItem = uiDevice.findObject(new UiSelector().text(DestID));
            pandaItem.clickAndWaitForNewWindow();
            UiObject faXiaoXiButton = uiDevice.findObject(new UiSelector().text("发消息"));
            if (faXiaoXiButton.exists()) {
                faXiaoXiButton.clickAndWaitForNewWindow();
            }

        }else if(!targetContact.exists()&&!DestID.endsWith("Ma")){


            UiObject tongXunLuTab = uiDevice.findObject(new UiSelector().text("通讯录"));
            tongXunLuTab.clickAndWaitForNewWindow();

            UiScrollable tongXunLuList = new UiScrollable(
                    new UiSelector().scrollable(true));

            while (uiDevice.findObject(new UiSelector().text(DestID)).exists() == false) {

                tongXunLuList = new UiScrollable(
                        new UiSelector().scrollable(true));
                tongXunLuList.scrollForward();

            }

            UiObject pandaItem = uiDevice.findObject(new UiSelector().text(DestID));

            pandaItem.clickAndWaitForNewWindow();

            UiObject faXiaoXiButton = uiDevice.findObject(new UiSelector().text("发消息"));
            if (faXiaoXiButton.exists()) {
                faXiaoXiButton.click();

            }

        }
        sendVoice();
    }
    //发送语音消息
    private void sendVoice() throws UiObjectNotFoundException{

        UiObject sendButton= uiDevice.findObject(new UiSelector().className("android.widget.Button").text("发送"));
        if (sendButton.exists()){
            sendButton.click();
        }

        UiObject RL2 = uiDevice.findObject(new UiSelector()
                .className("android.widget.RelativeLayout")
                .resourceId(GlobalConfig.voiceNewParent)
                .instance(
                        getInstenceNum("android.widget.TextView",
                                GlobalConfig.voiceNew)));

        // 发送等待控件
        UiObject WeiXinWaitingSend = RL2.getChild(new UiSelector()
                .className("android.widget.ProgressBar").resourceId(
                        GlobalConfig.voiceSendWait));
        // 发送失败控件
        UiObject IV = RL2.getChild(new UiSelector()
                .className("android.widget.ImageView")
                .resourceId(GlobalConfig.voiceSendFail)
                .descriptionContains("重发"));

        UiObject voiceSwitch=uiDevice.findObject(new UiSelector().descriptionContains("切换到按住说话"));
        UiObject voiceButton=uiDevice.findObject(new UiSelector().descriptionContains("按住说话"));
        UiObject moreFunction = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.moreFunction));
        if(!voiceButton.exists()){
            moreFunction.click();
            voiceSwitch.click();
        }
        for(int voiceCount = 0; voiceCount<RptTimes;voiceCount++){
            startTotalTxBytes = TrafficStats.getMobileTxBytes();
            startTotalRxBytes = TrafficStats.getMobileRxBytes();
            if(voiceButton.exists()){
                longClick(uiDevice,voiceButton,time/19);
            }else{
                moreFunction.click();
                voiceSwitch.click();
                longClick(uiDevice,voiceButton,time/19);
            }

            startMili = System.currentTimeMillis();// 当前时间对应的毫秒数


            if (WeiXinWaitingSend.waitUntilGone(10000)) {// 监控时间在10秒以内，如果超过10秒我们认为超出一般人为操作的极限，可能是断网或者网络不通
                endMili = System.currentTimeMillis();// 发送成功的毫秒数，这样一减就能够看出这个时间间隔
                deltaTxBytes = TrafficStats.getMobileTxBytes()
                        - startTotalTxBytes;
                deltaRxBytes = TrafficStats.getMobileRxBytes()
                        - startTotalRxBytes;


            } else {
                endMili = startMili + 10000;
                deltaTxBytes = TrafficStats.getMobileTxBytes()
                        - startTotalTxBytes;
                deltaRxBytes = TrafficStats.getMobileRxBytes()
                        - startTotalRxBytes;

            }

            if ((endMili - startMili) < 10000 & (endMili - startMili) >= 0) {
                if (IV.exists()) {
                    sleep(1000);
                    IV = null;
                    sleep(1000);
                    IV = RL2.getChild(new UiSelector()
                            .className("android.widget.ImageView")
                            .resourceId(GlobalConfig.voiceSendFail)
                            .descriptionContains("重发"));
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy/MM/dd HH:mm:ss");
                    if (IV.exists()) {
                        db.execSQL(
                                "INSERT INTO WeiXinVoiceLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                new Object[] {
                                        System.currentTimeMillis(),
                                        dateFormat.format(System
                                                .currentTimeMillis()),
                                        "com.WenXinVoice",
                                        String.valueOf(voiceCount), "Error", "Fail",
                                        "", Long.toString(deltaTxBytes),
                                        Long.toString(deltaRxBytes) });
                    } else {
                        db.execSQL(
                                "INSERT INTO WeiXinVoiceLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                new Object[] {
                                        System.currentTimeMillis(),
                                        dateFormat.format(System
                                                .currentTimeMillis()),
                                        "com.WenXinVoice",
                                        String.valueOf(voiceCount), "Error",
                                        "Judge Error", "",
                                        Long.toString(deltaTxBytes),
                                        Long.toString(deltaRxBytes) });
                    }

                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy/MM/dd HH:mm:ss");
                    // WeiXinVoiceLog.info("Success," +
                    // Long.toString(endMili - startMili));
                    db.execSQL(
                            "INSERT INTO WeiXinVoiceLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            new Object[] {
                                    System.currentTimeMillis(),
                                    dateFormat.format(System
                                            .currentTimeMillis()),
                                    "com.WenXinVoice", String.valueOf(voiceCount),
                                    "info", "Success",
                                    Long.toString(endMili - startMili),
                                    Long.toString(deltaTxBytes),
                                    Long.toString(deltaRxBytes) });

                }
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy/MM/dd HH:mm:ss");
                // WeiXinVoiceLog.info("OutOfTime," + Long.toString(endMili
                // - startMili));
                db.execSQL(
                        "INSERT INTO WeiXinVoiceLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new Object[] {
                                System.currentTimeMillis(),
                                dateFormat.format(System
                                        .currentTimeMillis()),
                                "com.WenXinVoice", String.valueOf(voiceCount),
                                "Error", "OutOfTime",
                                Long.toString(endMili - startMili),
                                Long.toString(deltaTxBytes),
                                Long.toString(deltaRxBytes) });

            }
            sleep(RptInterval);
        }

        uiDevice.pressBack();
        uiDevice.pressBack();
        uiDevice.pressHome();
    }
//    长按语音按钮
    public void longClick(UiDevice ud, UiObject uiObject, int steps)
            throws UiObjectNotFoundException {

        ud.swipe(uiObject.getBounds().centerX(),
                uiObject.getBounds().centerY(), uiObject.getBounds().centerX(),
                uiObject.getBounds().centerY(), steps);
    }

    //创建数据库

    public void createSqlite(){

        String DATABASE_PATH = "/sdcard/adbtestcase";
        String DATABASE_PATH_NAME = adbName+".db";
        File dir = new File(DATABASE_PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File dbfile = new File(DATABASE_PATH_NAME);

        if(!dbfile.exists()){
            db =  SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH_NAME, null);
        }else{
            db = SQLiteDatabase.openDatabase(DATABASE_PATH_NAME,null,SQLiteDatabase.OPEN_READWRITE);
        }
        db.execSQL("CREATE TABLE IF NOT EXISTS WeiXinVoiceLog (mili VARCHAR, format VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, result VARCHAR, speed VARCHAR, Tx VARCHAR, Rx VARCHAR)");

    }

    // 获取聊天框中消息数量的最大值
    public int getInstenceNum(String className, String resourceId) {
        int instence = 0;
        for (int k = 9; k >= 0; k--) {
            UiObject WeiXinWaitingSend = uiDevice.findObject(new UiSelector()
                    .className(className).resourceId(resourceId).instance(k));
            if (WeiXinWaitingSend.exists()) {
                instence = k;
                break;
            }
        }
        return instence;
    }

}

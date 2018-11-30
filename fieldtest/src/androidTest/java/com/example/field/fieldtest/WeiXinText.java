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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 * Created by wangshiqiandemac on 2018/4/27. 微信版本6.6.6  控件找不到重新退出未做
 */
@RunWith(AndroidJUnit4.class)
public class WeiXinText {
    UiDevice uiDevice;
    Instrumentation instrumentation;
    SQLiteDatabase db = null;
    String DestID = "Ma";//信息默认发送人
    int RptTimes =20;//微信文本发送的次数
    int RptInterval= 1000;//发送间隔时间
    long startTotalTxBytes;
    long startTotalRxBytes;
    long deltaTxBytes,deltaRxBytes;
    long startMili = 0;// 开始发送时间，单位毫秒
    long endMili = 0;// 结束发送时间，单位毫秒tmm
    String fixedPath ;
    String adbName;
    //测试之前 初始化数据
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

        RptInterval=1000*Integer.parseInt(ob1.getString("WeiXin_Text_RptInterval"));
        DestID = ob1.getString("WeiXin_Text_DestID").toString();
        RptTimes = Integer.parseInt(ob1.getString("WeiXin_Text_RptTimes"));

        adbName = ob2.getString("logpath");


    }
    //开始测试
    @Test
    public void runWeixin()throws UiObjectNotFoundException{
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
        sendMessage();
    }
    //循环发送文本
    public void sendMessage() throws UiObjectNotFoundException {
       UiObject sendButton= uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.WXTSendButton).text("发送"));
        if (sendButton.exists()){
            sendButton.click();
        }
       UiObject switchButton= uiDevice.findObject(new UiSelector().descriptionContains("切换到键盘"));
        if(switchButton.exists()){
            switchButton.click();
        }

        for(int count = 0; count<RptTimes; count++){

            String message = "基站大容量微信文本测试"+String.valueOf(count);
            UiObject textEdit=uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.WXTEdit));
            if (textEdit.exists()){
                textEdit.click();
            }
            textEdit.setText(message);
            sleep(3000);

           UiObject sendTextButton= uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.WXTSendButton));
            startTotalTxBytes = TrafficStats.getMobileTxBytes();
            startTotalRxBytes = TrafficStats.getMobileRxBytes();

            Log.i("traffic","TxBytes"+String.valueOf(startTotalTxBytes)+"-RxByte"+ String.valueOf(startTotalRxBytes));
            if(sendTextButton.exists()){
                sendTextButton.click();
            }else{
                uiDevice.pressBack();
                sendTextButton.click();
            }


            UiObject WeiXinTimeDelay = null;
            UiObject WeiXinSuccessORFail = null;
            for(int c =10 ; c>=0 ;c--) {
                UiObject tencentLastNew = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.WXTLastNew).instance(c));
                if(tencentLastNew.exists()) {
                    WeiXinTimeDelay = tencentLastNew.getFromParent(new UiSelector().className("android.widget.ProgressBar"));
                    WeiXinSuccessORFail = tencentLastNew.getFromParent(new UiSelector().className("android.widget.ImageView"));
                    break;
                }

            }

            startMili = System.currentTimeMillis();// 当前时间对应的毫秒数
            // }

            if (WeiXinTimeDelay.waitUntilGone(10000)) {// 监控时间在10秒以内，如果超过10秒我们认为超出一般人为操作的极限，可能是断网或者网络不通
                endMili = System.currentTimeMillis();// 发送成功的毫秒数，这样一减就能够看出这个时间间隔
                deltaTxBytes =  TrafficStats.getMobileTxBytes() - startTotalTxBytes;
                deltaRxBytes =  TrafficStats.getMobileRxBytes() - startTotalRxBytes;

            } else {
                endMili = startMili + 10000;
                deltaTxBytes =  TrafficStats.getMobileTxBytes() - startTotalTxBytes;
                deltaRxBytes =  TrafficStats.getMobileRxBytes() - startTotalRxBytes;

            }

            // 根据时延和发送状态计入日志
            if ((endMili - startMili) < 10000 & (endMili - startMili) >= 0) {
                if (WeiXinSuccessORFail.exists()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                   db.execSQL("INSERT INTO WeiXinTextLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinText",String.valueOf(count), "Error","Fail","",Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    db.execSQL("INSERT INTO WeiXinTextLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinText", String.valueOf(count), "info","Success",Long.toString(endMili - startMili),Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                }
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                db.execSQL("INSERT INTO WeiXinTextLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinText",String.valueOf(count), "Error","OutOfTime",Long.toString(endMili - startMili),Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
            }

            sleep(RptInterval);
        }
        uiDevice.pressBack();
        uiDevice.pressBack();
        uiDevice.pressBack();
        uiDevice.pressHome();
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
        db.execSQL("CREATE TABLE IF NOT EXISTS WeiXinTextLog (mili VARCHAR, format VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, result VARCHAR, speed VARCHAR, Tx VARCHAR, Rx VARCHAR)");

    }


}

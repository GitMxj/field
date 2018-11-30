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
 * Created by wangshiqiandemac on 2018/5/2.    首次填充还没有做   每次发完图片等待的时间有点长 具体原因要再查下
 */
@RunWith(AndroidJUnit4.class)
public class WeiXinImage {
    UiDevice uiDevice;
    Instrumentation instrumentation;
    String DestID = "Ma";
    int yuantuornot = 1;
    int RptTimes=20;
    String Size="1";
    int RptInterval=500;
    long startMili = 0;// 开始发送时间，单位毫秒
    long endMili = 0;// 结束发送时间，单位毫秒
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

        RptInterval=1000*Integer.parseInt(ob1.getString("WeiXin_Image_RptInterval"));
        DestID = ob1.getString("WeiXin_Image_DestID").toString();
        RptTimes = Integer.parseInt(ob1.getString("WeiXin_Image_RptTimes"));
        Size = ob1.getString("WeiXin_Image_Size").toString();
        yuantuornot = Integer.parseInt(ob1.getString("WeiXin_Image_Origin").toString());
        adbName = ob2.getString("logpath");
    }
    //开始测试
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
        sendImage();
    }
    //发送图片
    private void sendImage()throws UiObjectNotFoundException {
        UiObject sendButton= uiDevice.findObject(new UiSelector().className("android.widget.Button").text("发送"));
        if (sendButton.exists()){
            sendButton.click();
        }

        for(int count = 0;count<RptTimes;count++){

            selectImage(count);
            sleep(RptInterval);

        }

        uiDevice.pressBack();
        uiDevice.pressBack();
        uiDevice.pressHome();


    }
    //选取图片
    private void selectImage(int count)throws UiObjectNotFoundException{


        UiObject album=uiDevice.findObject(new UiSelector().text("相册"));
        UiObject moreFun=uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.moreFunction));


        if (moreFun.exists()&&!album.exists()){
            moreFun.click();
            sleep(1000);
        }
        for (int i = 0; i < 3; i++) {

            if (album.exists()) {
                album.click();
                break;
            } else {
                uiDevice.pressBack();
                moreFun.click();
                sleep(1000);
            }


        }


        UiObject picAndImgpre=uiDevice.findObject(new UiSelector().text("图片和视频").instance(1));
        picAndImgpre.click();


        // 根据size选择文件夹
        UiScrollable FloderSelectScrollpre = new UiScrollable(new UiSelector().className("android.widget.ListView"));

        UiSelector  imageFolder=new UiSelector().resourceId(GlobalConfig.imageListSize).text(Size);

        if(uiDevice.findObject(imageFolder).exists()){
            System.out.println("存在");
        }else{
            System.out.println("不存在");
        }

        while (FloderSelectScrollpre.scrollIntoView(uiDevice.findObject(imageFolder))) {

            UiObject FloderSelectPre = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.imageListSize).text(Size));
            FloderSelectPre.click();

            break;
        }
        UiScrollable PhotoSelectScroll = new UiScrollable(new UiSelector().resourceId(GlobalConfig.imageGradeView));
        UiObject targetPhoto = uiDevice.findObject(new UiSelector().descriptionContains("图片 " + String.valueOf(count+ 1)));
        PhotoSelectScroll.scrollIntoView(targetPhoto);
        if (targetPhoto.exists()) {
            sleep(500);
            targetPhoto.click();
            if (yuantuornot == 1) {

                UiObject yuantu = uiDevice.findObject(new UiSelector().text("原图"));
                yuantu.click();
            }
             startTotalTxBytes = TrafficStats.getMobileTxBytes();
             startTotalRxBytes = TrafficStats.getMobileRxBytes();

            UiObject  PhotoSend = uiDevice.findObject(new UiSelector().className("android.widget.TextView").textContains("发送"));
            PhotoSend.click();// 发送

            writeLog(count);

        }

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
        db.execSQL("CREATE TABLE IF NOT EXISTS WeiXinImageLog (mili VARCHAR, format VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, result VARCHAR, speed VARCHAR, Tx VARCHAR, Rx VARCHAR)");

    }

    public void writeLog(int count) throws UiObjectNotFoundException {

        startMili = System.currentTimeMillis();// 当前时间对应的毫秒数
        UiObject WeiXinWaitingSend = null;
        UiObject hasSendImg = null;
        for (int i = 5; i >= 0; i--) {
             hasSendImg = uiDevice.findObject(
                    new UiSelector().resourceId(GlobalConfig.imageHasSend).instance(i));
            if (hasSendImg.exists()) {
                WeiXinWaitingSend = hasSendImg.getChild(new UiSelector().resourceId(GlobalConfig.imageSendWait));
                break;
            }
        }

        if (WeiXinWaitingSend.waitUntilGone(20000)) {// 监控时间在10秒以内，如果超过10秒我们认为超出一般人为操作的极限，可能是断网或者网络不通
            endMili = System.currentTimeMillis();// 发送成功的毫秒数，这样一减就能够看出这个时间间隔
            deltaTxBytes = TrafficStats.getMobileTxBytes() - startTotalTxBytes;
            deltaRxBytes = TrafficStats.getMobileRxBytes() - startTotalRxBytes;
        } else {
            endMili = startMili + 20000;
            deltaTxBytes = TrafficStats.getMobileTxBytes() - startTotalTxBytes;
            deltaRxBytes = TrafficStats.getMobileRxBytes() - startTotalRxBytes;
        }

        UiObject IV = WeiXinWaitingSend.getFromParent(new UiSelector().resourceId(GlobalConfig.imageSendFail));

        if ((endMili - startMili) < 20000 & (endMili - startMili) >= 0) {

            if (IV.exists()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                db.execSQL("INSERT INTO WeiXinImageLog VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)",
                        new Object[] { System.currentTimeMillis(),
                                dateFormat.format(System.currentTimeMillis()), "com.WenXinImage",
                                String.valueOf(count + 1), "Error", "Fail", "", Long.toString(deltaTxBytes),
                                Long.toString(deltaRxBytes) });
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                db.execSQL("INSERT INTO WeiXinImageLog VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)",
                        new Object[] { System.currentTimeMillis(),
                                dateFormat.format(System.currentTimeMillis()), "com.WenXinImage",
                                String.valueOf(count + 1), "info", "Success", Long.toString(endMili - startMili),
                                Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
            }

        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            db.execSQL("INSERT INTO WeiXinImageLog VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)",
                    new Object[] { System.currentTimeMillis(), dateFormat.format(System.currentTimeMillis()),
                            "com.WenXinImage", String.valueOf(count + 1), "Error", "OutOfTime",
                            Long.toString(endMili - startMili), Long.toString(deltaTxBytes),
                            Long.toString(deltaRxBytes) });
        }

        UiObject sendOutTime = uiDevice.findObject(new UiSelector().text("正在发送中"));

        if (sendOutTime.exists()) {
            sendOutTime.waitUntilGone(60 * 1000);
        }



    }

}

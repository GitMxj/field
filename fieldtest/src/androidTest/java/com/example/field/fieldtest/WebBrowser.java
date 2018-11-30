package com.example.field.fieldtest;

/**
 * Created by wangshiqiandemac on 2018/4/24.
 */

import android.app.Instrumentation;
import android.database.sqlite.SQLiteDatabase;
import android.net.TrafficStats;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.KeyEvent;

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
import java.util.Date;

/**
 * Created by tianxing on 2017/8/15.
 */

@RunWith(AndroidJUnit4.class)
public class WebBrowser {

    UiDevice uiDevice;
    Instrumentation instrumentation;
    SQLiteDatabase webdb = null;
    long beforeTotalTxBytes = 0, afterTotalTxBytes = 0;
    long beforeTotalRxBytes = 0, afterTotalRxBytes = 0;
    long startMili = 0;// 开始发送时间，单位毫秒
    long endMili = 0;// 结束发送时间，单位毫秒
    float speedTx;
    float speedRx;
    int sequence = 1;//日志记录序号
    String DestWebSite="www.baidu.com"; //默认网址
    long WaitingTolerance =10000;//连接失败等待时间
    int Duration=10000;//页面驻留时间
    String fixedPath ;
    String adbName;
//    String url;
    String[] DSTURL;
    int RptInterval;
    @Before
    public void setUp() throws Exception{
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);
        //解析参数
        fixedPath= Environment.getExternalStorageDirectory()+"/adbtestcase/parameter.json";
//        fixedPath= Environment.getExternalStorageDirectory()+"/adbtestcase/parameter.txt";
        StringBuilder builder = new StringBuilder();
        File file= new File(fixedPath);
        if(!file.exists()){
            file.mkdirs();
        }
        InputStream is= new FileInputStream(file);
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line=br.readLine()) != null) {
            builder.append(line);
        }

        String[] st = builder.toString().split("###");
        JSONObject ob1= new JSONArray(st[0]).getJSONObject(0);
        JSONObject ob2= new JSONObject(st[1]);
        WaitingTolerance = 1000*Integer.parseInt(ob1.getString("Web_Browse_WaitingTolerance"));
        Duration=1000*Integer.parseInt(ob1.getString("Web_Browse_Duration"));
        String destWebSiteInitial = ob1.getString("Web_Browse_DestWebSite").toString();
        DSTURL=destWebSiteInitial.split("\\s+");
        RptInterval = 1000*Integer.parseInt(ob1.getString("Web_Browse_RptInterval"));
        adbName = ob2.getString("logpath");


    }
    //开始执行测试
    @Test
    public void launchChrome()throws UiObjectNotFoundException{
        createSqlite();
        uiDevice.pressHome();
        uiDevice.pressHome();
        UiObject chrome = uiDevice.findObject(new UiSelector().text("Chrome"));
        chrome.click();
        firstOpenSet();
        while(true){

            for(String DestWebSite : DSTURL){
                if (!(DestWebSite == null || DestWebSite.isEmpty())){
                    uiDevice.pressHome();
                    chrome = uiDevice.findObject(new UiSelector().text("Chrome"));
                    chrome.click();
                    UiObject searchContent = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.chromeUrl));
                    if(searchContent.exists()){
                        searchContent.click();
                        searchContent.setText(DestWebSite);
                        beforeTotalTxBytes = TrafficStats.getTotalTxBytes();
                        beforeTotalRxBytes = TrafficStats.getTotalRxBytes();
                        uiDevice.pressKeyCode(KeyEvent.KEYCODE_ENTER);
                        recordData();
                        uiDevice.pressHome();
                    }
                }
            }
            cleanWebHistory();
            sleep(RptInterval);

        }



    }
    //谷歌浏览器第一次安装打开的基础配置
    public void firstOpenSet()throws UiObjectNotFoundException{
        UiObject acceptAgreement = uiDevice.findObject(new UiSelector().resourceId("com.android.chrome:id/terms_accept").text("接受并继续"));
        UiObject setSuggest = uiDevice.findObject(new UiSelector().resourceId("com.android.chrome:id/negative_button").text("不，谢谢"));
        UiObject  checkAgree=uiDevice.findObject(new UiSelector().resourceId("android:id/safe_alert_checkbox"));
        UiObject alertAgree=uiDevice.findObject(new UiSelector().resourceId("android:id/alertTitle"));
        UiObject checkButton=uiDevice.findObject(new UiSelector().resourceId("android:id/button1"));
        UiObject alertButton=uiDevice.findObject(new UiSelector().resourceId("com.huawei.systemmanager:id/btn_allow"));
        UiObject indexPageSearch=uiDevice.findObject(new UiSelector().resourceId("com.android.chrome:id/search_box_text").text("搜索或输入网址"));

        if(acceptAgreement.exists()){
            acceptAgreement.click();
        }
        if(setSuggest.exists()){
            setSuggest.click();
        }
        if(checkAgree.exists()){
            checkButton.click();
        }
        if(alertAgree.exists()){
            alertButton.click();
        }
        if(indexPageSearch.exists()){
            indexPageSearch.click();
        }

    }

    public void sleep(int mint){
        try{
            Thread.sleep(mint);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    //清楚历史记录
    public void cleanWebHistory() throws UiObjectNotFoundException {
        uiDevice.pressHome();
        uiDevice.pressHome();
        UiObject chrome = uiDevice.findObject(new UiSelector().text("Chrome"));
        chrome.click();
        UiObject URL = uiDevice.findObject(new UiSelector().className(
                "android.widget.EditText").resourceId(
                GlobalConfig.chromeUrl));
        if(URL.exists()){
            uiDevice.pressBack();
        }
        UiObject menu = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.chromeMenu));
        menu.click();

        sleep(200);

        UiObject history = uiDevice.findObject(new UiSelector().resourceId(
                GlobalConfig.chromeHistory).text("历史记录"));
        if(history.exists()){
            history.click();
            sleep(200);
            //判断是否存在历史记录
            UiObject noHistory = uiDevice.findObject(new UiSelector().text("此处没有任何历史记录"));
            if(noHistory.exists()){
                uiDevice.pressBack();
            }else{
                UiObject clean = uiDevice.findObject(new UiSelector().resourceId("com.android.chrome:id/clear_browsing_data_button").text("清除浏览数据…"));
                if (clean.exists()) {
                    clean.clickAndWaitForNewWindow();
                } else {
                    sleep(500);
                    if (clean.exists()) {
                        clean.clickAndWaitForNewWindow();
                    } else {
                        int height = uiDevice.getDisplayHeight();
                        int width = uiDevice.getDisplayWidth();
                        uiDevice.click(width/2, height-50);
                    }
                }
                sleep(200);
                UiObject CleanConfirm = uiDevice.findObject(new UiSelector().resourceId(
                        GlobalConfig.chromeClean).textContains("清除数据"));
                CleanConfirm.click();
                // 退出浏览器
                uiDevice.pressBack();
            }
            uiDevice.pressHome();
            sleep(1000);
            uiDevice.pressHome();
        }else{
            cleanWebHistory();
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
            webdb =  SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH_NAME, null);
        }else{
            webdb = SQLiteDatabase.openDatabase(DATABASE_PATH_NAME,null,SQLiteDatabase.OPEN_READWRITE);
        }
        webdb.execSQL("CREATE TABLE IF NOT EXISTS WebLog (mili VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, website VARCHAR, result VARCHAR, time VARCHAR, deltaTx VARCHAR, deltaRx VARCHAR, speedTx VARCHAR, speedRx VARCHAR)");

    }
    //记录测试数据
    public void recordData()throws UiObjectNotFoundException{

        UiObject URLWaitingTime= uiDevice.findObject(new UiSelector().resourceId("om.android.chrome:id/progress"));
//        UiObject URLWaitingTime = uiDevice.findObject(new UiSelector().resourceId("com.android.chrome:id/progress"));
        startMili = System.currentTimeMillis();// 当前时间对应的毫秒数
        //判断是否网络连接失败
        UiObject connectError = uiDevice.findObject(new UiSelector().descriptionContains("您处于离线状态"));
        if(connectError.exists()){
            afterTotalTxBytes = TrafficStats.getTotalTxBytes();
            afterTotalRxBytes = TrafficStats.getTotalRxBytes();
            String deltaTx = String.valueOf((afterTotalTxBytes - beforeTotalTxBytes)/1024);
            String deltaRx = String.valueOf((afterTotalRxBytes - beforeTotalRxBytes)/1024);
            speedTx = (afterTotalTxBytes - beforeTotalTxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
            speedRx = (afterTotalRxBytes - beforeTotalRxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
            webdb.execSQL("INSERT INTO WebLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ startMili,"com.WebBrowser", sequence, "error", DestWebSite, "ConnectionError","", deltaTx, deltaRx, speedTx, speedRx});

        }else{
            URLWaitingTime = uiDevice.findObject(new UiSelector().resourceId("com.android.chrome:id/progress"));
            if (URLWaitingTime.waitUntilGone(WaitingTolerance)) {//
                //判断是否连接失败
                if(connectError.exists()){
                    UiObject connectErrorConfirm = uiDevice.findObject(new UiSelector().text("确定"));

                    afterTotalTxBytes = TrafficStats.getTotalTxBytes();
                    afterTotalRxBytes = TrafficStats.getTotalRxBytes();
                    String deltaTx = String.valueOf((afterTotalTxBytes - beforeTotalTxBytes)/1024);
                    String deltaRx = String.valueOf((afterTotalRxBytes - beforeTotalRxBytes)/1024);
                    speedTx = (afterTotalTxBytes - beforeTotalTxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    speedRx = (afterTotalRxBytes - beforeTotalRxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    webdb.execSQL("INSERT INTO WebLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ startMili,"com.WebBrowser", sequence, "error", DestWebSite, "ConnectionError","", deltaTx, deltaRx, speedTx, speedRx});
                }
                endMili = System.currentTimeMillis();//
                UiObject FailReal = uiDevice.findObject(new UiSelector().description("无法访问此网站"));
                if(FailReal.exists()){
                    Date NOW = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    //WebBrowserLog.info(DestWebSite + ",Fail");
                    afterTotalTxBytes = TrafficStats.getTotalTxBytes();
                    afterTotalRxBytes = TrafficStats.getTotalRxBytes();
                    String deltaTx = String.valueOf((afterTotalTxBytes - beforeTotalTxBytes)/1024);
                    String deltaRx = String.valueOf((afterTotalRxBytes - beforeTotalRxBytes)/1024);
                    speedTx = (afterTotalTxBytes - beforeTotalTxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    speedRx = (afterTotalRxBytes - beforeTotalRxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    webdb.execSQL("INSERT INTO WebLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ startMili,"com.WebBrowser", sequence, "error", DestWebSite, "Fail","", deltaTx, deltaRx, speedTx, speedRx});
                }else{
                    Date NOW = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    afterTotalTxBytes = TrafficStats.getTotalTxBytes();
                    afterTotalRxBytes = TrafficStats.getTotalRxBytes();
                    String deltaTx = String.valueOf((afterTotalTxBytes - beforeTotalTxBytes)/1024);
                    String deltaRx = String.valueOf((afterTotalRxBytes - beforeTotalRxBytes)/1024);
                    speedTx = (afterTotalTxBytes - beforeTotalTxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    speedRx = (afterTotalRxBytes - beforeTotalRxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    webdb.execSQL("INSERT INTO WebLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ startMili,"com.WebBrowser", sequence, "info", DestWebSite, "Success",Long.toString(endMili - startMili), deltaTx, deltaRx, speedTx, speedRx});
                }
                // 驻留页面时间判断
                sleep(Duration);
            } else {

                UiObject FailReal = uiDevice.findObject(new UiSelector().descriptionContains("无法访问此网站"));
                if(FailReal.exists()){
                    Date NOW = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    afterTotalTxBytes = TrafficStats.getTotalTxBytes();
                    afterTotalRxBytes = TrafficStats.getTotalRxBytes();
                    String deltaTx = String.valueOf((afterTotalTxBytes - beforeTotalTxBytes)/1024);
                    String deltaRx = String.valueOf((afterTotalRxBytes - beforeTotalRxBytes)/1024);
                    speedTx = (afterTotalTxBytes - beforeTotalTxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    speedRx = (afterTotalRxBytes - beforeTotalRxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                    webdb.execSQL("INSERT INTO WebLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ startMili,"com.WebBrowser", sequence, "error", DestWebSite, "Fail","", deltaTx, deltaRx, speedTx, speedRx});
                }else {
                    endMili = System.currentTimeMillis();//

                    if(URLWaitingTime.exists()) {

                        Date NOW = new Date(System.currentTimeMillis());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        //WebBrowserLog.info(DestWebSite + ",Fail");
                        afterTotalTxBytes = TrafficStats.getTotalTxBytes();
                        afterTotalRxBytes = TrafficStats.getTotalRxBytes();
                        String deltaTx = String.valueOf((afterTotalTxBytes - beforeTotalTxBytes)/1024);
                        String deltaRx = String.valueOf((afterTotalRxBytes - beforeTotalRxBytes)/1024);
                        speedTx = (afterTotalTxBytes - beforeTotalTxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                        speedRx = (afterTotalRxBytes - beforeTotalRxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                        webdb.execSQL("INSERT INTO WebLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ startMili,"com.WebBrowser", sequence, "error", DestWebSite, "OutOfTime",Long.toString(endMili-startMili), deltaTx, deltaRx, speedTx, speedRx});

                    }else {
                        //记录成功
                        Date NOW = new Date(System.currentTimeMillis());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        afterTotalTxBytes = TrafficStats.getTotalTxBytes();
                        afterTotalRxBytes = TrafficStats.getTotalRxBytes();
                        String deltaTx = String.valueOf((afterTotalTxBytes - beforeTotalTxBytes)/1024);
                        String deltaRx = String.valueOf((afterTotalRxBytes - beforeTotalRxBytes)/1024);
                        speedTx = (afterTotalTxBytes - beforeTotalTxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                        speedRx = (afterTotalRxBytes - beforeTotalRxBytes)/1024/(System.currentTimeMillis()-startMili)*1000;
                        webdb.execSQL("INSERT INTO WebLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ startMili,"com.WebBrowser", sequence, "info", DestWebSite, "Success",Long.toString(endMili - startMili), deltaTx, deltaRx, speedTx, speedRx});
                    }

                }

            }
        }


        sequence = sequence+1;
//        cleanWebHistory();
    }

}

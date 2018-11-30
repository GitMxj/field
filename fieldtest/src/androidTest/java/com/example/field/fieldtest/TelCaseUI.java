package com.example.field.fieldtest;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

/**
 * Created by wangshiqiandemac on 2018/5/3.
 */
@RunWith(AndroidJUnit4.class)
public class TelCaseUI {
    private UiDevice uiDevice;
    private Instrumentation instrumentation;
    private static final String TAG = "TelCaseUI";
    private static final boolean DEBUG = false;
    //短呼一次的时间长短
    private long callDuration=60000;
    Context testedContext;

    //手机的制造厂商
    String manufacturer;
    //手机型号
    String model;

    SQLiteDatabase db = null;

    //数据库版本注释以下内容
    //Tools.writeTXTFile(als, sendCallLogFile);
    String dialNumber = "15116961228";   //default dialNumber
    int dialDuration = 20;   //default call duration time, 20 seconds
    int dialRepeatTimes = 5;  //default dial repeat times
    int dialRptInterval = 5;  //default dial repeat interval, 5 seconds
    int dialMaxFailure = 3;  //default max call failure times
    String dialType = "1"; //default send call type, type: short-count(1)/short-time(2)/long(0) 拨打方式长呼短呼
    int dialTime = 600;  //default send call total time, 600 seconds
    int dialWaitingTolerance = 30;  //default time to wait for receiver answer the call, 30 seconds


    boolean isActive = false;
    boolean isIdle = false;
    boolean isWaitTimeout = false;

    boolean isSuccess = false;
    boolean isDialing = false;
    boolean isAlerting = false;
    boolean isActiving = false;
    boolean isEndTest = false;

    long dialingLogTime;
    long alertingLogTime;
    long activeLogTime;
    long idleLogTime;

    String fixedPath;
    String adbName;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    SimpleDateFormat logdtformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");

    @Before
    public void setUp() throws Exception {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);
        testedContext = InstrumentationRegistry.getTargetContext();

        //硬件制造商
         manufacturer = Tools.getDeviceManufacturer();
        //手机型号
        model = Tools.getDeviceModel();

        Log.i("TelCase-----","before   create sql"+"手机制造厂商-----"+manufacturer+"手机型号----"+model);


        fixedPath = Environment.getExternalStorageDirectory() + "/adbtestcase/parameter.json";
        StringBuilder builder = new StringBuilder();
        File file = new File(fixedPath);
        InputStream is = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }

        String[] st = builder.toString().split("###");
        JSONObject ob1 = new JSONArray(st[0]).getJSONObject(0);
        JSONObject ob2 = new JSONObject(st[1]);


        dialNumber = ob1.getString("Call_Send_DestID");
        dialDuration = Integer.parseInt(ob1.getString("Call_Send_HoldTime"));
        dialRepeatTimes = Integer.parseInt(ob1.getString("Call_Send_ShortRptTimes"));
        dialRptInterval = Integer.parseInt(ob1.getString("Call_Send_RptInterval"));
        dialMaxFailure = Integer.parseInt(ob1.getString("Call_Send_MaxFailure"));
        dialType = ob1.getString("Call_Send_Type");
        dialTime = Integer.parseInt(ob1.getString("Call_Send_ShortDuration"));
        dialWaitingTolerance = Integer.parseInt(ob1.getString("Call_Send_WaitingTolerance"));


        adbName = ob2.getString("logpath");


        createSqlite();


    }

    @Test
    public void call() throws RemoteException, IOException, InterruptedException,UiObjectNotFoundException {

        if (!uiDevice.isScreenOn()) {
            uiDevice.wakeUp();
        }



        switch (dialType) {
            case "1": //short-count call
//                for (int i = 1; i <= dialRepeatTimes; i++) {
                while (true) {
                    testFlow();
                }
//                }
//                 break;

            case "2":  //short-time call
                break;
            default: //long call
                break;
        }
    }


    public void testFlow()throws UiObjectNotFoundException{

        //call start time
        long  startTime = System.currentTimeMillis();
        dialingLogTime=startTime;
        alertingLogTime=startTime;
        activeLogTime=startTime;
        idleLogTime=startTime;

        //拨打电话
        dialingPhone();
        //华为mate9
        UiObject alerting= uiDevice.findObject(new UiSelector().resourceId("com.android.incallui:id/callStateLable").textContains("对方已振铃"));
        UiObject dialing= uiDevice.findObject(new UiSelector().resourceId("com.android.incallui:id/callStateLable").textContains("正在拨号"));
        UiObject ringOFF= uiDevice.findObject(new UiSelector().packageName("com.android.incallui").descriptionContains("挂断"));
        UiObject activeFlag=  uiDevice.findObject(new UiSelector().resourceId("com.android.incallui:id/holdButton"));//界面等待控件
        //中国移动N2
        UiObject dialingN2= uiDevice.findObject(new UiSelector().resourceId("com.android.contacts:id/callStateLabel").textContains("正在呼叫"));
        UiObject ringOFFN2= uiDevice.findObject(new UiSelector().resourceId("com.android.contacts:id/floating_end_call_action_button").descriptionContains("结束通话"));
        UiObject activeFlagN2= uiDevice.findObject(new UiSelector().resourceId("com.android.contacts:id/recordButton").descriptionContains("录音"));
        UiObject callingN2=uiDevice.findObject(new UiSelector().textContains("通话中"));

        //开始拨号
      OUT1:  while(!isDialing){
            switch (model){
                case "MHA-AL00"://华为mate9
                    if (dialing.exists() && ringOFF.exists()){
                        dialingLogTime = System.currentTimeMillis();
                        isSuccess=false;
                        Log.i("call","正在拨号");
                        break OUT1;
                    }
                    break;
                case "M836"://移动N2
                    if (dialingN2.exists()){
                        dialingLogTime = System.currentTimeMillis();
                        isSuccess=false;
                        Log.i("call","正在拨号");
                        break OUT1;
                    }
                    break;
                default:break;
            }

            /**
             *  60秒内如果没有发起呼叫，则本次失败，进行下一轮呼叫，比如处于飞行模式时发起呼叫会失败，
             *  且没有DIALING状态log，此时计时60秒，超时则本轮呼叫失败，开始下一轮
             */
            if(((System.currentTimeMillis() - startTime) >= 60000)) {
                isSuccess = false;
                isAlerting = true;
                isActiving =true;
                break;
            }

        }

        //开始振铃
       OUT2: while (!isAlerting){
            switch (model){
                case "MHA-AL00"://华为mate9
                    if(alerting.exists() && ringOFF.exists()){
                        isSuccess = false;
                        Log.i("call","正在振铃");
                        break OUT2;
                    }
                    break;
                case "M836":// 华为mate9
                    if(dialingN2.exists() ){
                        isSuccess = false;
                        Log.i("call","正在振铃");
                        break OUT2;
                    }
                    break;
                default:break;
            }

//            Log.i("call","等待"+String.valueOf(activeFlag.isEnabled()));
        }


        //开始通话
        activeLogTime = System.currentTimeMillis();

      OUT3:  while (!isActiving){
            Log.i("call","进入通话循环");
            switch (model){
                case "MHA-AL00":
                    boolean isActive = activeFlag.isEnabled();
                    Log.i("call","等待"+String.valueOf(activeFlag.isEnabled()));
                    if (isActive){
                        isSuccess = true;
                        Log.i("call","正在通话");
                        sleep((int)callDuration);
                        Log.i("call","通话完毕");
                        hangUp();
                        break OUT3;
                    }
                    break;
                case "M836":
                        if(callingN2.exists()){
                            isSuccess = true;
                            Log.i("call","正在通话");
                            sleep((int)callDuration);
                            Log.i("call","通话完毕");
                            hangUp();
                            break OUT3;
                        }
                    break;
                default:break;
            }


            sleep(500);
        }



        //手机处于闲置
        idleLogTime= System.currentTimeMillis();



        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(dialingLogTime),logdtformat.format(dialingLogTime),"DIALING","","","","","","" });

        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(alertingLogTime),logdtformat.format(alertingLogTime),"ALERTING","","","","","","" });

        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(activeLogTime),logdtformat.format(activeLogTime),"ACTIVE","","","","","","" });

//        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(idleLogTime),logdtformat.format(idleLogTime),"IDLE",String.valueOf((idleLogTime-activeLogTime)/1000) + "/" + dialDuration,String.valueOf(i),dialRptInterval,isSuccess,dialMaxFailure,dialType });


//        long sleepTime = (idleLogTime + dialRptInterval*1000) - System.currentTimeMillis();
//        if(sleepTime > 0) {
//            sleep((int) sleepTime);
//        }


        sleep(5000);

    }

    public void sleep(int mint) {
        try {
            Thread.sleep(mint);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void dialingPhone() {

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse("tel:" + dialNumber);
        intent.setData(uri);
        try {
            testedContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hangUp(){
        switch (model){
            case "MHA-AL00"://华为 mate9
                uiDevice.click(550,1600);
                Log.i("call","挂断电话");
                break;
            case"M836"://移动N2
                try {
                    UiObject ringOFFN2= uiDevice.findObject(new UiSelector().resourceId("com.android.contacts:id/floating_end_call_action_button").descriptionContains("结束通话"));
                    ringOFFN2.click();
                } catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    //创建数据库

    public void createSqlite() {

        Log.i("TelCase-----","now create sql");
        String DATABASE_PATH = "/sdcard/adbtestcase";
        String DATABASE_PATH_NAME = adbName + ".db";
        File dir = new File(DATABASE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dbfile = new File(DATABASE_PATH_NAME);

        if (!dbfile.exists()) {
            db = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH_NAME, null);
        } else {
            db = SQLiteDatabase.openDatabase(DATABASE_PATH_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        }
        db.execSQL("CREATE TABLE sendCallLog (TimeStamp VARCHAR, DateTime VARCHAR, Status VARCHAR, DurationRequired VARCHAR, Repeat VARCHAR, Interval VARCHAR, Succes VARCHAR, MaxFailure VARCHAR, CallType VARCHAR)");
    }
}

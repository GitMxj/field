package com.example.field.fieldtest;

import android.app.Instrumentation;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by wangshiqiandemac on 2018/5/3.
 */
@RunWith(AndroidJUnit4.class)
public class TelAnswerCase {

   private UiDevice uiDevice;
    private Instrumentation instrumentation;

    private static final String RELATIVE_LAYOUT = "android.widget.RelativeLayout";
    private static final String LINEAR_LAYOUT = "android.widget.LinearLayout";
    private static final String FRAME_LAYOUT = "android.widget.FrameLayout";

    private static final String PHONE_PACKAGE_1 = "com.android.phone";
    private static final String PHONE_PACKAGE_2 = "com.android.incallui";
    private static final String PHONE_PACKAGE_3 = "com.android.dialer";
    private static final String PHONE_PACKAGE_4 = "com.android.contacts";



    @Before
    public void setUp(){
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);


        Log.i("TelAnswer------","before");
    }

    @Test
    public void answerCall() throws RemoteException, UiObjectNotFoundException{


        Log.i("TelAnswer------","test start");
        long startTime = System.currentTimeMillis();


        int delayTime = 3;  //default receive delay time, 3 second

        long answerTime = startTime + delayTime*1000;
        String manufacturer = Tools.getDeviceManufacturer();
        String model = Tools.getDeviceModel();

        while(System.currentTimeMillis() < answerTime) {
            //如果屏幕关闭，则点亮屏幕
            if(!uiDevice.isScreenOn()) {
                uiDevice.wakeUp();
                System.out.println("wakeup");
            }
        }
        UiObject answerButton;
        UiObject object;
        switch(manufacturer) {
            case "Samsung":
            case "SAMSUNG":
            case "samsung":
                uiDevice.pressHome();

                break;
            case "htc":
            case "HTC":
                    object = uiDevice.findObject(new UiSelector().className(FRAME_LAYOUT));
                if (!object.exists()) {
                    object = uiDevice.findObject(new UiSelector().className(LINEAR_LAYOUT));
                    if (!object.exists()) {
                        object = uiDevice.findObject(new UiSelector().className(RELATIVE_LAYOUT));
                        if (!object.exists()) {
                            return;
                        }
                    }
                }
                switch(object.getPackageName()) {
                    case PHONE_PACKAGE_1:
                    case PHONE_PACKAGE_2:
                    case PHONE_PACKAGE_3:
                    case PHONE_PACKAGE_4:
                        answerButton = uiDevice.findObject(new UiSelector().resourceId("com.android.phone:id/cmd_bar_btn_1"));
                        while(!answerButton.exists()) {
                            sleep(50);

                            answerButton = uiDevice.findObject(new UiSelector().resourceId("com.android.phone:id/cmd_bar_btn_1"));
                        }
                        answerButton.click();
                        break;
                }
                break;
            case "Xiaomi": //MI
                switch(model) {
                    case "MI 4LTE": //MI4 LTE

                        object = uiDevice.findObject(new UiSelector().className(FRAME_LAYOUT));
                        if (!object.exists()) {
                            object = uiDevice.findObject(new UiSelector().className(LINEAR_LAYOUT));
                            if (!object.exists()) {
                                object = uiDevice.findObject(new UiSelector().className(RELATIVE_LAYOUT));
                                if (!object.exists()) {
                                    return;
                                }
                            }
                        }
                        switch(object.getPackageName()) {
                            case PHONE_PACKAGE_1:
                            case PHONE_PACKAGE_2:
                            case PHONE_PACKAGE_3:
                            case PHONE_PACKAGE_4:
                                answerButton = uiDevice.findObject(new UiSelector().resourceId("com.android.incallui:id/incoming_answer"));
                                answerButton.dragTo(uiDevice.getDisplayWidth()/2, uiDevice.getDisplayHeight()/2, 3);
                                break;
                        }
                        break;
                }
                break;
            case "CMDC":  //chinamobile
                switch(model) {
                    case "M823":  //N1 max
                        uiDevice.click(880, 408);
                        break;
                    case "M821":  //N1
                        uiDevice.click(540, 160);
                        break;
                    case "M836"://N2
                        while (true) {
                            uiDevice.click(853, 400);
                            sleep(2000);
                        }


                }
                break;
            case "HUAWEI":
            case "huawei":

                Log.i("TelAnswer------","test   huawei ");
                switch(model) {
                    case "MHA-AL00":  //华为MATE9
                        Log.i("TelAnswer------","test   huawei mate9");

                        while (true) {
                            sleep(2000);
                            while (true) {
                                UiObject answerBtn = uiDevice.findObject(new UiSelector().resourceId("com.android.incallui:id/answerbutton"));
                                if (answerBtn.exists()) {
                                    answerBtn.click();
                                    break;
                                }

                                sleep(1000);
                            }

                            sleep(1000);
                        }

                        //--start 20181022 mxj
                       /* object = uiDevice.findObject(new UiSelector().className(FRAME_LAYOUT));
                        if (!object.exists()) {
                            object = uiDevice.findObject(new UiSelector().className(LINEAR_LAYOUT));
                            if (!object.exists()) {
                                object = uiDevice.findObject(new UiSelector().className(RELATIVE_LAYOUT));
                                if (!object.exists()) {
                                    return;
                                }
                            }
                        }
                        switch(object.getPackageName()) {
                            case PHONE_PACKAGE_1:
                            case PHONE_PACKAGE_2:
                            case PHONE_PACKAGE_3:
                            case PHONE_PACKAGE_4:
                                uiDevice.drag(uiDevice.getDisplayWidth()/2, 1634, 945, 1634, 1);
                                break;
                        }*/
                       //--end 20181022 mxj
//                        break;
                    default: //华为荣耀6

                        Log.i("TelAnswer------","test   huawei honor 6");
                        object = uiDevice.findObject(new UiSelector().className(FRAME_LAYOUT));
                        if (!object.exists()) {
                            object = uiDevice.findObject(new UiSelector().className(LINEAR_LAYOUT));
                            if (!object.exists()) {
                                object = uiDevice.findObject(new UiSelector().className(RELATIVE_LAYOUT));
                                if (!object.exists()) {
                                    return;
                                }
                            }
                        }
                        switch(object.getPackageName()) {
                            case PHONE_PACKAGE_1:
                            case PHONE_PACKAGE_2:
                            case PHONE_PACKAGE_3:
                            case PHONE_PACKAGE_4:
                                uiDevice.drag(uiDevice.getDisplayWidth()/2, 1598, 945, 1598, 1);
                                break;
                        }
                }
                break;
            case "ZTE":
                switch (model) {
                    case "ZTE B2015": //ZTE Axon MINI
                        object = uiDevice.findObject(new UiSelector().className(FRAME_LAYOUT));
                        if (!object.exists()) {
                            object = uiDevice.findObject(new UiSelector().className(LINEAR_LAYOUT));
                            if (!object.exists()) {
                                object = uiDevice.findObject(new UiSelector().className(RELATIVE_LAYOUT));
                                if (!object.exists()) {
                                    return;
                                }
                            }
                        }
                        switch(object.getPackageName()) {
                            case PHONE_PACKAGE_1:
                            case PHONE_PACKAGE_2:
                            case PHONE_PACKAGE_3:
                            case PHONE_PACKAGE_4:
                                answerButton = uiDevice.findObject(new UiSelector().text("向下滑动接听").resourceId("com.android.dialer:id/answer_notice"));
                                answerButton.dragTo(uiDevice.findObject(new UiSelector().resourceId("com.android.dialer:id/answer_icon")), 3);
                                break;
                        }
                        break;
                }
                break;
        }

    }

    public void sleep(int mint){
        try{
            Thread.sleep(mint);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}

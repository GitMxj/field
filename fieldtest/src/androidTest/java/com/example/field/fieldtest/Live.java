package com.example.field.fieldtest;

import android.app.Instrumentation;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
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
import java.util.Random;

/**
 * Created by wangshiqiandemac on 2018/4/26. dou yu for 4.1.0
 */
@RunWith(AndroidJUnit4.class)
public class Live {
    UiDevice uiDevice;
    Instrumentation instrumentation;
    int liveCount = 20;//播放次数
    SQLiteDatabase db = null;
    long duration2 = 10000;//播放时长
    int k = 1;
    long pausestart = 0;
    long pausestop = 0;
    SimpleDateFormat dateFormat;
    String fixedPath;
    String adbName;


    @Before
    public void setUp() throws Exception {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);


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
        // 播放时长//duration2
        duration2 = Long.parseLong(ob1.getString("Live_PlayDuration")) * 1000;
        // 播放次数
        liveCount = Integer.parseInt(ob1.getString("Live_PlayNum"));

        adbName = ob2.getString("logpath");
    }

    @Test
    public void runLive() throws UiObjectNotFoundException {
        createSqlite();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        while (true) {

            uiDevice.pressHome();
            uiDevice.pressHome();
            UiObject liveFish = uiDevice.findObject(new UiSelector().text("斗鱼直播"));
            liveFish.click();
            //处理刚打开斗鱼界面还处于上次测试直播房间的问题
            sleep(5000);
            for (int i = 0; i < 5; i++) {
                uiDevice.pressBack();
                sleep(500);
            }
            uiDevice.pressHome();
            uiDevice.pressHome();
            sleep(1000);
            uiDevice.pressHome();
            liveFish.clickAndWaitForNewWindow();
            sleep(20000);


            //版本更新#取消登陆
            UiObject updateLive = uiDevice.findObject(new UiSelector().resourceId("air.tv.douyu.android:id/update_btn").text("立即更新"));
            UiObject cancleUpdate = uiDevice.findObject(new UiSelector().resourceId("air.tv.douyu.android:id/cancel_btn"));
            if (updateLive.exists()) {
                cancleUpdate.click();
            }
            sleep(2000);
            UiObject liveLogin = uiDevice.findObject(new UiSelector().text("斗鱼账号登录"));
            UiObject cancleLogin = uiDevice.findObject(new UiSelector().resourceId("air.tv.douyu.android:id/close_btn"));
            if (liveLogin.exists()) {
                cancleLogin.click();
            }

            //首页加载是否成功


            int netTryCount = 1;

            boolean netTryFail = false;

            UiObject noNetContent = uiDevice.findObject(new UiSelector().textContains("无法获取网络内容"));

            UiObject loadAgain = uiDevice.findObject(new UiSelector().text("点击重试"));

            UiObject classify = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.lolChannel).textContains("英雄联盟"));

            while (noNetContent.exists()) {

                if (loadAgain.exists()) {
                    loadAgain.click();
                    sleep(10000);
                    netTryCount++;
                } else if (classify.exists() || !noNetContent.exists() || !loadAgain.exists()) {
                    break;
                }
                if (netTryCount > 6) {
                    netTryFail = true;
                    break;
                }

            }

            if (netTryFail) {
                uiDevice.pressBack();
                uiDevice.pressBack();
                uiDevice.pressBack();
                continue;
            }

            if (classify.exists()) {
                classify.click();
                sleep(5000);
            } else {
                uiDevice.pressBack();
                uiDevice.pressBack();
                uiDevice.pressBack();
                continue;
            }

            boolean roomTryFail = false;
            while (k <= liveCount) {
                cancleLogin = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.loginCancle));
                if (cancleLogin.exists()) {
                    cancleLogin.click();
                }
                UiObject videoConnection = uiDevice.findObject(new UiSelector().text("直播视频连接中..."));
                UiObject openRoomFail = uiDevice.findObject(new UiSelector().text("打开房间失败"));
                UiObject videoCaptureFail = uiDevice.findObject(new UiSelector().text("视频获取失败"));


                long videoStartTime = System.currentTimeMillis();// 开始时间

                int tryCount = 1;
                while (tryCount < 7) {
                    Random rand = new Random();
                    int randNum = rand.nextInt(3);
                    UiObject room = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.liveRoom).instance(randNum));
                    if (room.exists()) {
                        room.click();
                        break;
                    } else {
                        uiDevice.pressBack();
                        sleep(2000);
                        classify.click();
                        sleep(10000);
                    }
                    tryCount++;
//                    Log.i("live", "count:"+String.valueOf(tryCount));
                }

                if (tryCount > 6) {
                    uiDevice.pressBack();
                    uiDevice.pressBack();
                    uiDevice.pressBack();
                    uiDevice.pressBack();
                    roomTryFail = true;
                    break;
                }

                UiObject continuePlay = uiDevice.findObject(new UiSelector().resourceId("air.tv.douyu.android:id/wangsu_positive_btn").text("继续播放"));
                UiObject goOn = uiDevice.findObject(new UiSelector().resourceId("air.tv.douyu.android:id/positive_btn").text("继续"));
                if (continuePlay.waitForExists(10000)) {
                    continuePlay.click();
                }
                if (goOn.exists()) {
                    goOn.click();
                }

                long openstart = System.currentTimeMillis();
                if (videoConnection.waitUntilGone(duration2)) {

                    long opensuccess = System.currentTimeMillis();
                    if (openRoomFail.exists() || videoCaptureFail.exists()) {
                        // 失败

                        db.execSQL("INSERT INTO LiveLog2 VALUES (?, ?, ?, ?, ?, ?, ?)",
                                new Object[]{System.currentTimeMillis(), dateFormat.format(System.currentTimeMillis()),
                                        "com.Live", String.valueOf(k), "info", "Fail", ""});
                    } else {

                        db.execSQL("INSERT INTO LiveLog2 VALUES (?, ?, ?, ?, ?, ?, ?)",
                                new Object[]{System.currentTimeMillis(), dateFormat.format(System.currentTimeMillis()),
                                        "com.Live", String.valueOf(k), "info", "Success",
                                        Long.toString(opensuccess - openstart)});
                        while (System.currentTimeMillis() <= (videoStartTime + duration2)) {

                            // 卡顿出现的旋转圆圈
                            UiObject pause1 = uiDevice.findObject(
                                    new UiSelector().resourceId("air.tv.douyu.android:id/loading_progress")
                                            .className("android.widget.ProgressBar"));

                            UiObject playFail = uiDevice.findObject(new UiSelector().textContains("播放失败，网络连接断开"));
                            videoCaptureFail = uiDevice.findObject(new UiSelector().text("视频获取失败"));
                            openRoomFail = uiDevice.findObject(new UiSelector().text("打开房间失败"));

                            if (pause1.exists()) {

                                pausestart = System.currentTimeMillis();

                                if (pause1.waitUntilGone((videoStartTime + duration2) - pausestart)) {
                                    pausestop = System.currentTimeMillis();
                                } else {
                                    pausestop = videoStartTime + duration2;
                                }

                                db.execSQL("INSERT INTO LiveLog1 VALUES (?, ?, ?, ?, ?, ?, ?)",
                                        new Object[]{System.currentTimeMillis(),
                                                dateFormat.format(System.currentTimeMillis()), "com.Live", "0", "info", "",
                                                Long.toString(pausestop - pausestart)});
                            }

                            if (openRoomFail.exists() || videoCaptureFail.exists() || playFail.exists()) {
//							getUiDevice().pressBack();
                                sleep(3000);
                                break;
                            }

                        }
                    }
                } else {
                    db.execSQL("INSERT INTO LiveLog2 VALUES (?, ?, ?, ?, ?, ?, ?)",
                            new Object[]{System.currentTimeMillis(), dateFormat.format(System.currentTimeMillis()),
                                    "com.Live", String.valueOf(k), "info", "OutOfTime", String.valueOf(1000 * 60 * 5)});
                }
                UiObject loginCancelButton = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.loginCancle));


                liveLogin = uiDevice.findObject(new UiSelector().text("斗鱼账号登录"));
                if (liveLogin.exists()) {
                    loginCancelButton.click();
                }
                uiDevice.pressBack();
                sleep(1000);

                k++;
            }
            uiDevice.pressBack();
            uiDevice.pressBack();
            uiDevice.pressBack();
            uiDevice.pressBack();
            uiDevice.pressHome();
            if (roomTryFail) {
                continue;
            }
            if (k >= liveCount) {
                break;
            }


        }


    }


    //创建数据库

    public void createSqlite() {

        String DATABASE_PATH = "/sdcard/adbtestcase";
        String DATABASE_PATH_NAME = adbName+".db";
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
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS LiveLog1 (TimeStamp VARCHAR, DateTime VARCHAR, Status VARCHAR, Seq VARCHAR, Info VARCHAR, Result VARCHAR, DeltaTime VARCHAR)");
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS LiveLog2 (TimeStamp VARCHAR, DateTime VARCHAR, Status VARCHAR, Seq VARCHAR, Info VARCHAR, Result VARCHAR, DeltaTime VARCHAR)");
    }

    public void sleep(int mint) {
        try {
            Thread.sleep(mint);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

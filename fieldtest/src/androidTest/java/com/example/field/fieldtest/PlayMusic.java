package com.example.field.fieldtest;

import android.app.Instrumentation;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.view.KeyEvent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wangshiqiandemac on 2018/5/18.  百度版本6.2.0.1
 */

public class PlayMusic {
    UiDevice uiDevice;
    Instrumentation instrumentation;
    int playDuration = 60; //default play music time is 60 seconds
    String fixedPath ;
    String adbName;

    @Before
    public void setUp()throws Exception{
        instrumentation = InstrumentationRegistry.getInstrumentation();
        uiDevice = UiDevice.getInstance(instrumentation);

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
        playDuration = Integer.parseInt(ob1.getString("Music_Play_Duration"));
    }
    @Test
    public void runMusic() throws UiObjectNotFoundException {

        uiDevice.pressHome();
        uiDevice.pressHome();

        UiObject baiDuMusic = uiDevice.findObject(new UiSelector().textContains(GlobalConfig.musicName));
        baiDuMusic.click();

        sleep(8000);

        //更新提示
       UiObject updatePrompt= uiDevice.findObject(new UiSelector().resourceId("com.ting.mp3.android:id/dialog_common_title").text("更新提示"));

       UiObject updateCancle= uiDevice.findObject(new UiSelector().resourceId("com.ting.mp3.android:id/dialog_common_cancel").text("取消"));

        if(updatePrompt.exists()){
            updateCancle.click();
        }
        //点击本地歌曲
        UiObject localMusic = uiDevice.findObject(new UiSelector().text("本地歌曲"));
        UiObject mine = uiDevice.findObject(new UiSelector().text("我的音乐"));
        if(!localMusic.exists()) {
                mine.clickAndWaitForNewWindow();

        }
        localMusic.clickAndWaitForNewWindow();

        UiObject songTrackName = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.currentPlayMusic));
        //如果当前播放的音乐为目标音乐 则直接点击播放 如果不是 在歌曲列表里查找 然后播放
        if(songTrackName.getText().equalsIgnoreCase("song")) {  //current song to play is "song.mp3"
            UiObject startBtn = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.playButton));
            startBtn.click();
        } else {
            //
            UiObject songItem = uiDevice.findObject(new UiSelector().text(GlobalConfig.targetMusicName).resourceId(GlobalConfig.targetMusicId));
            if(!songItem.exists()) {
                UiScrollable songList = new UiScrollable(new UiSelector().scrollable(true));
                songList.scrollToBeginning(50);
                while(songItem.exists() == false) {
                    songList.scrollForward();

                }
                UiObject frongImage = uiDevice.findObject(new UiSelector().resourceId("com.ting.mp3.android:id/mb_image_front"));
                if((frongImage.getBounds().top - songItem.getBounds().bottom) <= 50) {
                    songList.setSwipeDeadZonePercentage(0.3);
                    songList.scrollForward();
                }
            }
            songItem.click();

        }

        //开发音量
        for(int j = 1; j <= 20; j++) {
            uiDevice.pressKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
        }

        sleep(playDuration*1000);

        UiObject stopBtn = uiDevice.findObject(new UiSelector().resourceId(GlobalConfig.playButton));
        stopBtn.click();
        for (int i = 1; i <= 3; i++) {
            //if not exit baidu music ui, then continue press back
            UiObject bmObject = uiDevice.findObject(new UiSelector().packageName("com.ting.mp3.android"));
            if(bmObject.exists()) {
                uiDevice.pressBack();
            }
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

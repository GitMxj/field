package com.example.field.fieldtest;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.sauronsoftware.ftp4j.FTPDataTransferListener;

/**
 * Created by wangshiqiandemac on 2018/5/18.
 */
@RunWith(AndroidJUnit4.class)
public class FTPDownload {
    String host = "117.136.182.132";//ftp地址
    String DATABASE_PATH_NAME;
    SQLiteDatabase db = null;
    int processCount = 3;
    String fixedPath;
    String adbName;
    @Before
    public void setUp() throws Exception {


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

        host = ob1.getString("FTP_Download_DestIP").toString();
        processCount = Integer.parseInt(ob1.getString("FTP_Download_ProcessNum"));
        adbName = ob2.getString("logpath");


    }
    @Test
    public void runFTPDownload() throws IOException, JSONException {
        createSqlite();
        File folder = new File("/sdcard/adbtestcase/downloaddir");
        Tools.delAllFile("/sdcard/adbtestcase/downloaddir");
        if (!folder.exists()) {
            folder.mkdir();
        } else {
            Tools.delAllFile("/sdcard/adbtestcase/downloaddir");
        }
        sleep(1000 * 2);

        DownUtil downUtil=new DownUtil(host,null,processCount);
        downUtil.downloadFail();

        // 循环记录log
        int seq = 1;
        int duration = 1000 * 60 * 60 * 10;
        long aftertime;
        double startsize = 0;
        double aftersize = 0;
        long speed2 = 0;
        long speed;
        long startTime = System.currentTimeMillis();
        while (seq <= duration) {

            aftersize = Tools.getDirSize(new File("/sdcard/adbtestcase/downloaddir")); // 每次循环得到指定文件的大小

            aftertime = System.currentTimeMillis(); // 时间戳

            if (aftersize < startsize) { // 防止重传出现负数的问题
                startsize = 0;
            }

            float sizeDifference = (float) (aftersize - startsize);
            float timeDifference = (float) (aftertime - startTime);
            if (aftertime == startTime) {
                speed = (long) (sizeDifference / (timeDifference + 1) * 1000);
            } else {
                speed = (long) (sizeDifference / timeDifference * 1000);
            }

            db.execSQL(
                    "INSERT INTO FTPDownLog VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[] { aftertime, "com.FTPDownload",
                            String.valueOf(seq), "info", speed, speed2,
                            "Byte/s" });

            seq = seq + 1;
            startTime = aftertime;
            startsize = aftersize;
            sleep(1000);
        }

        sleep(duration);

        db.execSQL("INSERT INTO FTPDownLog VALUES (?, ?, ?, ?, ?, ?, ?)",
                new Object[] { System.currentTimeMillis(), "com.FTPDownload",
                        "0", "info", "DownloadFinished", "UploadFinished", "" });


    }


    //创建数据库

    public void createSqlite(){

        String DATABASE_PATH = "/sdcard/adbtestcase";
        DATABASE_PATH_NAME = adbName+".db";
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
        db.execSQL("CREATE TABLE IF NOT EXISTS FTPDownLog (mili VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, speed VARCHAR, upspeed VARCHAR, danwei VARCHAR)");
    }

    public void sleep(int mint){
        try{
            Thread.sleep(mint);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    /**
     * ftp 下载监听器 aborted :下载中止时调用 completed:下载完成时调用 failed:下载失败时调用 started:下载开始时调用
     *
     * @author Administrator
     *
     */
    class dllistener implements FTPDataTransferListener {

        public JSONObject ReadJsonFile(String FilePathAndName) throws IOException,
                JSONException {

            File file = new File(FilePathAndName);// 读取json文件
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            StringBuilder jsonString = new StringBuilder();
            String line = reader.readLine();
            jsonString.append(line);
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();
            JSONObject json = array.getJSONObject(0);
            return json;
        }

        @Override
        public void aborted() {
            try {

                SQLiteDatabase db = SQLiteDatabase.openDatabase(adbName+ ".db", null,
                        SQLiteDatabase.OPEN_READWRITE);
                db.execSQL("INSERT INTO FTPDownLog VALUES (?, ?, ?, ?, ?, ?, ?)",
                        new Object[] { System.currentTimeMillis(),
                                "com.FTPDownload", "0", "info", "DownloadAborted",
                                "", "" });

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        @Override
        public void completed() {

        }

        @Override
        public void failed() {

            try {

                SQLiteDatabase db = SQLiteDatabase.openDatabase(adbName + ".db", null,
                        SQLiteDatabase.OPEN_READWRITE);
                db.execSQL("INSERT INTO FTPDownLog VALUES (?, ?, ?, ?, ?, ?, ?)",
                        new Object[] { System.currentTimeMillis(),
                                "com.FTPDownload", "0", "info", "DownloadFailed",
                                "", "" });

            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        @Override
        public void started() {

        }

        @Override
        public void transferred(int arg0) {

        }
    }

}



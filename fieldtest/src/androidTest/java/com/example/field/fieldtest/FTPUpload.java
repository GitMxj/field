package com.example.field.fieldtest;

import android.database.sqlite.SQLiteDatabase;
import android.net.TrafficStats;
import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

/**
 * Created by wangshiqiandemac on 2018/5/18.
 */
@RunWith(AndroidJUnit4.class)
public class FTPUpload  {

    String host = "117.136.182.132";//上传地址

    long fileSize = 200;//文件大小

    // 上传时限
    int uploadduraion = 100000;

    SQLiteDatabase db = null;

    boolean flag = true;

    boolean clientConnect = false;

    String DATABASE_PATH_NAME;
    String filePath = "/sdcard/adbtestcase/ftpupload";
    String fileName = "update";
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

        host = ob1.getString("FTP_Upload_DestIP").toString();
        // 文件大小
        fileSize = Integer.parseInt(ob1.getString("FTP_Upload_FileName").toString());
        // 上传时限
        uploadduraion = Integer.parseInt(ob1.getString(
                "FTP_Upload_Duration").toString());
        adbName = ob2.getString("logpath");


    }

    @Test
    public void runFtpUpload() {

        fileName = "update"+System.currentTimeMillis()+".rar";

        Tools.delFolder("/sdcard/adbtestcase/ftpupload");

        //生成上传文件
        new CreateFile().createFile(filePath, fileName, fileSize, CreateFile.FileUnit.KB);

        // 等待一段时间，待文件生成
        sleep(1000 * 30);

        createSqlite();
        //开启线程 执行上传操作
        new Thread() {
            public void run() {
                try {
                    while (!clientConnect) {
                        Log.i("upload", "开启线程");
                        FTPClient loginClient = loginClient();
                        clientConnect = loginClient.isConnected();

                        Log.i("upload", String.valueOf(clientConnect));

                        if (clientConnect) {
                            uploadFile(loginClient);
                            break;
                        }
                        sleep(2000);
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            ;
        }.start();
        //循环记录log日志
        long nowTotalTxBytes = 0, lastTotalTxBytes = 0, nowTimeStamp, lastTimeStamp = System
                .currentTimeMillis();
        int seq = 1;
        long speed;
        while (seq <= uploadduraion && flag) {
            nowTotalTxBytes = TrafficStats.getTotalTxBytes();
            if (seq == 1) {
                lastTotalTxBytes = nowTotalTxBytes;
            }
            nowTimeStamp = System.currentTimeMillis();

            float timeDifference = (float) (nowTimeStamp - lastTimeStamp);
            float txBytesDifference = (float) (nowTotalTxBytes - lastTotalTxBytes);

            if (nowTimeStamp == lastTimeStamp) {
                speed = (long) (txBytesDifference * 1000 / (timeDifference + 1));// 毫秒转换
            } else {
                speed = (long) (txBytesDifference * 1000 / (timeDifference));// 毫秒转换
            }

            db.execSQL("INSERT INTO FTPUpLog VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{System.currentTimeMillis(), "com.FTPUpload",
                            String.valueOf(seq), "info", String.valueOf(speed),
                            String.valueOf(System.currentTimeMillis()),
                            "Byte/s"});
            sleep(1000);
            lastTimeStamp = nowTimeStamp;
            lastTotalTxBytes = nowTotalTxBytes;
            seq = seq + 1;
            sleep(1000);

        }


    }

    //创建数据库

    public void createSqlite() {

        String DATABASE_PATH = "/sdcard/adbtestcase";
        DATABASE_PATH_NAME = adbName+".db";
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
        db.execSQL("CREATE TABLE IF NOT EXISTS FTPUpLog (mili VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, speed VARCHAR, upspeed VARCHAR, danwei VARCHAR)");
    }

    private FTPClient loginClient() throws FTPDataTransferException,
            FTPAbortedException {

        FTPClient client = new FTPClient();
        try {
            client.connect(host, 21);
            client.login("admin", "jizhandarongliang");
            client.changeDirectory("ftpupload");

        } catch (IllegalStateException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {

            e.printStackTrace();
        } catch (FTPException e) {

            e.printStackTrace();
        }

        return client;

    }


    public void uploadFile(FTPClient client) throws IllegalStateException,
            FileNotFoundException, IOException, FTPIllegalReplyException,
            FTPException, FTPDataTransferException, FTPAbortedException {

        File file = new File("/sdcard/adbtestcase/ftpupload/");
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                for (final File f : children) {
                    File uploadFile = new File("/sdcard/adbtestcase/ftpupload/"
                            + f.getName());
                    client.upload(uploadFile, new MyTransferListener(null,
                            null, client));
                }
            }
        }

    }

    public void sleep(int mint) {
        try {
            Thread.sleep(mint);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //ftp上传监听器
    class MyTransferListener implements FTPDataTransferListener {

        long log2start = 0;
        long log2finish = 0;
        FTPClient client;
        boolean connect = false;

//        public JSONObject ReadJsonFile(String FilePathAndName)
//                throws IOException, JSONException {
//
//            File file = new File(FilePathAndName);// 读取json文件
//            FileReader fr = new FileReader(file);
//            BufferedReader reader = new BufferedReader(fr);
//            StringBuilder jsonString = new StringBuilder();
//            String line = reader.readLine();
//            jsonString.append(line);
//            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
//                    .nextValue();
//            JSONObject json = array.getJSONObject(0);
//            return json;
//        }

        public MyTransferListener(Logger log1, Logger log2, FTPClient client) {
            this.client = client;
        }

        @Override
        public void aborted() {

            db = SQLiteDatabase.openDatabase(DATABASE_PATH_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("INSERT INTO FTPUpLog VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{System.currentTimeMillis(),
                            "com.FTPUpload", "0", "info",
                            "FTPUploadAborted", "", ""});
            Log.i("fileUpload", "文件上传终止");
        }

        @Override
        public void completed() {
            Log.i("fileUpload", "文件上传完成");
            log2finish = System.currentTimeMillis();
            if ((log2finish - log2start) > 0) {

            }
            flag = false;
            try {
                logout(client);
            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        @Override
        public void failed() {

            SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("INSERT INTO FTPUpLog VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{System.currentTimeMillis(),
                            "com.FTPUpload", "0", "info",
                            "FTPUploadFailed", "", ""});

            Log.i("fileUpload", "文件上传失败");
            new Thread() {
                public void run() {
                    while (!connect) {
                        Log.i("fileUpload", "尝试重新连接上传");

                        FTPClient LClient;
                        try {
                            sleep(10000);
                            LClient = loginClient();
                            connect = LClient.isConnected();
                            if (connect) {
                                uploadFile(LClient);
                                break;
                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }

                }

                ;
            }.start();

        }

        @Override
        public void started() {
            log2start = System.currentTimeMillis();
            Log.i("fileUpload", "文件上传开始");


        }

        @Override
        public void transferred(int arg0) {
            Log.i("fileUpload", "文件上传字节" + String.valueOf(arg0));
        }

    }

    // 退出登录 断开连接
    private void logout(FTPClient client) throws Exception {
        if (client != null) {
            try {

                // 有些FTP服务器未实现此功能，若未实现则会出错

                client.logout(); // 退出登录
            } catch (FTPException fe) {
            } catch (Exception e) {
                throw e;
            } finally {
                if (client.isConnected()) { // 断开连接
                    client.disconnect(false);
                }
            }
        }
    }

}

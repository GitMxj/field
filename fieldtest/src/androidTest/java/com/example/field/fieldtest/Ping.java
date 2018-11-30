package com.example.field.fieldtest;

import android.app.Instrumentation;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.text.format.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangshiqiandemac on 2018/5/22.
 */
@RunWith(AndroidJUnit4.class)
public class Ping {

    UiDevice uiDevice;
    Instrumentation instrumentation;
    String url = "192.168.2.19";   //default ping dest address
    int count = 30;	//default ping package number, 10 packages
    int length = 1500;	//default ping package length, 32 byte
    int pingtime = 10;	//default ping duration time, 10 second
    String pingtype = "count";  //default ping type, type: count/permanent
    boolean isLastPingFinish = true;  //whether the last ping operation is finished
    String cmd = "";
    int seq;  //icmp packet sequence number
    boolean isFinish = false;  //whether end the total ping operation
    String fixedPath ;
    String adbName;
    Timer timer = new Timer();
    Message msg = new Message();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(isLastPingFinish == true) {
                msg.what = 1;
            }
        }

    };

    @Before
    public void setUp() throws Exception{
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
        JSONObject ob2= new JSONObject(st[1]);


        url = ob1.getString("Ping_Send_DestIP");  //get user input url
        count = Integer.parseInt(ob1.getString("Ping_Send_Packages"));  //get user input ping count
        length = Integer.parseInt(ob1.getString("Ping_Send_PkgSize")); //get user input ping package length
        pingtime = Integer.parseInt(ob1.getString("Ping_Send_Time"));
        pingtype = ob1.getString("Ping_Send_Type");

        adbName = ob2.getString("logpath");
    }



    @Test
    public void runPing() throws RemoteException{



        if (uiDevice.isScreenOn() == false) {
            uiDevice.wakeUp();

        }
        Log.i("ping","进入到ping测试");
        //-----------------
        cmd = "ping -c 1 -s " + String.valueOf(length - 8) + " " + url;

        seq = 0;
        msg.what = 0;


        PingThread pingThread = new PingThread();
        pingThread.start();

        if(pingtype.equals("count")) {
            //send a certain number of the icmp packages, e.g. 10 packages
            timer.scheduleAtFixedRate(task, 0, 1000);
            while(seq < count) {
                //wait
            }
            isFinish = true;
            timer.cancel();
        } else {
            //send the icmp packages for a certain time, e.g. 10 seconds
            long endTime = System.currentTimeMillis() + pingtime * 1000;
            timer.scheduleAtFixedRate(task, 0, 1000);
            while(System.currentTimeMillis() < endTime) {
                //wait
            }
            isFinish = true;
            timer.cancel();
        }
    }



    class PingThread extends Thread {

        @Override
        public void run() {

            //数据库创建
            SQLiteDatabase db = null;

            try {

                String DATABASE_PATH = "/sdcard/adbtestcase";
                String DATABASE_PATH_NAME = adbName+".db";
                File dir = new File(DATABASE_PATH);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                Log.i("ping","创建数据哭");
                db =  SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH_NAME, null);

                db.execSQL("CREATE TABLE IF NOT EXISTS ping (timeStamp VARCHAR, dataTime VARCHAR, url VARCHAR, seq VARCHAR, length VARCHAR, delayTime VARCHAR, status VARCHAR, count VARCHAR, pingtime VARCHAR, pingtype VARCHAR)");

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }



            // TODO Auto-generated method stub
            while(!isFinish) {

                if(msg.what == 1) {
                    msg.what = 0;
                    isLastPingFinish = false;  //start current ping, 1 package per times
                    try {
                        Time time = new Time();
                        time.setToNow();
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String dataTime = Tools.timeStamp2DateTime(time, false);
                        Process p = Runtime.getRuntime().exec(cmd);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        boolean isEchoPING = false;  //display "PING ..." line
                        boolean isEchoResponce = false;  //display like "64 bytes from ..." line
                        boolean isWaitForDNS = false;  //wait for DNS responce once for 4 second
                        String line;
                        String[] sa;
                        String status = "";
                        String delayTime = "";
                        while(!isEchoPING) {
                            line = reader.readLine();

                            Log.i("ping",line);

                            try {
                                sa = line.split("\\s+");
                                Log.i("ping", String.valueOf(sa[0]));
                                if(sa[0].equals("PING")) {
                                    Log.i("ping", "1111111");
                                    isEchoPING = true;
                                    sa = null;
                                } else if(line.toString().contains("Network is unreachable")) {//无网络时无echo，读取reader为null，显示内容为系统生成获取不到
                                    isEchoPING = true;
                                    isEchoResponce = true;
                                    status = "NoNetwork";  //目前获取不到此状态
                                    //System.out.println("NoNetwork");
                                } else if(line.toString().contains("unknown host")) {//同无网络时情况
                                    isEchoPING = true;
                                    isEchoResponce = true;
                                    status = "DNSErr";  //目前获取不到此状态
                                }
                            } catch (NullPointerException e) {
                                if (isWaitForDNS == false) {
                                    try {
                                        Thread.sleep(4000);
                                    } catch (InterruptedException e1) {
                                        // TODO Auto-generated catch block
                                        //e1.printStackTrace();
                                    }
                                    isWaitForDNS = true;
                                } else {
                                    isEchoPING = true;
                                    isEchoResponce = true;
                                    status = "error";  //无网络或DNS无法解析
                                }
                            }
                        }
                        while(!isEchoResponce) {
                            line = reader.readLine();
                            if(line == null) {
                                continue;
                            }
                            Log.i("ping", String.valueOf(line.toString().trim().length()));
                            if(line.toString().trim().length() != 0) {
                                //not a empty line
                                sa = line.split("\\s+");
                                try {
                                    int pkgsize = Integer.parseInt(sa[0]);  //检查是否合法数字，ping成功时返回的信息开头是数字，表示包长
                                    delayTime = sa[6].substring(5);
                                    status = "success";
                                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                    status = "error"; //Destination Net Unreachable
                                }
                                isEchoResponce = true;
                            } else {
                                //empty line
                                isEchoResponce = true;
                                status = "timeout";
                            }
                        }

                        seq++;

                        db.execSQL("INSERT INTO ping VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{timeStamp,dataTime,url,String.valueOf(seq),String.valueOf(length),delayTime,status,String.valueOf(count),String.valueOf(pingtime),pingtype});
                        reader.close();
                        p.destroy();
                    } catch (IOException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }



                    isLastPingFinish = true;
                }
            }

        }
    }

}

package com.example.field.fieldtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;





import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

public class DownUtil {
	// private String i;
	private String serverip;
	private Logger FTPDownLog1;
	int processnum;
	public DownUtil(String serverip) {

		this.serverip = serverip;
	}
	public DownUtil(String serverip, Logger FTPDownLog1,int processnum) {
		this.serverip = serverip;
		this.FTPDownLog1 = FTPDownLog1;
		this.processnum=processnum;
	}


	public void downloadFail() throws IOException, JSONException {

		
		if(processnum == 1){
			downloadFile1();
		}
		if(processnum == 2){
			downloadFile1();
			downloadFile2();
		}
		if(processnum == 3){
			downloadFile1();
			downloadFile2();
			downloadFile3();
		}
		if(processnum == 4){
			downloadFile1();
			downloadFile2();
			downloadFile3();
			downloadFile4();
		}
		if(processnum == 5){
			downloadFile1();
			downloadFile2();
			downloadFile3();
			downloadFile4();
			downloadFile5();
		}
		
		/*downloadTestFile1(getClient());
		downloadTestFile2(getClient());
		downloadTestFile3(getClient());
		downloadTestFile4(getClient());*/
		
	}

	private void downloadFile5() {
		// TODO Auto-generated method stub
		boolean download5 = false;
		while(!download5){
			boolean downloadTestFile5 = downloadTestFile5(getClient());
			if(downloadTestFile5){
				download5 =true;
			}else{
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Log.i("ftp4jj", "登录失败  再次登录");
			
		}
	}

	private void downloadFile4() {
		// TODO Auto-generated method stub
		
		boolean download4 = false;
		while(!download4){
			boolean downloadTestFile4 = downloadTestFile4(getClient());
			if(downloadTestFile4){
				download4 =true;
			}else{
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

	private void downloadFile3() {
		// TODO Auto-generated method stub
		
		boolean download3 = false;
		while(!download3){
			boolean downloadTestFile3 = downloadTestFile3(getClient());
			if(downloadTestFile3){
				download3 =true;
			}else{
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

	private void downloadFile2() {
		// TODO Auto-generated method stub
		
		boolean download2 = false;
		while(!download2){
			boolean downloadTestFile2 = downloadTestFile2(getClient());
			if(downloadTestFile2){
				download2 =true;
			}else{
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

	private void downloadFile1() {
		// TODO Auto-generated method stub
		
		boolean download1 = false;
		while(!download1){
			boolean downloadTestFile1 = downloadTestFile1(getClient());
			if(downloadTestFile1){
				download1 =true;
			}else{
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private boolean downloadTestFile5(final FTPClient client) {
		boolean flag = true;
		if(client.isConnected()){
			flag = true;
		}else{
			flag = false;
		}
		new Thread() {
			public void run() {
				try {
					client.download("test5.rar", new File(
							"/sdcard/adbtestcase/downloaddir/" + "test5.rar"),
							new MyTest5Listener());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		return flag;
	}

	private boolean downloadTestFile4(final FTPClient client) {
		boolean flag = true;
		if(client.isConnected()){
			flag = true;
		}else{
			flag = false;
		}
		new Thread() {
			public void run() {
				try {
					client.download("test4.rar", new File(
							"/sdcard/adbtestcase/downloaddir/" + "test4.rar"),
							new MyTest4Listener());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		return flag;
	}

	private boolean downloadTestFile3(final FTPClient client) {
		boolean flag = true;
		if(client.isConnected()){
			flag = true;
		}else{
			flag = false;
		}
		new Thread() {
			public void run() {
			
				try {
					client.download("test3.rar", new File(
							"/sdcard/adbtestcase/downloaddir/" + "test3.rar"),
							new MyTest3Listener());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		return flag;

	}

	private boolean downloadTestFile2(final FTPClient client) {
		boolean flag = true;
		if(client.isConnected()){
			flag = true;
		}else{
			flag = false;
		}
		new Thread() {
			public void run() {
				
				try {
					client.download("test2.rar", new File(
							"/sdcard/adbtestcase/downloaddir/" + "test2.rar"),
							new MyTest2Listener());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		return flag;

	}

	private boolean downloadTestFile1(final FTPClient client) {
		boolean flag = true;
		if(client.isConnected()){
			flag = true;
		}else{
			flag = false;
		}
		new Thread() {
			public void run() {
				
				try {
					client.download("test1.rar", new File(
							"/sdcard/adbtestcase/downloaddir/" + "test1.rar"),
							new MyTest1Listener());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPIllegalReplyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPDataTransferException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FTPAbortedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		return flag;

	}

	private FTPClient getClient() {
		
		FTPClient client = new FTPClient();
		try {
			client.connect(serverip, 21);
			client.login("admin", "jizhandarongliang");
			client.changeDirectory("keyan");
			
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

	class MyTest1Listener implements FTPDataTransferListener {
		
		@Override
		public void aborted() {
			// TODO Auto-generated method stub
			writeLogAbort();
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void failed() {

			// TODO Auto-generated method stub
			writeLog();
			
			boolean connected = false;
			while(!connected){
				Log.i("ftp4jj", "1111111111111111111111111");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FTPClient client = getClient();
				downloadTestFile1(client);
				connected = client.isConnected();
				Log.i("ftp4jj", "client"+String.valueOf(connected));
			}
			

		}

		@Override
		public void started() {
			// TODO Auto-generated method stub

		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub

		}

	}

	class MyTest2Listener implements FTPDataTransferListener {

		@Override
		public void aborted() {
			
			writeLogAbort();
		}

		@Override
		public void completed() {
			

		}

		@Override
		public void failed() {
			
			writeLog();
			boolean connected = false;
			while(!connected){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				FTPClient client = getClient();
				downloadTestFile2(client);
				connected = client.isConnected();
			}

		}

		@Override
		public void started() {
			

		}

		@Override
		public void transferred(int arg0) {
			

		}

	}

	class MyTest3Listener implements FTPDataTransferListener {

		@Override
		public void aborted() {
			
			writeLogAbort();
		}

		@Override
		public void completed() {
		

		}

		@Override
		public void failed() {
			
			writeLog();
			boolean connected = false;
			while(!connected){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				FTPClient client = getClient();
				downloadTestFile3(client);
				connected = client.isConnected();
			}

		}

		@Override
		public void started() {
			

		}

		@Override
		public void transferred(int arg0) {
			

		}

	}

	class MyTest4Listener implements FTPDataTransferListener {

		@Override
		public void aborted() {
			
			writeLogAbort();
		}

		@Override
		public void completed() {
			

		}

		@Override
		public void failed() {

			
			writeLog();
			
			boolean connected = false;
			while(!connected){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FTPClient client = getClient();
				downloadTestFile4(client);
				connected = client.isConnected();
			}

		}

		@Override
		public void started() {
			// TODO Auto-generated method stub

		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub

		}

	}

	class MyTest5Listener implements FTPDataTransferListener {

		@Override
		public void aborted() {
			// TODO Auto-generated method stub
			writeLogAbort();
		}

		@Override
		public void completed() {
			// TODO Auto-generated method stub

		}

		@Override
		public void failed() {
			// TODO Auto-generated method stub
			writeLog();
			boolean connected = false;
			while(!connected){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FTPClient client = getClient();
				downloadTestFile5(client);
				connected = client.isConnected();
			}

		}

		@Override
		public void started() {
			// TODO Auto-generated method stub

		}

		@Override
		public void transferred(int arg0) {
			// TODO Auto-generated method stub

		}

	}

	public void writeLog() {
		FTPDownLog1.info("DownloadFailed,");
	}

	public void writeLogAbort() {
		FTPDownLog1.info("DownloadAborted,");
	}
}

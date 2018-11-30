package com.example.field.fieldtest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.os.Build;
import android.text.format.Time;
import android.util.Log;

public class Tools {
	private Process mProcess;

	public Process getProcess() {
		return mProcess;
	}

	public Tools() {
		try {
			mProcess = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			mProcess = null;
		}
	}

	public int execShellCMD(String[] s, String TAG) throws IOException,
			InterruptedException {
		int status = -1;
		if (s.length != 0) {
			// Process p = Runtime.getRuntime().exec(s[0]);
			// mProcess = p;
			OutputStream outputStream = mProcess.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(
					outputStream);
			int i = 0;
			while (i <= s.length - 1) {
				dataOutputStream.writeBytes(s[i]);
				i = i + 1;
			}
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
			status = mProcess.waitFor();
			if (status != 0) {
				Log.w(TAG, "Shell command exec ERROR!");
			}
		}
		return status;
	}

	public String getPID(String processName) throws IOException,
			InterruptedException {
		String pid = null;
		final Process process = Runtime.getRuntime().exec("ps " + processName);
		final InputStreamReader inputStream = new InputStreamReader(
				process.getInputStream());
		final BufferedReader reader = new BufferedReader(inputStream);
		String line = null;
		try {
			int read;
			final char[] buffer = new char[4096];
			final StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			// Waits for the command to finish.
			process.waitFor();
			// no need to destroy the process since waitFor() will wait until
			// all subprocesses exit
			line = output.toString();
		} finally {
			try {
				reader.close();
				inputStream.close();
				reader.close();
			} catch (Exception e) {

			}
		}
		String[] rows = line.split("\\n");
		if (rows[0].startsWith("USER")) {
			final String row = rows[1];
			final String[] values_item = row.split("\\s+");
			int itemNum = 1; // second column contains PID
			if (values_item[itemNum].length() > 0) {
				pid = values_item[itemNum];
			}
		}
		return pid;
	}

	public void killProcess(String processName) throws IOException,
			InterruptedException {
		Process sh = null;
		DataOutputStream os = null;
		String pid = null;

		pid = getPID(processName);

		if (pid != null) {
			try {
				sh = Runtime.getRuntime().exec("su");
				os = new DataOutputStream(sh.getOutputStream());
				final String Command = "kill -9 " + pid + "\n";
				os.writeBytes(Command);
				os.flush();
				sh.waitFor();
			} finally {
				try {
					os.close();
				} catch (IOException e) {
				}
				sh.destroy();
			}
		}
	}

	public static void sleep(int milisecond) throws InterruptedException {
		try {
			Thread.sleep(milisecond);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	public static JSONArray readJSONFile(String fileNameWithPath) {
		JSONArray array = null;
		BufferedReader reader = null;
		try {
			File file = new File(fileNameWithPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileReader fr = new FileReader(file);
			reader = new BufferedReader(fr);
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			boolean hasContent = false;
			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
				hasContent = true;
			}
			if (hasContent) {
				array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}
		return array;
	}

	public static boolean writeJSONFile(JSONArray array, String fileNameWithPath) {
		boolean isOK = true;
		BufferedWriter writer = null;
		try {
			File file = new File(fileNameWithPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			writer = new BufferedWriter(fw);
			writer.write(array.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			isOK = false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}
		return isOK;
	}

	public static ArrayList<String> readTXTFile(String fileName) {
		ArrayList<String> s = null;
		BufferedReader reader = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return s;
			}
			FileReader fr = new FileReader(file);
			reader = new BufferedReader(fr);
			String line = null;
			s = new ArrayList<String>();
			boolean hasContent = false;
			while ((line = reader.readLine()) != null) {
				s.add(line);
				hasContent = true;
			}
			if (hasContent == false) {
				s = null;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}
		return s;
	}

	public static boolean writeTXTFile(ArrayList<String> s, String fileName) {
		boolean isOK = true;
		BufferedWriter writer = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			writer = new BufferedWriter(fw);
			int i;
			for (i = 0; i <= s.size() - 1; i++) {
				writer.write(s.get(i));
				if (i < s.size() - 1) {
					writer.write("\r\n");
				}
			}
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			isOK = false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}
		return isOK;
	}

	public synchronized static boolean appendTXTFile(ArrayList<String> s,
			String fileName) {
		boolean isOK = true;
		BufferedWriter writer = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			int i;
			for (i = 0; i <= s.size() - 1; i++) {
				writer.write(s.get(i));
				writer.write("\r\n");
			}
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			isOK = false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}
		return isOK;
	}

	// get the device manufacturer
	public static String getDeviceManufacturer() {
		Build b = new Build();
		String manufacturer = b.MANUFACTURER;
		return manufacturer;
	}

	// get the device model
	public static String getDeviceModel() {
		Build b = new Build();
		String model = b.MODEL;
		return model;
	}

	public static String timeStamp2DateTime(Time time, boolean isMills) {
		int year = time.year;
		int month = time.month + 1;
		int day = time.monthDay;
		int dayofweek = time.weekDay;
		int hour = time.hour;
		int min = time.minute;
		int sec = time.second;
		String str;
		switch (dayofweek) {
		case 1:
			str = "MON";
			break;
		case 2:
			str = "TUE";
			break;
		case 3:
			str = "WED";
			break;
		case 4:
			str = "THU";
			break;
		case 5:
			str = "FRI";
			break;
		case 6:
			str = "SAT";
			break;
		default:
			str = "SUN";
		}
		String dateTime = year + "." + month + "." + day + "-" + str + "-" + hour + ":" + min + ":" + sec;
		if (isMills) {
			long timeStamp = time.toMillis(false);
			int millis = (int) (timeStamp % 1000);
			dateTime = dateTime + "." + millis;
		}
		return dateTime;
	}

	
	 public static void doCmds(String cmd) throws Exception {
	        Process process = Runtime.getRuntime().exec("su");
	        DataOutputStream os = new DataOutputStream(process.getOutputStream());
	        os.writeBytes(cmd);
	        os.flush();
	        os.close();
	    }

	//删除文件夹
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//删除文件
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	// 得到指定文件或者文件夹的大小
	public static double getDirSize(File file) {
		// 判断文件是否存在
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				double size = 0;
				for (File f : children)
					size += f.length();
				return size;
			} else {// 如果是文件则直接返回其大小,以“兆”为单位
				double size = (double) file.length();
				return size;
			}
		} else {
			// System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
			return 0.0;
		}
	}


	//解析测试参数
//	public static String[] getTestPara (String fielPath) throws Exception{
//		Log.i("weixin","11111");
//		//读取
//		File file=new File(fielPath);
//		Reader reader = new InputStreamReader(new FileInputStream(file));
//		FileReader fr = new FileReader(file);
//		Log.i("weixin","3333");
//		BufferedReader reader = new BufferedReader(fr);
//
//		StringBuilder jsonString = new StringBuilder();
//		String line = null;
//		while ((line = reader.readLine()) != null) {
//			jsonString.append(line);
//		}
//		Log.i("weixin",jsonString.toString());
//		String[] arr=jsonString.toString().split("###");
//
//		return arr;
//	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 */


	
}

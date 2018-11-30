package com.example.field.fieldtest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.database.Cursor;
import android.util.Log;

public class ExportToCSVUtil {
	/**
	 *
	 * @param c
	 *            数据库Cursor
	 * @param filePath
	 *            保存文件的路径
	 * @param fileName
	 *            保存文件的名字,不用加后缀名
	 */
	public static void ExportToCSV(Cursor c, String filePath, String fileName) {

		try {
			long startTime = System.currentTimeMillis();
			int rowCount = 0;
			int colCount = 0;
			FileWriter fw;
			BufferedWriter bfw;
			String date = null;
			String num = null;

			File path = new File(filePath);// 创建保存csv文件的文件夹
			if (!path.exists()) {
				path.mkdirs();
			}

			File saveFile = new File(path, fileName + ".csv");

			rowCount = c.getCount();
			colCount = c.getColumnCount();
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);

			if (rowCount > 0) {

				for (int i = 0; i < rowCount; i++) {
					c.moveToPosition(i);
					Log.e("导出数据	", "正在导第" + (i + 1) + "条数据");

					for (int j = 0; j < colCount; j++) {
						if (j != colCount - 1) {
							bfw.write(c.getString(j) + ",");

						} else {
							bfw.write(c.getString(j));

						}

					}
					if (i == rowCount - 1) {
						date = c.getString(0);
						num = c.getString(2) + 1;
					}
					// 写好每条记录后换行
					bfw.newLine();

				}


				// bfw.write("this,finish");
			}
			// 将缓存存入文件
			bfw.flush();
			// 释放缓存
			bfw.close();
			Log.e("导出数据完毕", "导出数据完毕");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
	}
}

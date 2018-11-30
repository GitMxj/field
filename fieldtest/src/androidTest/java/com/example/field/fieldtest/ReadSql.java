package com.example.field.fieldtest;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class ReadSql {


    /**
     *
     * @param context
     * @param
     *
     * @param fileNum
     *            当前上传到第几段了
     * @param isEnd
     *            是否上传结束
     */
    public static void readAndWrite(Context context, int fileNum,
                                    Boolean isEnd, String dbName) {
        Cursor cAction = null;
        Cursor cAction02 = null;
        Cursor cNetWork = null;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("sdcard/adbtestcase/"
                + dbName + ".db", null);
//

        ReadSql.exportAndZip(context, fileNum, isEnd, true,dbName);


    }
    /**
     *
     * @param context
     * @param
     *
     * @param fileNum
     *            当前上传到第几段了
     * @param isEnd
     *            是否上传结束
     * @param isActionLog 是否有本action的csv日志文件
     *
     */
    //从数据库中读出当前action文件并压缩
    public static void exportAndZip( final Context context,final int fileNum,final Boolean isEnd ,Boolean isActionLog,String dbName){
        Cursor cAction = null, cNetWork = null;
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("sdcard/adbtestcase/"
                + dbName + ".db", null);

        Cursor tableName=db.rawQuery("select name from sqlite_master where type='table' order by name",null);
        while(tableName.moveToNext()) {
            //遍历出表名
            String logName = tableName.getString(0);
            Log.i("readsqi", logName);

            if(!logName.contains("metadata")){
                try {
                    if(isActionLog){
                        cAction = db.rawQuery("select * from "+logName, null);
                        ExportToCSVUtil.ExportToCSV(cAction,"/sdcard/adbtestcase/" + dbName, logName);
                    }

                    cNetWork = db.rawQuery("select * from networklog", null);
                    // 导出文件
                    ExportToCSVUtil.ExportToCSV(cNetWork, "/sdcard/adbtestcase/" + dbName, "networklog");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // 压缩
        try {
            new Thread().sleep(3 * 1000);
            ZipFile.zipFile("/sdcard/adbtestcase/" +dbName);


            String rarFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/adbtestcase";
            File fileOld = new File(rarFilePath, dbName+".db");
            File fileNew = new File(rarFilePath, "OK" + dbName+".db");
            fileOld.renameTo(fileNew);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

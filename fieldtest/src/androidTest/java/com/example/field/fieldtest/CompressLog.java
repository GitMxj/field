package com.example.field.fieldtest;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by wangshiqian on 2018/9/30.
 */
@RunWith(AndroidJUnit4.class)
public class CompressLog {

    Context testedContext;
    Instrumentation instrumentation;

    @Before
    public void setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        testedContext = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void compressFile(){

        //遍历testcase中所有的文件  得到文件名  根据文件名判断哪些文件需要压缩
        String logPath = getSDPath() + "/adbtestcase";

        File file = new File(logPath);

        File[] subFile= file.listFiles();

        for(int i=0 ; i<subFile.length; i++){
            String subFileName= subFile[i].getName();
//            Log.i("readsqi",subFileName);

            if(subFileName.endsWith(".db") && !subFileName.startsWith("OK")){
                String[] strs= subFileName.split(".db");
                String dbName =strs[0];
                ReadSql.readAndWrite(testedContext,1,true,dbName);

            }
        }



    }
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

}

package com.example.field.fieldtest;

/**
 * Created by wangshiqian on 2018/9/29.
 */
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ZipFile {

    String archPath = "";// 压缩包保存的路径
        /**
         *
         * @param filePath
         *            压缩文件夹的路径
         */
        public static void zipFile(String filePath) {
            Log.e("开始压缩文件", System.currentTimeMillis() + "");
            String srcPath = "";// 压缩文件的路径
            String archPath = "";// 压缩包保存的路径
            String[] fileSrcStrings;// 指定压缩源，可以是目录或文件的数组
            String commentString = "Androi Java Zip 测试.";// 压缩包注释
            ZipUtil mZipControl;

            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            archPath = path + "/adbtestcase";
            File zipFile = new File(archPath);// 创建保存zip文件的文件夹
            if (!zipFile.exists()) {
                zipFile.mkdir();
            }

            srcPath = filePath;
            File srcFile = new File(srcPath);// 创建压缩源的文件夹
            if (!srcFile.exists()) {
                srcFile.mkdir();
            }

            fileSrcStrings = new String[] { srcFile.toString() };
            mZipControl = new ZipUtil();

            try {
                mZipControl.writeByApacheZipOutputStream(fileSrcStrings, srcPath+".zip", commentString);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    public static void noZipFile(String filePath,String lastFileName){

        Log.e("开始压缩文件", System.currentTimeMillis() + "");
        String srcPath = "";// 压缩文件的路径
        String archPath = "";// 压缩包保存的路径
        String[] fileSrcStrings;// 指定压缩源，可以是目录或文件的数组
        String commentString = "Androi Java Zip 测试.";// 压缩包注释
        ZipUtil mZipControl;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        archPath = path + "/adbtestcase";
        File zipFile = new File(archPath);// 创建保存zip文件的文件夹
        if (!zipFile.exists()) {
            zipFile.mkdir();
        }

        srcPath = filePath;
        File srcFile = new File(srcPath);// 创建压缩源的文件夹
        if (!srcFile.exists()) {
            srcFile.mkdir();
        }

        fileSrcStrings = new String[] { srcFile.toString() };
        mZipControl = new ZipUtil();

        try {
            mZipControl.writeByApacheZipOutputStream(fileSrcStrings, archPath  + ".zip", commentString);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    }






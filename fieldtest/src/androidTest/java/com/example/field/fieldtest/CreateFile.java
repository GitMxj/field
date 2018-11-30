package com.example.field.fieldtest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by wangshiqiandemac on 2018/5/22.
 *
 * 生成指定大小的文件
 */

public class CreateFile {



    public enum FileUnit {
        KB, MB, GB
    }

    /**
     * 创建指定大小和类型的文件
     * @author cxq
     * @param targetFile 文件路径以及文件名，需要加后缀
     * @param fileLength 文件大小
     * @param unit 单位，KB,MB，GB
     * @retrun boolean
     */
    public static boolean createFile(String targetFile,String fileName ,long fileLength, FileUnit unit) {
        //指定每次分配的块大小
        long KBSIZE = 1024;
        long MBSIZE1 = 1024 * 1024;
        long MBSIZE10 = 1024 * 1024 * 10;
        switch (unit) {
            case KB:
                fileLength = fileLength * 1024;
                break;
            case MB:
                fileLength = fileLength * 1024*1024;
                break;
            case GB:
                fileLength = fileLength * 1024*1024*1024;
                break;

            default:
                break;
        }
        FileOutputStream fos = null;
        File f = new File(targetFile);
        try {

            if (!f.exists()) {
                f.mkdir();
            }

           File file= new File(targetFile, fileName);

            if(!file.exists()){
                file.createNewFile();
            }



            long batchSize = 0;
            batchSize = fileLength;
            if (fileLength > KBSIZE) {
                batchSize = KBSIZE;
            }
            if (fileLength > MBSIZE1) {
                batchSize = MBSIZE1;
            }
            if (fileLength > MBSIZE10) {
                batchSize = MBSIZE10;
            }
            long count = fileLength / batchSize;
            long last = fileLength % batchSize;

            fos = new FileOutputStream(file);
            FileChannel fileChannel = fos.getChannel();
            for (int i = 0; i < count; i++) {
                ByteBuffer buffer = ByteBuffer.allocate((int) batchSize);
                fileChannel.write(buffer);

            }
            if (last != 0) {
                ByteBuffer buffer = ByteBuffer.allocate((int) last);
                fileChannel.write(buffer);
            }
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}

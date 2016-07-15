package com.caij.emore.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Caij on 2016/6/24.
 */
public class FileUtil {

    public static String readAssetsFile(String file, Context context) {
        StringBuffer sb = new StringBuffer();

        try {
            InputStream e = context.getResources().getAssets().open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(e, "UTF-8"));
            String readLine = null;

            while((readLine = reader.readLine()) != null) {
                sb.append(readLine);
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

        return sb.toString();
    }

    public static long getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {
                long size = file.length();
                return size;
            }
        } else {
            return 0;
        }
    }

    public static void deleteFolderFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath());
                    }
                }else {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

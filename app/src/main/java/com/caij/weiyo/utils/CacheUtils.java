package com.caij.weiyo.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by Caij on 2016/7/6.
 */
public class CacheUtils {

    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static long getCacheFileSizeLong(Context context) {
        return getDirSize(getCacheDir(context));
    }

    public static String getCacheFileSizeString(Context context) {
        long size = getCacheFileSizeLong(context);
        long kb = (long) (size / 1024f);
        if (kb >= 1000) {
            long mb = (long) (kb / 1000f);
            if (mb >= 1000) {
                long gb = (long) (mb / 1024f);
                return gb + "G";
            }else {
                return mb + "M";
            }
        }else {
            return kb + "KB";
        }

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
}

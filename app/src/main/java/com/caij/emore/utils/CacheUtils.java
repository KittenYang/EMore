package com.caij.emore.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Caij on 2016/7/6.
 */
public class CacheUtils {

    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    public static long getCacheFileSizeLong(Context context) {
        return FileUtil.getDirSize(getCacheDir(context));
    }

    public static void clearCache(Context context) {
        FileUtil.deleteFolderFile(getCacheDir(context).getAbsolutePath());
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

}

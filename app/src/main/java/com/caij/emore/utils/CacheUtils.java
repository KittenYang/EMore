package com.caij.emore.utils;

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
        return FileUtil.getFileSize(getCacheDir(context));
    }

    public static void clearCache(Context context) {
        FileUtil.deleteFolderFile(getCacheDir(context).getAbsolutePath());
    }

    public static String getCacheFileSizeString(Context context) {
        return FileUtil.getFileSizeString(getCacheDir(context));
    }

    public static File getCompressImageDir(Context context) {
        return new File(getCacheDir(context), "CompressImage");
    }

}

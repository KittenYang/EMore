package com.caij.emore.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Caij on 2016/2/26.
 */
public class ChannelUtil {

    private static final String CHANNEL_SCHEME_SPIT = "_";
    public final static String CHANNEL_SCHEME = "channel";

    public final static String FOLDER_NAME = "META-INF";

    public static String getChannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        String ret = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(FOLDER_NAME) && entryName.contains(CHANNEL_SCHEME)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (ret != null) {
                String[] split = ret.split(CHANNEL_SCHEME_SPIT);
                if (split.length >= 2) {
                    return ret.substring(split[0].length() + 1);
                }
            }
        }catch (Exception e) {

        }

        return null;
    }

}

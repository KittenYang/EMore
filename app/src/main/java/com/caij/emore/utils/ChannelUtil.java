package com.caij.emore.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Caij on 2016/2/26.
 */
public class ChannelUtil {

    public static String CHANNEL_SCHEME = "channel";
    public static String CHANNEL_SCHEME_SPIT = "_";

    public static String getChannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(CHANNEL_SCHEME)) {
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

        if (!TextUtils.isEmpty(ret)) {
            String[] split = ret.split(CHANNEL_SCHEME_SPIT);
            if (split.length >= 2) {
                return ret.substring(split[0].length() + 1);
            }
        }

        return null;
    }
}

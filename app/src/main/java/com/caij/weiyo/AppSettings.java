package com.caij.weiyo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Caij on 2016/7/6.
 */
public class AppSettings {

    /**
     * 是否使用内置浏览器
     *
     * @return
     */
    public static boolean isInnerBrower() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeiYoApplication.getInstance());
        return prefs.getBoolean("pInnerBrowser", true);
    }

}

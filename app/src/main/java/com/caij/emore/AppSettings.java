package com.caij.emore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Caij on 2016/7/6.
 */
public class AppSettings {

    public static final String MessageIntervalDefaultValue = "300000";

    /**
     * 是否使用内置浏览器
     *
     * @return
     */
    public static boolean isInnerBrower(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.key_setting_browser), true);
    }

    public static boolean isNotifyEnable(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.key_notification_enable), true);
    }

    public static long getMessageIntervalValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Long.parseLong(prefs.getString(context.getString(R.string.key_message_interval), MessageIntervalDefaultValue));
    }

    public static boolean isNotifyWeiboMentionEnable(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.key_status_mention), true);
    }

    public static boolean isNotifyCommentMentionEnable(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.key_comment_mention), true);
    }

    public static boolean isNotifyFollowerEnable(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.key_setting_follower), true);
    }

    public static boolean isNotifyCommentEnable(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.key_setting_comment), true);
    }

    public static boolean isNotifyDmEnable(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.key_setting_dm), true);
    }

    public static boolean isNotifyAttitudeEnable(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getBoolean(ctx.getString(R.string.key_setting_attitude), true);
    }
}

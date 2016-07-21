package com.sina.weibo.security;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class WeiboSecurityUtils {
    public static WeiboSecurityUtils instance;
    private static Object mCalculateSLock;
    private static String sIValue;
    private static String sImei;
    private static String sMac;
    private static String sSeed;
    private static String sValue;

    static {
        System.loadLibrary("utility");
        instance = null;
        sImei = "";
        sMac = "";
        mCalculateSLock = new Object();
    }

    public static String calculateSInJava(Context paramContext, String paramString1, String paramString2) {
        synchronized (mCalculateSLock) {
            if ((paramString1.equals(sSeed)) && (!TextUtils.isEmpty(sValue))) {
                String str2 = sValue;
                return str2;
            }
            if (paramContext != null) {
                sSeed = paramString1;
                sValue = getInstance().calculateS(paramContext.getApplicationContext(), paramString1, paramString2);
                String str1 = sValue;
                return str1;
            }
        }
        return "";
    }

    public static String getIValue(Context paramContext) {
        if (TextUtils.isEmpty(sIValue)) {
            String str1 = getImei(paramContext);
            if (TextUtils.isEmpty(str1))
                str1 = getWifiMac(paramContext);
            if (TextUtils.isEmpty(str1))
                str1 = "000000000000000";
            if ((paramContext != null) && (!TextUtils.isEmpty(str1))) {
                String str2 = getInstance().getIValue(paramContext.getApplicationContext(), str1);
                sIValue = str2;
                return str2;
            }
            return "";
        }
        return sIValue;
    }

    public static String getImei(Context paramContext) {
        if ((TextUtils.isEmpty(sImei)) && (paramContext != null))
            sImei = ((TelephonyManager) paramContext.getSystemService("phone")).getDeviceId();
        return sImei;
    }

    public static WeiboSecurityUtils getInstance() {
        if (instance == null)
            instance = new WeiboSecurityUtils();
        WeiboSecurityUtils localWeiboSecurityUtils = instance;
        return localWeiboSecurityUtils;
    }

    public static String getWifiMac(Context paramContext) {
        WifiInfo localWifiInfo;
        if ((TextUtils.isEmpty(sMac)) && (paramContext != null)) {
            localWifiInfo = ((WifiManager) paramContext.getApplicationContext().getSystemService("wifi")).getConnectionInfo();
            if (localWifiInfo == null)
                return "";
            String str = localWifiInfo.getMacAddress();
            sMac = str;
        }
        return sMac;
    }

    public native String calculateS(Context paramContext, String paramString1, String paramString2);

    public native String getIValue(Context paramContext, String paramString);
}


package com.sina.weibo.security;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class WeiboSecurityUtils {
    private static WeiboSecurityUtils instance;

    static {
        System.loadLibrary("utility");
    }

    public static WeiboSecurityUtils getInstance() {
        if (instance == null) {
            instance = new WeiboSecurityUtils();
        }
        return instance;
    }

    public synchronized static String calculateSInJava(Context paramContext, String paramString1, String paramString2) {
        return getInstance().calculateS(paramContext.getApplicationContext(), paramString1, paramString2);
    }

    public synchronized static String getIValue(Context paramContext) {
        String str1 = getImei(paramContext);
        if (TextUtils.isEmpty(str1))
            str1 = getWifiMac(paramContext);
        if (TextUtils.isEmpty(str1))
            str1 = "000000000000000";
        if ((paramContext != null) && (!TextUtils.isEmpty(str1))) {
            return getInstance().getIValue(paramContext, str1);
        }
        return "";
    }

    public static String getImei(Context paramContext) {
        return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static String getWifiMac(Context paramContext) {
        WifiInfo localWifiInfo;
        localWifiInfo = ((WifiManager) paramContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        if (localWifiInfo == null) {
            return "";
        } else {
            return localWifiInfo.getMacAddress();
        }
    }

    public native String calculateS(Context paramContext, String paramString1, String paramString2);

    public native String getIValue(Context paramContext, String paramString);

}

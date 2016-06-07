package com.caij.weiyo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by Caij on 2016/6/4.
 */
public class SystemUtil {

    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), "status_bar_height");
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if(resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }

        return result;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.isAvailable())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNetworkWifi(Context context) {
        return getNetworkType(context) == ConnectivityManager.TYPE_WIFI;
    }

    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return -1;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            netType = ConnectivityManager.TYPE_MOBILE;
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = ConnectivityManager.TYPE_WIFI;
        }
        return netType;
    }

//    public static int getScreenWidth(Context context) {
//        DisplayMetrics dm = new DisplayMetrics();
//        WindowManager windowManager = (WindowManager) context.getSystemService("window");
//        windowManager.getDefaultDisplay().getMetrics(dm);
//        screenWidth = dm.widthPixels;
//        screenHeight = dm.heightPixels;
//        density = dm.density;
//    }
}

package com.caij.weiyo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

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

    public static void showKeyBoard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
                    imm.toggleSoftInputFromWindow(view.getWindowToken(), 0,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyBoard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    if (imm.isActive(view)) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isKeyBoardShow(Activity paramActivity) {
        int height = DensityUtil.getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity) - getAppHeight(paramActivity);
        return height != 0;
    }

    public static int getAppHeight(Activity paramActivity) {
        Rect localRect = new Rect();
        paramActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        return localRect.height();
    }

}

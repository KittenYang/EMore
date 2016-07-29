package com.caij.emore.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

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

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }

}

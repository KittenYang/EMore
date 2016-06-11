package com.caij.weiyo.utils.okhttp;

import com.caij.weiyo.utils.LogUtil;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by Caij on 2015/10/30.
 */
public class OkHttpClientProvider {

    private static final String TAG = "OkHttpClientProvider";

    private static OkHttpClient sOkHttpClient;

    public static OkHttpClient getDefaultOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (OkHttpClientProvider.class) {
                if (sOkHttpClient == null) {
                    OkHttpClient.Builder okHttpBuild = new OkHttpClient.Builder();
                    try {
                        //only debug
                        Class c = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor");
                        okHttpBuild.addNetworkInterceptor((Interceptor) c.newInstance());
                    } catch (Exception e) {
                        LogUtil.e(TAG, "com.facebook.stetho.okhttp3.StethoInterceptor not found");
                    }
                    sOkHttpClient = okHttpBuild.build();
                }
            }
        }
        return sOkHttpClient;
    }

    public static OkHttpClient newDefaultOkHttpClient() {
        OkHttpClient.Builder okHttpBuild = new OkHttpClient.Builder();
        try {
            Class c = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor");
            okHttpBuild.addNetworkInterceptor((Interceptor) c.newInstance());
        } catch (Exception e) {
            LogUtil.e(TAG, "com.facebook.stetho.okhttp3.StethoInterceptor not found");
        }
        sOkHttpClient = okHttpBuild.build();
        return sOkHttpClient;
    }

}

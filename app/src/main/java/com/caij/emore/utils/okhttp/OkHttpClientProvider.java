package com.caij.emore.utils.okhttp;

import com.caij.emore.BuildConfig;
import com.caij.emore.EMApplication;
import com.caij.emore.R;
import com.caij.emore.utils.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Caij on 2015/10/30.
 */
public class OkHttpClientProvider {

    private static final String TAG = "OkHttpClientProvider";

    private static final String ua = EMApplication.getInstance().getString(R.string.app_name) + "_" + android.os.Build.MODEL + "_" + android.os.Build.VERSION.RELEASE + "_" + BuildConfig.VERSION_NAME;

    public static OkHttpClient getDefaultOkHttpClient(boolean isNeedTest) {
        OkHttpClient.Builder okHttpBuild = new OkHttpClient.Builder();
        okHttpBuild.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .addHeader("User-Agent",  ua)
                        .build();
                return chain.proceed(request);
            }
        });
        try {
            //only debug
            if (isNeedTest) {
                Class c = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor");
                okHttpBuild.addNetworkInterceptor((Interceptor) c.newInstance());
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "com.facebook.stetho.okhttp3.StethoInterceptor not found");
        }
        return okHttpBuild.build();
    }

}

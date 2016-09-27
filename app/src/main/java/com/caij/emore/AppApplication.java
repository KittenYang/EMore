package com.caij.emore;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.utils.Init;
import com.caij.emore.utils.ChannelUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Caij on 2016/5/27.
 */
public class AppApplication extends Application{

    private static Application mApplication;

    public void onCreate() {
        super.onCreate();

        mApplication = this;
        initCrashReport();
        if (UserPrefs.get(this).getToken() != null) {
            Init.getInstance().start(this, UserPrefs.get(this).getAccount().getUid());
        }

        boolean isNight = AppSettings.isNight(this);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void initCrashReport(){
        if (!BuildConfig.DEBUG) {
            ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, String>() {
                @Override
                protected String doInBackground(Object... params) {
                    String channel = ChannelUtil.getChannel(getApplicationContext());
                    LogUtil.d(AppApplication.this, "get channel : %s", channel);
                    if (TextUtils.isEmpty(channel)) {
                        channel = "default";
                    }

                    CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());

                    strategy.setAppChannel(channel);
                    CrashReport.initCrashReport(getApplicationContext(), Key.BUGLY_KEY, true, strategy);
                    return null;
                }
            });
         }
    }

    public static Context getInstance() {
        return mApplication;
    }
}

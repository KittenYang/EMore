package com.caij.emore;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.utils.Init;
import com.caij.emore.utils.ChannelUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SPUtil;
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
        if (UserPrefs.get(this).getEMoreToken() != null && UserPrefs.get(this).getWeiCoToken() != null) {
            Token accessToken = UserPrefs.get(this).getEMoreToken();
            Init.getInstance().reset(this, Long.parseLong(accessToken.getUid()));
        }
    }

    private void initCrashReport(){
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                return ChannelUtil.getChannel(getApplicationContext());
            }

            @Override
            protected void onPostExecute(String channel) {
                LogUtil.d(AppApplication.this, "get channel : %s", channel);
                if (TextUtils.isEmpty(channel)) {
                    channel = "default";
                }
                if (!BuildConfig.DEBUG) {
                    CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());

                    strategy.setAppChannel(channel);
                    CrashReport.initCrashReport(getApplicationContext(), Key.BUGLY_KEY, true, strategy);
                }
            }
        });
    }

    public static Context getInstance() {
        return mApplication;
    }
}

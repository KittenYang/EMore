package com.caij.emore;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.caij.emore.bean.AccessToken;
import com.caij.emore.service.EMoreService;
import com.caij.emore.utils.AppUtil;
import com.caij.emore.utils.ChannelUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.SPUtil;
import com.caij.emore.utils.db.DBManager;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Caij on 2016/5/27.
 */
public class AppApplication extends Application{

    private static Application mApplication;

    public void onCreate() {
        super.onCreate();
        mApplication = this;
        SPUtil.init(this, Key.SP_CONFIG);
        initCrashReport();
        if (UserPrefs.get().getEMoreToken() != null && UserPrefs.get().getWeiCoToken() != null) {
            AccessToken accessToken = UserPrefs.get().getEMoreToken();
            AppUtil.start(this, Long.parseLong(accessToken.getUid()));
        }
    }

    private void initCrashReport(){
        if (!BuildConfig.DEBUG) {
            ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, String>() {
                @Override
                protected String doInBackground(Object... params) {
                    return ChannelUtil.getChannel(getApplicationContext());
                }

                @Override
                protected void onPostExecute(String channel) {
                    CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
                    strategy.setAppChannel(channel);
                    CrashReport.initCrashReport(getApplicationContext(), Key.BUGLY_KEY, true, strategy);
                }
            });
        }
    }

    public static Context getInstance() {
        return mApplication;
    }
}

package com.caij.weiyo;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.database.dao.DBManager;
import com.caij.weiyo.service.WeiyoService;
import com.caij.weiyo.utils.ChannelUtil;
import com.caij.weiyo.utils.ExecutorServiceUtil;
import com.caij.weiyo.utils.SPUtil;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Caij on 2016/5/27.
 */
public class WeiYoApplication extends Application{

    private static Application mApplication;

    public void onCreate() {
        super.onCreate();
        mApplication = this;
        SPUtil.init(this, Key.SP_CONFIG);
        initDB();
        initCrashReport();
        startWeiyoService();
    }

    private void initDB() {
        AccessToken accessToken = UserPrefs.get().getWeiYoToken();
        if (accessToken != null) {
            DBManager.initDB(this, Key.DB_NAME + accessToken.getUid());
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

    private void startWeiyoService() {
        if (UserPrefs.get().getWeiYoToken() != null) {
            WeiyoService.start(this);
        }
    }

    public static Context getInstance() {
        return mApplication;
    }
}

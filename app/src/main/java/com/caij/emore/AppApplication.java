package com.caij.emore;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;

import com.caij.emore.bean.AccessToken;
import com.caij.emore.service.EMoreService;
import com.caij.emore.utils.AppUtil;
import com.caij.emore.utils.ChannelUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SPUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.tencent.bugly.crashreport.CrashReport;

import rx.functions.Action1;

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
                    if (TextUtils.isEmpty(channel)) {
                        channel = "default";
                    }
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

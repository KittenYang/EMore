package com.caij.emore;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.caij.emore.account.Account;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.ChannelUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.tencent.bugly.crashreport.CrashReport;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/5/27.
 */
public class EMApplication extends Application{

    private static Application mApplication;
    private int mVisibleActivityCount;

    public void onCreate() {
        super.onCreate();
        mApplication = this;

        initCrashReport();

        if (UserPrefs.get(this).getToken() != null) {
            Init.getInstance().start(this, UserPrefs.get(this).getAccount().getUid());
        }

        initNightMode();

        if (SystemUtil.isMainProcess(this)) {
            registerTokenExpiredEvent();
            registerActivityEvent();
        }
    }

    private void initNightMode() {
        boolean isNight = AppSettings.isNight(this);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void registerActivityEvent() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mVisibleActivityCount ++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mVisibleActivityCount --;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    protected void registerTokenExpiredEvent() {
        Observable<Object> tokenExpiredObservable = RxBus.getDefault().register(EventTag.EVENT_TOKEN_EXPIRED);
        tokenExpiredObservable.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onAuthenticationError();
            }
        });
    }

    public void onAuthenticationError() {
        Account account = UserPrefs.get(this).getAccount();
        Intent intent = WeiCoLoginActivity.newWeiCoLoginIntent(this, null,
                null);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        UserPrefs.get(this).deleteUsingAccount();

        Init.getInstance().stop(this);

        if (mVisibleActivityCount > 0) { //app 用户可见
            //直接跳到登陆页
            ToastUtil.show(this, R.string.auth_invalid_hint);
            startActivity(intent);
        }else {
            //关闭所有的Activity
            ActivityStack.getInstance().finishAllActivity();
            Process.killProcess(Process.myPid());
        }
    }


    private void initCrashReport(){
        if (!BuildConfig.DEBUG) {
            ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, String>() {
                @Override
                protected String doInBackground(Object... params) {
                    String channel = ChannelUtil.getChannel(getApplicationContext());
                    LogUtil.d(EMApplication.this, "get app channel : %s", channel);
                    if (TextUtils.isEmpty(channel)) {
                        channel = "default";
                    }

                    CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());

                    strategy.setAppChannel(channel);
                    CrashReport.initCrashReport(getApplicationContext(), Key.BUGLY_KEY, false, strategy);
                    return null;
                }
            });
         }
    }

    public static Context getInstance() {
        return mApplication;
    }
}

package com.caij.emore;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;

import com.caij.emore.account.UserPrefs;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.image.ImageLoadFactory;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.ChannelUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.tencent.bugly.crashreport.CrashReport;

import hugo.weaving.DebugLog;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/27.
 */
public class EMApplication extends Application{

    private static Application mApplication;
    private int mVisibleActivityCount;

    public static Context getInstance() {
        return mApplication;
    }

    @DebugLog
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        initCrashReport();

        if (UserPrefs.get(this).getToken() != null) {
            Init.getInstance().start(this, UserPrefs.get(this).getAccount().getUid());
        }

        if (SystemUtil.isMainProcess(this)) {
            registerTokenExpiredEvent();
            registerActivityEvent();

            initNightMode();
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

    private void initCrashReport(){
        RxUtil.createDataObservable(new RxUtil.Provider<String>() {
                @Override
                public String getData() throws Exception {
                    return ChannelUtil.getChannel(getApplicationContext());
                }
            }).filter(new Func1<String, Boolean>() {
                @Override
                public Boolean call(String s) {
                    return !BuildConfig.DEBUG;
                }
            })
            .compose(SchedulerTransformer.<String>create())
            .subscribe(new SubscriberAdapter<String>() {
                @Override
                public void onNext(String channel) {
                    if (TextUtils.isEmpty(channel)) channel = "default";

                    CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
                    strategy.setAppChannel(channel);
                    CrashReport.initCrashReport(getApplicationContext(), Key.BUGLY_KEY, false, strategy);
                }
            });
    }

    private void registerActivityEvent() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksAdapter() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ActivityStack.getInstance().push(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mVisibleActivityCount ++;
            }


            @Override
            public void onActivityStopped(Activity activity) {
                mVisibleActivityCount --;
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                ActivityStack.getInstance().remove(activity);
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
            Init.getInstance().stop(this);
            Process.killProcess(Process.myPid());
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (SystemUtil.isMainProcess(this)) {
            ImageLoadFactory.getImageLoad().onTrimMemory(this, level);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (SystemUtil.isMainProcess(this)) {
            ImageLoadFactory.getImageLoad().onLowMemory(this);
        }
    }

    private static class ActivityLifecycleCallbacksAdapter implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}

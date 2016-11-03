package com.caij.emore.present.imp;

import android.app.Activity;
import android.content.Intent;

import com.caij.emore.account.Account;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.GroupResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.present.SplashPresent;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.view.SplashView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/11/2.
 */

public class SplashPresentImp implements SplashPresent {

    private Observable<Long> mToAppObservable;
    private Subscription mToAppSubscription;
    private SplashView mSplashView;
    private Activity mActivity;
    private Intent mToIntent;

    public SplashPresentImp(SplashView splashView, Activity activity) {
        mToAppObservable = Observable.timer(3, TimeUnit.SECONDS);
        mSplashView = splashView;
        mActivity = activity;
    }

    @Override
    public void onResume() {
        mToAppSubscription = mToAppObservable.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                mSplashView.toActivity(mToIntent);
            }
        });
    }

    @Override
    public void onPause() {
        if (mToAppSubscription != null && !mToAppSubscription.isUnsubscribed()) {
            mToAppSubscription.unsubscribe();
        }
    }

    @Override
    public void onCreate() {
        Token token = UserPrefs.get(mActivity).getToken();
        if (token == null || token.isExpired()){
            Account account = UserPrefs.get(mActivity).getAccount();
            mToIntent = WeiCoLoginActivity.newWeiCoLoginIntent(mActivity,
                    account != null ? account.getUsername() : null, null);
        }else {
            mToIntent = new Intent(mActivity, MainActivity.class);
        }
    }

    @Override
    public void onDestroy() {

    }
}

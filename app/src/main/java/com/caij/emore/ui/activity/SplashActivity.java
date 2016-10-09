package com.caij.emore.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.utils.weibo.ThemeUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/5/28.
 */
public class SplashActivity extends BaseActivity{

    private Observable<Long> mToAppObservable;
    private Subscription mToAppSubscription;
    private Intent mToIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Token token = UserPrefs.get(this).getToken();
        if (token == null || token.isExpired()){
            Account account = UserPrefs.get(this).getAccount();
            mToIntent = WeiCoLoginActivity.newWeiCoLoginIntent(this,
                    account != null ? account.getUsername() : null,
                    account != null ? account.getPwd() : null);
        }else {
            mToIntent = new Intent(this, MainActivity.class);
        }
        mToAppObservable = Observable.timer(3, TimeUnit.SECONDS);
    }

    @Override
    protected void setTheme() {
        int themePosition = ThemeUtils.getThemePosition(this);
        setTheme(ThemeUtils.THEME_ARR[themePosition][ThemeUtils.APP_SPALH_THEME_POSITION]);
    }

    @Override
    protected BasePresent createPresent() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToAppSubscription = mToAppObservable.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                startActivity(mToIntent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mToAppSubscription != null && !mToAppSubscription.isUnsubscribed()) {
            mToAppSubscription.unsubscribe();
        }
    }
}

package com.caij.emore.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.manager.imp.GroupManagerImp;
import com.caij.emore.present.BasePresent;
import com.caij.emore.present.SplashPresent;
import com.caij.emore.present.imp.SplashPresentImp;
import com.caij.emore.remote.imp.GroupApiImp;
import com.caij.emore.repository.GroupRepository;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.view.SplashView;
import com.caij.emore.utils.weibo.ThemeUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/5/28.
 */
public class SplashActivity extends BaseActivity<SplashPresent> implements SplashView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void setTheme() {
        int themePosition = ThemeUtils.getThemePosition(this);
        setTheme(ThemeUtils.THEME_ARR[themePosition][ThemeUtils.APP_SPLASH_THEME_POSITION]);
    }

    @Override
    protected SplashPresent createPresent() {
        return new SplashPresentImp(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresent.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresent.onPause();
    }

    @Override
    public void toActivity(Intent intent) {
        startActivity(intent);
        finish();
    }

}

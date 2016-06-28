package com.caij.weiyo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;

/**
 * Created by Caij on 2016/5/28.
 */
public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccessToken accessToken = UserPrefs.get().getWeiYoToken();
        final Intent intent;
        if (accessToken == null || accessToken.isExpired()) {
            intent = LoginActivity.newWeiYoLoginIntent(this, null, null);
        }else {
            intent = new Intent(this, MainActivity.class);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 2000);
    }


}

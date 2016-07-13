package com.caij.emore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;

/**
 * Created by Caij on 2016/5/28.
 */
public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        AccessToken accessToken = UserPrefs.get().getEMoreToken();
        final Intent intent;
        if (accessToken == null || accessToken.isExpired()) {
            intent = EMoreLoginActivity.newEMoreLoginIntent(this, null, null);
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

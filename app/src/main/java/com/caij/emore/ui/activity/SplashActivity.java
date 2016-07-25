package com.caij.emore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;

/**
 * Created by Caij on 2016/5/28.
 */
public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        AccessToken eMoreAccessToken = UserPrefs.get().getEMoreToken();
        AccessToken weicoAccessToken = UserPrefs.get().getWeiCoToken();
        final Intent intent;
        if (eMoreAccessToken == null || eMoreAccessToken.isExpired()) {
            intent = EMoreLoginActivity.newEMoreLoginIntent(this, null, null);
        }else if (weicoAccessToken == null || weicoAccessToken.isExpired()){
            intent = WeiCoLoginActivity.newWeiCoLoginIntent(this,
                    UserPrefs.get().getAccount().getUsername(),
                    UserPrefs.get().getAccount().getPwd());
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

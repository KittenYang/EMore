package com.caij.emore.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.present.imp.WeiCoLoginPresentImp;
import com.caij.emore.present.view.WeiCoLoginView;
import com.caij.emore.source.server.LoginSourceImp;

/**
 * Created by Caij on 2016/7/8.
 */
public class WeiCoLoginActivity extends AbsLoginActivity implements WeiCoLoginView {

    private LoginPresent mLoginPresent;

    public static Intent newWeiCoLoginIntent(Context context, String userName, String pwd) {
        Intent intent = new Intent(context, WeiCoLoginActivity.class);
        intent.putExtra(Key.USERNAME, userName);
        intent.putExtra(Key.PWD, pwd);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginPresent = new WeiCoLoginPresentImp(new LoginSourceImp(), this);
        mLoginPresent.onCreate();
    }

    @Override
    protected void getAccessToken(String code) {
        mLoginPresent.getAccessToken(getAppId(), getAppSecret(), code, getRedirectUrL());
    }

    protected String getLoginUrl() {
            return String
                    .format("https://api.weibo.com/oauth2/authorize?client_id=%s&redirect_uri=%s&display=mobile&forcelogin=true",
                            getAppId(), getRedirectUrL());
    }

    @Override
    protected String getAppId() {
        return Key.WEICO_APP_ID;
    }

    @Override
    protected String getAppSecret() {
        return Key.WEICO_APP_SECRET;
    }

    @Override
    protected String getRedirectUrL() {
        return Key.WEICO_CALLBACK_URL;
    }

    @Override
    public void onLoginSuccess(WeiCoLoginResponse response) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginPresent.onDestroy();
    }
}

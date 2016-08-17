package com.caij.emore.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.present.imp.WeiCoLoginPresentImp;
import com.caij.emore.ui.view.WeiCoLoginView;
import com.caij.emore.source.server.LoginSourceImp;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.Init;

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
        setTitle("高级认证");
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
        ActivityStack.getInstance().remove(this);
        ActivityStack.getInstance().finishAllActivity();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginSuccess(AccessToken accessToken) {
        init(UserPrefs.get().getEMoreToken().getUid());

        ActivityStack.getInstance().remove(this);
        ActivityStack.getInstance().finishAllActivity();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void init(String uid) {
        Init.getInstance().reset(getApplicationContext(), Long.parseLong(uid));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginPresent.onDestroy();
    }
}

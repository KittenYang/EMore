package com.caij.emore.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.Token;
import com.caij.emore.manager.imp.GroupManagerImp;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.present.imp.WeiCoLoginPresentImp;
import com.caij.emore.remote.imp.GroupApiImp;
import com.caij.emore.repository.GroupRepository;
import com.caij.emore.ui.view.WeiCoLoginView;
import com.caij.emore.remote.imp.LoginApiImp;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.Init;

/**
 * Created by Caij on 2016/7/8.
 */
public class WeiCoLoginActivity extends AbsLoginActivity implements WeiCoLoginView {


    public static Intent newWeiCoLoginIntent(Context context, String userName, String pwd) {
        Intent intent = new Intent(context, WeiCoLoginActivity.class);
        intent.putExtra(Key.USERNAME, userName);
        intent.putExtra(Key.PWD, pwd);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.login_label));
    }

    @Override
    protected LoginPresent createPresent() {
        return new WeiCoLoginPresentImp(new LoginApiImp(), this);
    }

    @Override
    protected void getAccessToken(String code) {
        ((LoginPresent)mPresent).getAccessToken(getAppId(), getAppSecret(), code, getRedirectUrL(), mUsername, mPassword);
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
    public void onLoginSuccess(Token accessToken) {
        init(accessToken.getUid());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void init(String uid) {
        Init.getInstance().reset(getApplicationContext(), Long.parseLong(uid));
    }

}

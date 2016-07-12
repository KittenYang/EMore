package com.caij.emore.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.dao.DBManager;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.present.imp.LoginPresentImp;
import com.caij.emore.present.view.LoginView;
import com.caij.emore.source.server.LoginSourceImp;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.utils.EventUtil;

/**
 * Created by Caij on 2016/7/8.
 */
public class EMoreLoginActivity extends AbsLoginActivity  implements LoginView {

    private LoginPresent mEMoreLoginPresent;

    public static Intent newEMoreLoginIntent(Context context, String userName, String pwd) {
        Intent intent = new Intent(context, EMoreLoginActivity.class);
        intent.putExtra(Key.USERNAME, userName);
        intent.putExtra(Key.PWD, pwd);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEMoreLoginPresent = new LoginPresentImp(new LoginSourceImp(), this);
        mEMoreLoginPresent.onCreate();
    }

    @Override
    protected void getAccessToken(String code) {
        mEMoreLoginPresent.getAccessToken(getAppId(), getAppSecret(), code, getRedirectUrL());
    }

    protected String getLoginUrl() {
        return String
                .format("https://api.weibo.com/oauth2/authorize?client_id=%s&scope=friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog&redirect_uri=%s&display=mobile&forcelogin=true",
                        getAppId(), getRedirectUrL());
    }

    @Override
    protected String getAppId() {
        return Key.WEIBO_APP_ID;
    }

    @Override
    protected String getAppSecret() {
        return Key.WEIBO_APP_SECRET;
    }

    @Override
    protected String getRedirectUrL() {
        return Key.WEIBO_CALLBACK_URL;
    }


    @Override
    public void onLoginSuccess(AccessToken accessToken) {
        UserPrefs.get().setUsername(mAccount);
        UserPrefs.get().setPwd(mPassword);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        EventUtil.postLoginEvent(true);
        initDB(UserPrefs.get().getEMoreToken().getUid());
        finish();
    }

    private void initDB(String uid) {
        DBManager.initDB(this, Key.DB_NAME + uid);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEMoreLoginPresent.onDestroy();
    }
}

package com.caij.weiyo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.present.LoginPresent;
import com.caij.weiyo.present.imp.LoginPresentImp;
import com.caij.weiyo.present.view.LoginView;
import com.caij.weiyo.source.imp.LoginSourceImp;
import com.caij.weiyo.utils.LogUtil;

import java.net.URL;

/**
 * Created by Caij on 2016/5/28.
 */
public class LoginActivity extends WebActivity implements LoginView {

    private ProgressDialog mLoginDialog;

    private LoginPresent mLoginPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.login_label);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mLoginPresent = new LoginPresentImp(new LoginSourceImp(), this);
        mLoginPresent.onCreate();
        mLoginDialog = new ProgressDialog(this);
        mLoginDialog.setMessage(getString(R.string.logining));
        mLoginDialog.setCancelable(false);
        mLoginDialog.setProgressStyle(R.style.AppCompatAlertDialogStyle);
    }

    @Override
    protected void handlerIntent(Intent intent) {
        mWebView.loadUrl(getLoginUrl());
    }

    @Override
    protected void initView() {
        super.initView();
        mWebView.setWebViewClient(new LoginWebViewClient(mProgressBar));
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_web;
    }

    private void processLoginUrl(String url) throws Exception{
        URL urlU = new URL(url);
        String query = urlU.getQuery();
        String[] params = query.split("&");
        if (params.length != 0) {
            for(String param : params) {
                String[] keyAndValue = param.split("=");
                if ("code".equals(keyAndValue[0])) {
                    String codeValue = keyAndValue[1];
                    getAccessToken(codeValue);
                    break;
                }
            }
        }
    }

    private void getAccessToken(String code) {
        mLoginPresent.getAccessToken(getAppId(), getAppSecret(), code, getRedirectUrL());
    }

    private static String getLoginUrl() {
        return String
                .format("https://api.weibo.com/oauth2/authorize?client_id=%s&scope=friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog&redirect_uri=%s&display=mobile&forcelogin=true",
                        getAppId(), getRedirectUrL());
    }

    private static String getAppId() {
        return Key.WEIBO_APP_ID;
    }

    public static String getAppSecret() {
        return Key.WEIBO_APP_SECRET;
    }

    private static String getRedirectUrL() {
        return Key.WEIBO_CALLBACK_URL;
    }


    @Override
    public void onLoginSuccess(AccessToken accessToken) {
        UserPrefs.get().setToken(accessToken);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoading(boolean isVisible) {
        if (isVisible) {
            mLoginDialog.show();
        }else {
            mLoginDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (mLoginDialog.isShowing()) {
            mLoginDialog.dismiss();
            mLoginDialog = null;
        }
        super.onDestroy();
        mLoginPresent.onDestroy();
    }

    @Override
    public void onAuthenticationError() {
        showToast(R.string.username_or_pwd_error);
    }

    class LoginWebViewClient extends MyWebViewClient {

        public LoginWebViewClient(ProgressBar progressBar) {
            super(progressBar);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(this, url);
            if (url != null && url.startsWith(getRedirectUrL())) { // 授权成功
                try {
                    processLoginUrl(url);
                }catch (Exception e) {
                    LogUtil.d(this, "callback url analyze exception");
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}

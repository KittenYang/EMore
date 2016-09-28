//package com.caij.emore.ui.activity.login;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.caij.emore.Key;
//import com.caij.emore.account.Token;
//import com.caij.emore.account.UserPrefs;
//import com.caij.emore.present.LoginPresent;
//import com.caij.emore.present.imp.LoginPresentImp;
//import com.caij.emore.ui.activity.MainActivity;
//import com.caij.emore.ui.view.LoginView;
//import com.caij.emore.source.server.LoginSourceImp;
//
///**
// * Created by Caij on 2016/7/8.
// */
//public class EMoreLoginActivity extends AbsLoginActivity  implements LoginView {
//
//    public static Intent newEMoreLoginIntent(Context context, String userName, String pwd) {
//        Intent intent = new Intent(context, EMoreLoginActivity.class);
//        intent.putExtra(Key.USERNAME, userName);
//        intent.putExtra(Key.PWD, pwd);
//        return intent;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setTitle("登录");
//    }
//
//    @Override
//    protected LoginPresent createPresent() {
//        return new LoginPresentImp(new LoginSourceImp(), this);
//    }
//
//    @Override
//    protected void getAccessToken(String code) {
//        ((LoginPresent)mPresent).getAccessToken(getAppId(), getAppSecret(), code, getRedirectUrL(),
//                mUsername, mPassword);
//    }
//
//    protected String getLoginUrl() {
//        return String
//                .format("https://api.weibo.com/oauth2/authorize?client_id=%s&scope=friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog&redirect_uri=%s&display=mobile&forcelogin=true",
//                        getAppId(), getRedirectUrL());
//    }
//
//    @Override
//    protected String getAppId() {
//        return Key.WEIBO_APP_ID;
//    }
//
//    @Override
//    protected String getAppSecret() {
//        return Key.WEIBO_APP_SECRET;
//    }
//
//    @Override
//    protected String getRedirectUrL() {
//        return Key.WEIBO_CALLBACK_URL;
//    }
//
//
//    @Override
//    public void onLoginSuccess(Token accessToken) {
//        Intent intent =  WeiCoLoginActivity.newWeiCoLoginIntent(this, mUsername, mPassword);
//        startActivity(intent);
//        finish();
//    }
//
//}

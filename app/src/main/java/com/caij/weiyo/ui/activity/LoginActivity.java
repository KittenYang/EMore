package com.caij.weiyo.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.database.dao.DBManager;
import com.caij.weiyo.present.LoginPresent;
import com.caij.weiyo.present.imp.LoginPresentImp;
import com.caij.weiyo.present.view.LoginView;
import com.caij.weiyo.source.server.LoginSourceImp;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.utils.ExecutorServiceUtil;
import com.caij.weiyo.utils.FileUtil;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.UrlUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;

import retrofit2.http.Part;

/**
 * Created by Caij on 2016/5/28.
 */
public class LoginActivity extends WebActivity implements LoginView {

    private final static int TYPE_WEIYO = 1;
    private final static int TYPE_WEICO = 2;

    private static final String TAG = "LoginActivity";
    private Dialog mLoginDialog;

    private LoginPresent mLoginPresent;
    private String mAccount;
    private String mPassword;
    private AsyncTask<Object, Object, String> mHtmlAsyncTask;
    private boolean mAccountFilled = false;
    private int mLoginType;

    public static Intent newWeiYoLoginIntent(Context context, String userName, String pwd) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Key.USERNAME, userName);
        intent.putExtra(Key.PWD, pwd);
        intent.putExtra(Key.TYPE, TYPE_WEIYO);
        return intent;
    }

    public static Intent newWeiCoLoginIntent(Context context, String userName, String pwd) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Key.TYPE, TYPE_WEICO);
        intent.putExtra(Key.USERNAME, userName);
        intent.putExtra(Key.PWD, pwd);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.login_label);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mLoginPresent = new LoginPresentImp(new LoginSourceImp(), this);
        mLoginPresent.onCreate();
//        mLoginDialog = new ProgressDialog(this);
//        mLoginDialog.setMessage(getString(R.string.logining));
//        mLoginDialog.setCancelable(false);
//        mLoginDialog.setProgressStyle(R.style.AppCompatAlertDialogStyle);
    }

    @Override
    protected void initView() {
        super.initView();
        mWebView.addJavascriptInterface(new LoginJavaScriptInterface(), "loginjs");
        mWebView.setWebViewClient(new LoginWebViewClient(mProgressBar));
        mWebView.setWebChromeClient(new LoginWebChromeClient(mProgressBar));
    }

    @Override
    protected void handlerIntent(Intent intent) {
        mLoginType = getIntent().getIntExtra(Key.TYPE, -1);
        mAccount = getIntent().getStringExtra(Key.USERNAME);
        mPassword = getIntent().getStringExtra(Key.PWD);
        if (mAccount == null || mPassword == null) {
            mAccount = "";
            mPassword = "";
        }
        loadLoginHtml();
    }

    private void loadLoginHtml() {
        mHtmlAsyncTask = new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                int i = 3;
                while (i > 0) {
                    try {
                        String js = FileUtil.readAssetsFile("oauth.js", getApplicationContext());
                        js = js.replace("%username%", mAccount).replace("%password%", mPassword);

                        Document dom = Jsoup.connect(getLoginUrl()).get();
                        String html = dom.toString();
                        html = html.replace("<html>", "<html id='all' >").replace("</head>", js + "</head>")
                                .replace("action-type=\"submit\"", "action-type=\"submit\" id=\"submit\"");

                        try {
                            // 通过监听input标签的oninput事件，来获取账户密码
                            // onchange是value改变，且焦点改变才触发
                            // oninput是value改变就触发
                            dom = Jsoup.parse(html);
                            Element inputAccount = dom.select("input#userId").first();
                            inputAccount.attr("oninput", "getAccount()");

                            Element pwdAccount = dom.select("input#passwd").first();
                            pwdAccount.attr("oninput", "getAccount()");

                            LogUtil.d(TAG, inputAccount.toString());
                            LogUtil.d(TAG, pwdAccount.toString());

                            html = dom.toString();

                            LogUtil.d(TAG, "添加input监听事件");
                        }catch(Exception e){
                            LogUtil.d(TAG, e.getMessage());
                        }

                        LogUtil.v(TAG, html);

                        return html;
                    }catch(Exception e){
                        LogUtil.d(TAG, e.getMessage());
                    }
                    i --;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s)) {
                    mWebView.loadDataWithBaseURL("https://api.weibo.com", s, "text/html", "UTF-8", "");
                }
            }
        };
        ExecutorServiceUtil.executeAsyncTask(mHtmlAsyncTask);
    }


    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_web;
    }

    private void getAccessToken(String code) {
        mLoginPresent.getAccessToken(getAppId(), getAppSecret(), code, getRedirectUrL());
    }

    private String getLoginUrl() {
        if (mLoginType == TYPE_WEIYO) {
            return String
                    .format("https://api.weibo.com/oauth2/authorize?client_id=%s&scope=friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog&redirect_uri=%s&display=mobile&forcelogin=true",
                            getAppId(), getRedirectUrL());
        }else if (mLoginType == TYPE_WEICO) {
            return String
                    .format("https://api.weibo.com/oauth2/authorize?client_id=%s&redirect_uri=%s&display=mobile&forcelogin=true",
                            getAppId(), getRedirectUrL());
        }
        return String
                .format("https://api.weibo.com/oauth2/authorize?client_id=%s&scope=friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog&redirect_uri=%s&display=mobile&forcelogin=true",
                        getAppId(), getRedirectUrL());
    }

    private String getAppId() {
        if (mLoginType == TYPE_WEIYO) {
            return Key.WEIBO_APP_ID;
        }else if (mLoginType == TYPE_WEICO) {
            return Key.WEICO_APP_ID;
        }
        return Key.WEIBO_APP_ID;
    }

    public String getAppSecret() {
        if (mLoginType == TYPE_WEIYO) {
            return Key.WEIBO_APP_SECRET;
        }else if (mLoginType == TYPE_WEICO) {
            return Key.WEICO_APP_SECRET;
        }
        return Key.WEIBO_APP_SECRET;
    }

    private String getRedirectUrL() {
        if (mLoginType == TYPE_WEIYO) {
            return Key.WEIBO_CALLBACK_URL;
        }else if (mLoginType == TYPE_WEICO) {
            return Key.WEIYO_CALLBACK_URL;
        }
        return Key.WEIBO_CALLBACK_URL;
    }

    @Override
    public void onLoginSuccess(AccessToken accessToken) {
        Account account = new Account();
        account.setUsername(mAccount);
        account.setPwd(mPassword);
        if (mLoginType == TYPE_WEIYO) {
            account.setWeiyoToken(accessToken);
            account.setWeicoToken(UserPrefs.get().getWeiCoToken());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if (mLoginType == TYPE_WEICO) {
            account.setWeicoToken(accessToken);
            account.setWeiyoToken(UserPrefs.get().getWeiYoToken());
            setResult(RESULT_OK);
        }
        UserPrefs.get().setAccount(account);
        initDB(accessToken.getUid());
        finish();
    }

    private void initDB(String uid) {
        DBManager.initDB(this, Key.DB_NAME + uid);
    }

    @Override
    public void showDialogLoading(boolean isVisible) {
        if (isVisible) {
            if (mLoginDialog  == null) {
                mLoginDialog = DialogUtil.showProgressDialog(this, null, getString(R.string.logining));
            }else {
                mLoginDialog.show();
            }
        }else {
            if (mLoginDialog != null) {
                mLoginDialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mHtmlAsyncTask != null && !mHtmlAsyncTask.isCancelled()) {
            mHtmlAsyncTask.cancel(true);
        }
        mWebView.removeAllViews();
        mWebView.destroy();
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
                Map<String, String> params  = UrlUtil.getUrlParams(url);
                if (params != null) {
                    String codeValue = params.get("code");
                    getAccessToken(codeValue);
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    class LoginWebChromeClient extends MyWebChromeClient {

        public LoginWebChromeClient(ProgressBar progressBar) {
            super(progressBar);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                if (!TextUtils.isEmpty(mAccount) && !TextUtils.isEmpty(mPassword)) {
                    if (!mAccountFilled && !TextUtils.isEmpty(view.getUrl()) && view.getUrl().equalsIgnoreCase("about:blank")) {
                        LogUtil.d(TAG, "fillAccount(%s, %s)", mAccount, mPassword);

                        view.loadUrl("javascript:fillAccount()");
                        mAccountFilled = true;
                    }
                }
            }
        }
    }


    final class LoginJavaScriptInterface {

        public LoginJavaScriptInterface() {
            LogUtil.d(TAG, "new LoginJavaScriptInterface()");
        }

        @JavascriptInterface
        public void setAccount(String account, String password) {
            LogUtil.d(TAG, "account = %s, password = %s", account, password);
            mAccount = account;
            mPassword = password;
        }

    }
}

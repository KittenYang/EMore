package com.caij.emore.ui.activity.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.ui.activity.WebActivity;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.FileUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import rx.Subscription;

/**
 * Created by Caij on 2016/5/28.
 */
public abstract class AbsLoginActivity extends WebActivity {

    private static final String TAG = "AbsLoginActivity";
    private Dialog mLoginDialog;

    protected String mUsername;
    protected String mPassword;
    private boolean mAccountFilled = false;
    private Subscription mLoginHtmlSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.login_label);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        mUsername = getIntent().getStringExtra(Key.USERNAME);
        mPassword = getIntent().getStringExtra(Key.PWD);

        mUsername = mUsername == null ? "" : mUsername;
        mPassword = mPassword == null ? "" : mPassword;

        loadLoginHtml();
    }

    private void loadLoginHtml() {
        mLoginHtmlSubscription = RxUtil.createDataObservable(new RxUtil.Provider<String>() {
                @Override
                public String getData() throws Exception {
                    int i = 3;
                    while (i > 0) {
                        try {
                            return getLoginHtml();
                        }catch(Exception e){
                            LogUtil.d(TAG, e.getMessage());
                        }
                        i --;
                    }
                    throw new RuntimeException(new IOException());
                }
            })
            .compose(SchedulerTransformer.<String>create())
            .subscribe(new SubscriberAdapter<String>() {
                @Override
                public void onNext(String s) {
                    if (!TextUtils.isEmpty(s)) {
                        mWebView.loadDataWithBaseURL("https://api.weibo.com", s, "text/html", "UTF-8", "");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    showReLoadDialog();
                }
            });
    }

    private void showReLoadDialog() {
        DialogUtil.showHintDialog(AbsLoginActivity.this, getString(R.string.hint), "网页加载失败加载",
                "重新加载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadLoginHtml();
                    }
                }, "退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

    private String getLoginHtml() throws IOException {
        String js = FileUtil.readAssetsFile("oauth.js", getApplicationContext());
        js = js.replace("%username%", mUsername).replace("%password%", mPassword);

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
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_web;
    }

    protected abstract void getAccessToken(String code);

    protected abstract String getLoginUrl();

    protected abstract String getAppId();

    protected abstract String getAppSecret();

    protected abstract String getRedirectUrL();

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
        if (mLoginHtmlSubscription != null && !mLoginHtmlSubscription.isUnsubscribed()) {
            mLoginHtmlSubscription.unsubscribe();
            mLoginHtmlSubscription = null;
        }
        super.onDestroy();
    }

    @Override
    public void onAuthenticationError() {
        showHint(R.string.username_or_pwd_error);
    }

    class LoginWebViewClient extends MyWebViewClient {

        public LoginWebViewClient(ProgressBar progressBar) {
            super(progressBar);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.d(this, url);
            if (url != null && url.startsWith(getRedirectUrL())) { // 授权成功
                String codeValue  = Uri.parse(url).getQueryParameter("code");
                if (codeValue != null) {
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
//                if (!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mPassword)) {
                    if (!mAccountFilled && !TextUtils.isEmpty(view.getUrl()) && view.getUrl().equalsIgnoreCase("about:blank")) {
                        LogUtil.d(TAG, "fillAccount(%s, %s)", mUsername, mPassword);

                        if (!TextUtils.isEmpty(mUsername)) {
                            view.loadUrl("javascript:fillAccount()");
                        }
                        mAccountFilled = true;
                    }
//                }
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
            mUsername = account;
            mPassword = password;
        }

    }
}

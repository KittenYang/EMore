package com.caij.weiyo.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.SpannableStringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/5/28.
 */
public class WebActivity extends BaseToolBarActivity {

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.web_view)
    WebView mWebView;

    public static Intent newIntent(Activity activity, String url) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(Key.URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        handlerIntent(getIntent());
    }

    protected void handlerIntent(Intent intent) {
        String url;
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equalsIgnoreCase(action) && getIntent().getData() != null) {
            url = getIntent().getData().toString();
            if (url.startsWith(SpannableStringUtil.HTTP_SCHEME))
            url = url.replace(SpannableStringUtil.HTTP_SCHEME, "");
            mWebView.loadUrl(url);
        }

    }

    protected void initView() {
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setAppCacheEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient(mProgressBar));
        mWebView.setWebChromeClient(new MyWebChromeClient(mProgressBar));
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_web;
    }

    static class MyWebViewClient extends WebViewClient {

        ProgressBar mProgressBar;

        public MyWebViewClient(ProgressBar progressBar) {
            mProgressBar = progressBar;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    static class MyWebChromeClient extends WebChromeClient {

        ProgressBar mProgressBar;

        public MyWebChromeClient(ProgressBar progressBar) {
            mProgressBar = progressBar;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress < 100) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);

                LogUtil.d(this, "progress = %d , url = %s", newProgress, view.getUrl());
            } else if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);

                LogUtil.d(this, "progress = 100 , url = %s", view.getUrl());

            }
        }
    }
}

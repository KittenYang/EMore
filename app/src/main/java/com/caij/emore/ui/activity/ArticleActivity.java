package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.caij.emore.AppSettings;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Article;
import com.caij.emore.present.ArticlePresent;
import com.caij.emore.present.imp.ArticlePresentImp;
import com.caij.emore.source.server.SercerArticleSource;
import com.caij.emore.ui.view.ArticleView;
import com.caij.emore.widget.weibo.html.ArticleHeadView;

import org.jsoup.Jsoup;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/9/3.
 */
public class ArticleActivity extends BaseToolBarActivity<ArticlePresent> implements ArticleView {


    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.article_head)
    ArticleHeadView articleHead;
    @BindView(R.id.content)
    ScrollView content;
    @BindView(R.id.web_view)
    WebView webView;

    public static Intent newIntent(Context context, String id) {
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra(Key.ID, id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        content.setVisibility(View.GONE);
        setTitle(getString(R.string.article_detail));

        WebSettings settings=webView.getSettings();
        settings.setSupportZoom(true);
        settings.getTextZoom();
        settings.setTextZoom(110);

        webView.setWebViewClient(new ArticleWebViewClient());

        mPresent.loadArticleInfo();
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_article;
    }

    @Override
    protected ArticlePresent createPresent() {
        String token = UserPrefs.get(this).getToken().getAccess_token();
        String contentId = getIntent().getStringExtra(Key.ID);
        return new ArticlePresentImp(token, contentId, new SercerArticleSource(), this);
    }

    @Override
    public void onArticleLoadSuccess(Article article) {
        articleHead.setHtml(article);
        content.setVisibility(View.VISIBLE);
        webView.loadDataWithBaseURL(Key.WEIBO_BASE_URL, Jsoup.parse(article.getData().getArticle()).getElementsByClass("WBA_content").html(), "text/html", "UTF-8", "");
    }

    @Override
    public void showDialogLoading(boolean isShow) {
        if (isShow) {
            pbLoading.setVisibility(View.VISIBLE);
        } else {
            pbLoading.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        content.removeAllViews();
        webView.removeAllViews();
        webView.destroy();
    }

    private static class ArticleWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Context context = view.getContext();
            if (AppSettings.isInnerBrower(context)) {
                url = url.replace("http", context.getString(R.string.emore_http_scheme));
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                context.startActivity(intent);
                return true;
            }
            return false;
        }
    }
}

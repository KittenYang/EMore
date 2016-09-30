package com.caij.emore.present.imp;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.caij.emore.AppApplication;
import com.caij.emore.Key;
import com.caij.emore.bean.Article;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.bean.response.Response;
import com.caij.emore.present.ArticlePresent;
import com.caij.emore.source.ArticleSource;
import com.caij.emore.ui.view.ArticleView;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.widget.span.MyURLSpan;

import org.jsoup.Jsoup;

import java.util.concurrent.ExecutionException;

import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/9/3.
 */
public class ArticlePresentImp extends AbsBasePresent implements ArticlePresent {

    private String mToken;
    private ArticleSource mServerArticleSource;
    private ArticleView mArticleView;
    private String mContainerid;

    public ArticlePresentImp(String token, String containerid, ArticleSource serverArticleSource, ArticleView articleView) {
        this.mToken = token;
        mServerArticleSource = serverArticleSource;
        mArticleView = articleView;
        mContainerid = containerid;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void loadArticleInfo() {
        mServerArticleSource.getArticleInfo(mToken, mContainerid)
                .compose(new ErrorCheckerTransformer<Article>())
                .compose(new SchedulerTransformer<Article>())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mArticleView.showDialogLoading(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mArticleView.showDialogLoading(false);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<Article>(mArticleView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Article article) {
                        mArticleView.onArticleLoadSuccess(article);
                    }
                });
    }
}

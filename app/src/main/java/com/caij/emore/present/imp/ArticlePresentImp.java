package com.caij.emore.present.imp;

import com.caij.emore.bean.Article;
import com.caij.emore.present.ArticlePresent;
import com.caij.emore.remote.ArticleApi;
import com.caij.emore.ui.view.ArticleView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import rx.functions.Action0;

/**
 * Created by Caij on 2016/9/3.
 */
public class ArticlePresentImp extends AbsBasePresent implements ArticlePresent {

    private String mToken;
    private ArticleApi mServerArticleApi;
    private ArticleView mArticleView;
    private String mContainerid;

    public ArticlePresentImp(String token, String containerid, ArticleApi serverArticleApi, ArticleView articleView) {
        this.mToken = token;
        mServerArticleApi = serverArticleApi;
        mArticleView = articleView;
        mContainerid = containerid;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void loadArticleInfo() {
        mServerArticleApi.getArticleInfo(mToken, mContainerid)
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

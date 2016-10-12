package com.caij.emore.present.imp;

import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.Article;
import com.caij.emore.present.ArticlePresent;
import com.caij.emore.remote.ArticleApi;
import com.caij.emore.ui.view.ArticleView;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;

import rx.functions.Action0;

/**
 * Created by Caij on 2016/9/3.
 */
public class ArticlePresentImp extends AbsBasePresent implements ArticlePresent {

    private String mToken;
    private ArticleApi mServerArticleApi;
    private ArticleView mArticleView;
    private String mContainerId;

    public ArticlePresentImp(String token, String containerId, ArticleApi serverArticleApi, ArticleView articleView) {
        this.mToken = token;
        mServerArticleApi = serverArticleApi;
        mArticleView = articleView;
        mContainerId = containerId;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void loadArticleInfo() {
        mServerArticleApi.getArticleInfo(mToken, mContainerId)
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
                .subscribe(new ResponseSubscriber<Article>(mArticleView) {
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

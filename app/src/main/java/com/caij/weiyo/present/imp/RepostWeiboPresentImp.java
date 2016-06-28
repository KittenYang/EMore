package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.RepostWeiboPresent;
import com.caij.weiyo.present.view.RepostWeiboView;
import com.caij.weiyo.source.RepostSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class RepostWeiboPresentImp implements RepostWeiboPresent {

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    private RepostSource mRepostSource;
    private RepostWeiboView mRepostWeiboView;

    public RepostWeiboPresentImp(String token, long weiboId,
                                 RepostSource repostSource, RepostWeiboView repostWeiboView) {
        this.mToken = token;
        this.mWeiboId = weiboId;
        this.mRepostSource = repostSource;
        this.mRepostWeiboView = repostWeiboView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void repostWeibo(String status) {
        Subscription subscription = mRepostSource.commentForWeibo(mToken, status, mWeiboId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRepostWeiboView.showLoading(false);
                        mRepostWeiboView.onComnLoadError();
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mRepostWeiboView.showLoading(false);
                        mRepostWeiboView.onRepostSuccess(weibo);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

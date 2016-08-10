package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.RepostWeiboPresent;
import com.caij.emore.present.view.RepostWeiboView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultTransformer;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class RepostWeiboPresentImp implements RepostWeiboPresent {

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    private WeiboSource mRepostSource;
    private RepostWeiboView mRepostWeiboView;

    public RepostWeiboPresentImp(String token, long weiboId,
                                 WeiboSource repostSource, RepostWeiboView repostWeiboView) {
        this.mToken = token;
        this.mWeiboId = weiboId;
        this.mRepostSource = repostSource;
        this.mRepostWeiboView = repostWeiboView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void repostWeibo(String status) {
        Subscription subscription = mRepostSource.repostWeibo(mToken, status, mWeiboId)
                .compose(new DefaultTransformer<Weibo>())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mRepostWeiboView.showDialogLoading(false);
                        mRepostWeiboView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mRepostWeiboView.showDialogLoading(false);
                        mRepostWeiboView.onRepostSuccess(weibo);

                        RxBus.getDefault().post(Event.EVENT_REPOST_WEIBO_SUCCESS, weibo);
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

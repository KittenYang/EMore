package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.bean.event.RepostStatusEvent;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.RepostWeiboPresent;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.RepostWeiboView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class RepostWeiboPresentImp extends AbsBasePresent implements RepostWeiboPresent {

    private String mToken;
    private long mWeiboId;
    private StatusApi mStatusApi;
    private RepostWeiboView mRepostWeiboView;

    public RepostWeiboPresentImp(String token, long weiboId,
                                 StatusApi statusApi, RepostWeiboView repostWeiboView) {
        this.mToken = token;
        this.mWeiboId = weiboId;
        this.mStatusApi = statusApi;
        this.mRepostWeiboView = repostWeiboView;
    }

    @Override
    public void repostWeibo(String status) {
        Subscription subscription = mStatusApi.repostWeibo(status, mWeiboId)
                .compose(new DefaultTransformer<Weibo>())
                .subscribe(new DefaultResponseSubscriber<Weibo>(mRepostWeiboView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mRepostWeiboView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mRepostWeiboView.showDialogLoading(false);
                        mRepostWeiboView.onRepostSuccess(weibo);

                        RepostStatusEvent repostStatusEvent = new RepostStatusEvent(EventTag.EVENT_REPOST_WEIBO_SUCCESS, weibo, mWeiboId);
                        RxBus.getDefault().post(repostStatusEvent.type, repostStatusEvent);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {

    }

}

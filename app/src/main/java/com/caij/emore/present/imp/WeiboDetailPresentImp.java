package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.StatusAttitudeCountUpdateEvent;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.dao.StatusManager;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboDetailPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.WeiboDetailView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/14.
 */
public class WeiboDetailPresentImp extends AbsTimeLinePresent<WeiboDetailView> implements WeiboDetailPresent {

    private long mWeiboId;

    public WeiboDetailPresentImp(long weiboId, WeiboDetailView view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mWeiboId = weiboId;
    }

    @Override
    public void loadWeiboDetail() {
        Observable<Weibo> localObservable = Observable.<Weibo>create(new Observable.OnSubscribe<Weibo>() {
            @Override
            public void call(Subscriber<? super Weibo> subscriber) {
                subscriber.onNext(mStatusManager.getWeiboById(mWeiboId));
                subscriber.onCompleted();
            }
        });

        Observable<Weibo> serverObservable = mStatusApi.getWeiboById(1, mWeiboId)
                .compose(ErrorCheckerTransformer.<Weibo>create())
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mStatusManager.saveWeibo(weibo);
                    }
                });
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return weibo != null && weibo.getUpdate_time() != null
                                && System.currentTimeMillis() - weibo.getUpdate_time() < 2 * 60 * 60 * 1000
                                && (!weibo.getIsLongText() || weibo.getLongText() != null);
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        doSpanNext(weibo);
                    }
                })
                .compose(SchedulerTransformer.<Weibo>create())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(false);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<Weibo>(mView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mView.setWeibo(weibo);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void refreshWeiboDetail() {
        StatusRefreshEvent event = new StatusRefreshEvent(EventTag.EVENT_STATUS_REFRESH, mWeiboId);
        RxBus.getDefault().post(event.type, event);

        Subscription subscription = mStatusApi.getWeiboById(1, mWeiboId)
                .compose(ErrorCheckerTransformer.<Weibo>create())
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mStatusManager.saveWeibo(weibo);
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        doSpanNext(weibo);
                    }
                })
                .compose(SchedulerTransformer.<Weibo>create())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.onRefreshComplete();
                    }
                }).subscribe(new DefaultResponseSubscriber<Weibo>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mView.setWeibo(weibo);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    protected void doSpanNext(Weibo weibo) {
        doSpanNext(weibo, true);
    }

    @Override
    protected void onStatusAttitudeCountUpdate(StatusAttitudeCountUpdateEvent event) {
        if (event.statusId == mWeiboId) {
            mView.onStatusAttitudeCountUpdate(event.count);
        }
    }

    @Override
    protected void onStatusAttitudeUpdate(StatusAttitudeEvent event) {
        if (event.statusId == mWeiboId) {
            mView.onStatusAttitudeUpdate(event.isAttitude);
        }
    }

}

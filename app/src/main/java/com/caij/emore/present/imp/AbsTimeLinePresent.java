package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.R;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.StatusAttitudeCountUpdateEvent;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.dao.StatusManager;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboActionPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.WeiboActionView;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;


import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/7/2.
 */
public abstract class AbsTimeLinePresent<V extends WeiboActionView> extends AbsBasePresent
        implements WeiboActionPresent, Action1<Event>{

    protected V mView;
    protected StatusApi mStatusApi;
    protected StatusManager mStatusManager;
    protected AttitudeApi mAttitudeApi;

    private Observable<Event> mStatusAttitudeCountObservable;
    private Observable<Event> mAttitudeObservable;

    public AbsTimeLinePresent(V view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super();
        mView = view;
        mStatusApi = statusApi;
        mStatusManager = statusManager;
        mAttitudeApi = attitudeApi;
    }

    @Override
    public void onCreate() {
       registerEvent();
    }

    private void registerEvent() {
        mStatusAttitudeCountObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE);
        mStatusAttitudeCountObservable.subscribe(this);

        mAttitudeObservable = RxBus.getDefault().register(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS);
        mAttitudeObservable.subscribe(this);
    }

    @Override
    public void call(Event event) {
        if (EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE.equals(event.type)) {
            onStatusAttitudeCountUpdate((StatusAttitudeCountUpdateEvent) event);
        }else if (EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS.equals(event.type)) {
            onStatusAttitudeUpdate((StatusAttitudeEvent) event);
        }
    }

    protected abstract void onStatusAttitudeCountUpdate(StatusAttitudeCountUpdateEvent event);

    protected abstract void onStatusAttitudeUpdate(StatusAttitudeEvent event);

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE, mStatusAttitudeCountObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS, mAttitudeObservable);
    }

    @Override
    public void deleteWeibo(final Weibo deleteWeibo, final int position) {
        mView.showDialogLoading(true, R.string.deleting);
        Subscription subscription =  mStatusApi.deleteWeibo(deleteWeibo.getId())
                .compose(ErrorCheckerTransformer.<Weibo>create())
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mStatusManager.deleteWeibo(weibo.getId());
                    }
                })
                .compose(SchedulerTransformer.<Weibo>create())
                .subscribe(new DefaultResponseSubscriber<Weibo>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false, R.string.deleting);
                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        mView.onDeleteWeiboSuccess(deleteWeibo, position);
                        mView.showDialogLoading(false, R.string.deleting);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void collectWeibo(final Weibo weibo) {
        Subscription subscription = mStatusApi.collectWeibo(weibo.getId())
                .compose(ErrorCheckerTransformer.<FavoritesCreateResponse>create())
                .doOnNext(new Action1<FavoritesCreateResponse>() {
                    @Override
                    public void call(FavoritesCreateResponse favoritesCreateResponse) {
                        weibo.setFavorited(true);
                        mStatusManager.saveWeibo(weibo);
                    }
                })
                .compose(SchedulerTransformer.<FavoritesCreateResponse>create())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(true, R.string.collecting);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(false, R.string.collecting);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<FavoritesCreateResponse>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(FavoritesCreateResponse favoritesCreateResponse) {
                        mView.onCollectSuccess(weibo);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void uncollectWeibo(final Weibo weibo) {
        Subscription subscription = mStatusApi.uncollectWeibo(weibo.getId())
                .compose(ErrorCheckerTransformer.<FavoritesCreateResponse>create())
                .doOnNext(new Action1<FavoritesCreateResponse>() {
                    @Override
                    public void call(FavoritesCreateResponse favoritesCreateResponse) {
                        weibo.setFavorited(false);
                        mStatusManager.saveWeibo(weibo);
                    }
                })
                .compose(SchedulerTransformer.<FavoritesCreateResponse>create())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(true, R.string.uncollecting);
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(false, R.string.uncollecting);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<FavoritesCreateResponse>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(FavoritesCreateResponse favoritesCreateResponse) {
                        mView.onUncollectSuccess(weibo);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void attitudesWeibo(final Weibo weibo) {
        Subscription subscription = mAttitudeApi.attitudesToWeibo("smile", weibo.getId())
                .compose(ErrorCheckerTransformer.<Attitude>create())
                .doOnNext(new Action1<Attitude>() {
                    @Override
                    public void call(Attitude attitude) {
                        weibo.setAttitudes_status(1);
                        mStatusManager.saveWeibo(weibo);
                    }
                })
                .compose(SchedulerTransformer.<Attitude>create())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(true, R.string.requesting);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(false, R.string.requesting);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<Attitude>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                    }

                    @Override
                    public void onNext(Attitude attitude) {
                        Event attitudeSuccessEvent = new StatusAttitudeEvent(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS,
                                weibo.getId(), true, attitude.getUser());
                        RxBus.getDefault().post(attitudeSuccessEvent.type, attitudeSuccessEvent);

                        StatusAttitudeCountUpdateEvent statusAttitudeCountUpdateEvent =
                                new StatusAttitudeCountUpdateEvent(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE, weibo.getId(),
                                        attitude.getStatus().getAttitudes_count());
                        RxBus.getDefault().post(statusAttitudeCountUpdateEvent.type, statusAttitudeCountUpdateEvent);
                    }
                });
        addSubscription(subscription);
    }


    @Override
    public void destroyAttitudesWeibo(final Weibo weibo) {
        Subscription subscription = mAttitudeApi.destoryAttitudesWeibo("smile", weibo.getId())
                .compose(ErrorCheckerTransformer.create())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        weibo.setAttitudes_status(0);
                        mStatusManager.saveWeibo(weibo);
                    }
                })
                .compose(SchedulerTransformer.<Response>create())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(true, R.string.requesting);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(false, R.string.requesting);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<Response>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Response response) {
                        StatusAttitudeCountUpdateEvent attitudeCountEvent =
                                new StatusAttitudeCountUpdateEvent(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE,
                                        weibo.getId(), weibo.getAttitudes_count() - 1);
                        RxBus.getDefault().post(attitudeCountEvent.type, attitudeCountEvent);

                        StatusAttitudeEvent attitudeSuccessEvent = new StatusAttitudeEvent(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS,
                                weibo.getId(), false, null);
                        RxBus.getDefault().post(attitudeSuccessEvent.type, attitudeSuccessEvent);
                    }
                });
        addSubscription(subscription);
    }

    protected void doSpanNext(List<Weibo> weibos) {
        for (Weibo weibo : weibos) {
            SpannableStringUtil.paraeSpannable(weibo);
        }
    }

    protected void doSpanNext(Weibo weibo) {
        doSpanNext(weibo, false);
    }


    protected void doSpanNext(Weibo weibo, boolean isLongText) {
        SpannableStringUtil.paraeSpannable(weibo, isLongText);
    }

}

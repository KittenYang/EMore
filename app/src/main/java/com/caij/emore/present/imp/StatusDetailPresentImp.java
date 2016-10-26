package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.event.StatusActionCountUpdateEvent;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.StatusDetailPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.StatusDetailView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/14.
 */
public class StatusDetailPresentImp extends AbsTimeLinePresent<StatusDetailView> implements StatusDetailPresent {

    private static final long STATUS_CACHE_TIME = 60 * 60 * 1000;

    private long mStatusId;

    public StatusDetailPresentImp(long statusId, StatusDetailView view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mStatusId = statusId;
    }

    @Override
    public void loadStatusDetail() {
        Observable<Status> localObservable = RxUtil.createDataObservable(new RxUtil.Provider<Status>() {
            @Override
            public Status getData() {
                return mStatusManager.getStatusById(mStatusId);
            }
        });

        Observable<Status> serverObservable = mStatusApi.getWeiboById(1, mStatusId)
                .compose(ErrorCheckerTransformer.<Status>create())
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status weibo) {
                        mStatusManager.saveStatus(weibo);
                    }
                });
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<Status, Boolean>() {
                    @Override
                    public Boolean call(Status weibo) {
                        return weibo != null && weibo.getUpdate_time() != null
                                && System.currentTimeMillis() - weibo.getUpdate_time() < STATUS_CACHE_TIME
                                && (!weibo.getIsLongText() || weibo.getLongText() != null);
                    }
                })
                .compose(StatusContentSpannableConvertTransformer.create(true))
                .compose(SchedulerTransformer.<Status>create())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showDialogLoading(true);
                    }
                })

                .subscribe(new ResponseSubscriber<Status>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Status status) {
                        mView.showDialogLoading(false);
                        mView.setStatus(status);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void refreshStatusDetail() {
        StatusRefreshEvent event = new StatusRefreshEvent(EventTag.EVENT_STATUS_REFRESH, mStatusId);
        RxBus.getDefault().post(event.type, event);

        Subscription subscription = mStatusApi.getWeiboById(1, mStatusId)
                .compose(ErrorCheckerTransformer.<Status>create())
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status status) {
                        mStatusManager.saveStatus(status);
                    }
                })
                .compose(StatusContentSpannableConvertTransformer.create(true))
                .compose(SchedulerTransformer.<Status>create())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.onRefreshComplete();
                    }
                }).subscribe(new ResponseSubscriber<Status>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Status status) {
                        mView.setStatus(status);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    protected void onStatusAttitudeCountUpdate(StatusActionCountUpdateEvent event) {
        if (event.statusId == mStatusId) {
            mView.onStatusAttitudeCountUpdate(event.count);
        }
    }

    @Override
    protected void onStatusAttitudeUpdate(StatusAttitudeEvent event) {
        if (event.statusId == mStatusId) {
            mView.onStatusAttitudeUpdate(event.isAttitude);
        }
    }

    @Override
    protected void onStatusCommentCountUpdate(StatusActionCountUpdateEvent event) {
        if (event.statusId == mStatusId) {
            mView.onStatusCommentCountUpdate(event.count);
        }
    }

    @Override
    protected void onStatusRelayCountUpdate(StatusActionCountUpdateEvent event) {
        if (event.statusId == mStatusId) {
            mView.onStatusRelayCountUpdate(event.count);
        }
    }

}

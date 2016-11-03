package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.R;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.StatusActionCountUpdateEvent;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.StatusActionPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.WeiboActionView;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;


import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/7/2.
 */
public abstract class AbsTimeLinePresent<V extends WeiboActionView> extends AbsBasePresent
        implements StatusActionPresent, Action1<Event>{

    protected V mView;
    protected StatusApi mStatusApi;

    private AttitudeApi mAttitudeApi;
    StatusManager mStatusManager;

    private Observable<Event> mAttitudeObservable;
    private Observable<StatusActionCountUpdateEvent> mStatusAttitudeCountObservable;
    private Observable<StatusActionCountUpdateEvent> mStatusCommentCountObservable;
    private Observable<StatusActionCountUpdateEvent> mStatusRelayCountObservable;

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
        mAttitudeObservable = RxBus.getDefault().register(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS);
        mAttitudeObservable.subscribe(this);

        mStatusAttitudeCountObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE);
        addSubscription(mStatusAttitudeCountObservable.doOnNext(new Action1<StatusActionCountUpdateEvent>() {
            @Override
            public void call(StatusActionCountUpdateEvent statusActionCountUpdateEvent) {
                Status status = mStatusManager.getStatusById(statusActionCountUpdateEvent.statusId);
                if (status != null) {
                    status.setAttitudes_count(statusActionCountUpdateEvent.count);
                    mStatusManager.saveStatus(status);
                }
            }
        }).compose(SchedulerTransformer.<Event>create()).subscribe(this));

        mStatusCommentCountObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_COMMENT_COUNT_UPDATE);
        addSubscription(mStatusCommentCountObservable.doOnNext(new Action1<StatusActionCountUpdateEvent>() {
            @Override
            public void call(StatusActionCountUpdateEvent statusActionCountUpdateEvent) {
                Status status = mStatusManager.getStatusById(statusActionCountUpdateEvent.statusId);
                if (status != null) {
                    status.setComments_count(statusActionCountUpdateEvent.count);
                    mStatusManager.saveStatus(status);
                }
            }
        }).compose(SchedulerTransformer.<StatusActionCountUpdateEvent>create()).subscribe(this));

        mStatusRelayCountObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_RELAY_COUNT_UPDATE);
        addSubscription(mStatusRelayCountObservable.doOnNext(new Action1<StatusActionCountUpdateEvent>() {
            @Override
            public void call(StatusActionCountUpdateEvent statusActionCountUpdateEvent) {
                Status status = mStatusManager.getStatusById(statusActionCountUpdateEvent.statusId);
                if (status != null) {
                    status.setReposts_count(statusActionCountUpdateEvent.count);
                    mStatusManager.saveStatus(status);
                }
            }
        }).compose(SchedulerTransformer.<StatusActionCountUpdateEvent>create()).subscribe(this));
    }

    @Override
    public void call(Event event) {
        if (EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE.equals(event.type)) {
            onStatusAttitudeCountUpdate((StatusActionCountUpdateEvent) event);
        }else if (EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS.equals(event.type)) {
            onStatusAttitudeUpdate((StatusAttitudeEvent) event);
        }else if (EventTag.EVENT_STATUS_COMMENT_COUNT_UPDATE.equals(event.type)) {
            onStatusCommentCountUpdate((StatusActionCountUpdateEvent) event);
        }else if (EventTag.EVENT_STATUS_RELAY_COUNT_UPDATE.equals(event.type)) {
            onStatusRelayCountUpdate((StatusActionCountUpdateEvent) event);
        }
    }

    protected abstract void onStatusAttitudeCountUpdate(StatusActionCountUpdateEvent event);

    protected abstract void onStatusAttitudeUpdate(StatusAttitudeEvent event);

    protected abstract void onStatusCommentCountUpdate(StatusActionCountUpdateEvent event);

    protected abstract void onStatusRelayCountUpdate(StatusActionCountUpdateEvent event);

    @Override
    public void deleteStatus(final Status deleteStatus, final int position) {
        mView.showDialogLoading(true, R.string.deleting);
        Subscription subscription =  mStatusApi.deleteStatusById(deleteStatus.getId())
                .compose(ErrorCheckerTransformer.<Status>create())
                .doOnNext(new Action1<Status>() {
                    @Override
                    public void call(Status weibo) {
                        mStatusManager.deleteStatus(weibo.getId());
                    }
                })
                .compose(SchedulerTransformer.<Status>create())
                .subscribe(new ResponseSubscriber<Status>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.showDialogLoading(false, R.string.deleting);
                    }

                    @Override
                    public void onNext(Status status) {
                        mView.onDeleteStatusSuccess(deleteStatus, position);
                        mView.showDialogLoading(false, R.string.deleting);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void collectStatus(final Status status) {
        Subscription subscription = mStatusApi.collectStatus(status.getId())
                .compose(ErrorCheckerTransformer.<FavoritesCreateResponse>create())
                .doOnNext(new Action1<FavoritesCreateResponse>() {
                    @Override
                    public void call(FavoritesCreateResponse favoritesCreateResponse) {
                        status.setFavorited(true);
                        mStatusManager.saveStatus(status);
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
                .subscribe(new ResponseSubscriber<FavoritesCreateResponse>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(FavoritesCreateResponse favoritesCreateResponse) {
                        mView.onCollectSuccess(status);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void unCollectStatus(final Status status) {
        Subscription subscription = mStatusApi.unCollectStatus(status.getId())
                .compose(ErrorCheckerTransformer.<FavoritesCreateResponse>create())
                .doOnNext(new Action1<FavoritesCreateResponse>() {
                    @Override
                    public void call(FavoritesCreateResponse favoritesCreateResponse) {
                        status.setFavorited(false);
                        mStatusManager.saveStatus(status);
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
                .subscribe(new ResponseSubscriber<FavoritesCreateResponse>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(FavoritesCreateResponse favoritesCreateResponse) {
                        mView.onUnCollectSuccess(status);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void attitudeStatus(final Status status) {
        Subscription subscription = mAttitudeApi.attitudesToStatus("smile", status.getId())
                .compose(ErrorCheckerTransformer.<Attitude>create())
                .doOnNext(new Action1<Attitude>() {
                    @Override
                    public void call(Attitude attitude) {
                        status.setAttitudes_status(1);
                        mStatusManager.saveStatus(status);
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
                .subscribe(new ResponseSubscriber<Attitude>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                    }

                    @Override
                    public void onNext(Attitude attitude) {
                        Event attitudeSuccessEvent = new StatusAttitudeEvent(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS,
                                status.getId(), true, attitude.getUser());
                        RxBus.getDefault().post(attitudeSuccessEvent.type, attitudeSuccessEvent);

                        StatusActionCountUpdateEvent statusActionCountUpdateEvent =
                                new StatusActionCountUpdateEvent(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE, status.getId(),
                                        attitude.getStatus().getAttitudes_count());
                        RxBus.getDefault().post(statusActionCountUpdateEvent.type, statusActionCountUpdateEvent);
                    }
                });
        addSubscription(subscription);
    }


    @Override
    public void destroyAttitudeStatus(final Status status) {
        Subscription subscription = mAttitudeApi.destroyAttitudesStatus("smile", status.getId())
                .compose(ErrorCheckerTransformer.create())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        status.setAttitudes_status(0);
                        mStatusManager.saveStatus(status);
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
                .subscribe(new ResponseSubscriber<Response>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Response response) {
                        StatusActionCountUpdateEvent attitudeCountEvent =
                                new StatusActionCountUpdateEvent(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE,
                                        status.getId(), status.getAttitudes_count() - 1);
                        RxBus.getDefault().post(attitudeCountEvent.type, attitudeCountEvent);

                        StatusAttitudeEvent attitudeSuccessEvent = new StatusAttitudeEvent(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS,
                                status.getId(), false, null);
                        RxBus.getDefault().post(attitudeSuccessEvent.type, attitudeSuccessEvent);
                    }
                });
        addSubscription(subscription);
    }

    private void unregisterEvent() {
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE, mStatusAttitudeCountObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS, mAttitudeObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_COMMENT_COUNT_UPDATE, mStatusCommentCountObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_RELAY_COUNT_UPDATE, mStatusRelayCountObservable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEvent();
    }

    public static class StatusContentSpannableConvertTransformer
            implements Observable.Transformer<Status, Status> {

        private boolean isLongText;

        public static StatusContentSpannableConvertTransformer create(boolean isLongText) {
            return new StatusContentSpannableConvertTransformer(isLongText);
        }

        public StatusContentSpannableConvertTransformer( boolean isLongText) {
            this.isLongText = isLongText;
        }

        @Override
        public Observable<Status> call(Observable<Status> statusObservable) {
            return statusObservable.doOnNext(new Action1<Status>() {
                @Override
                public void call(Status status) {
                    SpannableStringUtil.formatSpannable(status, isLongText);
                }
            });
        }
    }


}

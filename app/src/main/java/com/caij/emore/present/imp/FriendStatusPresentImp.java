package com.caij.emore.present.imp;

import com.caij.emore.AppApplication;
import com.caij.emore.EventTag;
import com.caij.emore.Key;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.FriendStatusView;
import com.caij.emore.utils.SPUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class FriendStatusPresentImp extends AbsListTimeLinePresent<FriendStatusView> implements FriendWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private final static long STATUS_REFRESH_INTERVAL = 60 * 60 * 1000;

    private NotifyManager mNotifyManager;

    private Observable<Status> mPublishStatusObservable;

    private long mUid;

    private long mNextCursor;

    public FriendStatusPresentImp(long uid, FriendStatusView view, StatusApi statusApi,
                                  StatusManager statusManager, AttitudeApi attitudeApi,
                                  NotifyManager notifyManager) {
        super(view, statusApi, statusManager, attitudeApi);
        mNotifyManager = notifyManager;
        mUid = uid;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerEvent();
        getLocalFriendStatus();
    }

    private void registerEvent() {
        mPublishStatusObservable = RxBus.getDefault().register(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS);
        mPublishStatusObservable.compose(StatusContentSpannableConvertTransformer.create(false))
                    .compose(SchedulerTransformer.<Status>create())
                    .subscribe(new Action1<Status>() {
                        @Override
                        public void call(Status status) {
                            mView.onWeiboPublishSuccess(status);
                        }
                    });
    }

    private void getLocalFriendStatus() {
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<List<Status>>() {
            @Override
            public List<Status> getData() {
                return mStatusManager.getFriendStatuses(mUid, 0, 0, PAGE_COUNT , 1);
            }
        }).flatMap(new Func1<List<Status>, Observable<Status>>() {
            @Override
            public Observable<Status> call(List<Status> statuses) {
                return Observable.from(statuses);
            }
        })
        .compose(StatusContentSpannableConvertTransformer.create(false))
        .toList()
        .compose(SchedulerTransformer.<List<Status>>create())
        .subscribe(new SubscriberAdapter<List<Status>>() {

            @Override
            public void onError(Throwable e) {
                mView.toRefresh();
            }

            @Override
            public void onNext(List<Status> statuses) {
               onGetLocalStatusSuccess(statuses);
            }
        });
        addSubscription(subscription);
    }

    private void onGetLocalStatusSuccess(List<Status> statuses) {
        mStatuses.addAll(statuses);
        mView.setEntities(mStatuses);

        if (statuses.size() > 1) {
            mNextCursor = statuses.get(statuses.size() - 1).getId();
        }else {
            mNextCursor = 0;
        }

        long preRefreshTime  = new SPUtil.SPBuilder(AppApplication.getInstance())
                .openDefault()
                .getLong(Key.FRIEND_WEIBO_UPDATE_TIME, -1);
        if (System.currentTimeMillis() - preRefreshTime > STATUS_REFRESH_INTERVAL
                || statuses.size() <= 0) {
            mView.toRefresh();
        }

        if (statuses.size() >= PAGE_COUNT){
            mView.onLoadComplete(true);
        }
    }

    @Override
    public void refresh() {
        Subscription subscription = getFriendStatusesObservable(0)
                .doOnNext(new Action1<List<Status>>() {
                    @Override
                    public void call(List<Status> weibos) {
                       saveStatusRefreshTime();
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.onRefreshComplete();
                    }
                })
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.clear();
                        mStatuses.addAll(statuses);
                        mView.setEntities(mStatuses);

                        mView.onLoadComplete(statuses.size() >= PAGE_COUNT);

                        MessageUtil.resetLocalUnReadMessage(UnReadMessage.TYPE_STATUS, 0, mUid, mNotifyManager);
                    }
                });
        addSubscription(subscription);
    }

    private void saveStatusRefreshTime() {
        new SPUtil.SPBuilder(AppApplication.getInstance())
                .openDefault().edit()
                .putLong(Key.FRIEND_WEIBO_UPDATE_TIME, System.currentTimeMillis())
                .apply();
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = getFriendStatusesObservable(mNextCursor)
            .subscribe(new ResponseSubscriber<List<Status>>(mView) {

//                @Override
                protected void onFail(Throwable e) {
                    mView.onLoadComplete(true);
                }

                @Override
                public void onNext(List<Status> statuses) {
                    mStatuses.addAll(statuses);
                    mView.notifyItemRangeInserted(mStatuses, mStatuses.size() - statuses.size(), statuses.size());
                    mView.onLoadComplete(statuses.size() >= PAGE_COUNT);
                }
            });
        addSubscription(subscription);
    }

    private Observable<List<Status>> getFriendStatusesObservable(long maxId) {
        return mStatusApi.getFriendWeibo(mUid, 0, maxId, PAGE_COUNT, 1)
                .compose(ErrorCheckerTransformer.<QueryStatusResponse>create())
                .flatMap(new Func1<QueryStatusResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(QueryStatusResponse response) {
                        mNextCursor = response.getNext_cursor();
                        return Observable.from(response.getStatuses());
                    }
                })
                .compose(StatusContentSpannableConvertTransformer.create(false))
                .toList()
                .doOnNext(new Action1<List<Status>>() {
                    @Override
                    public void call(List<Status> statuses) {
                        mStatusManager.saveStatuses(statuses);
                    }
                })
                .compose(SchedulerTransformer.<List<Status>>create());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS, mPublishStatusObservable);
    }

}

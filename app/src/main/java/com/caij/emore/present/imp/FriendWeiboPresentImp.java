package com.caij.emore.present.imp;

import com.caij.emore.AppApplication;
import com.caij.emore.EventTag;
import com.caij.emore.Key;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.dao.NotifyManager;
import com.caij.emore.dao.StatusManager;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.FriendWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.SPUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class FriendWeiboPresentImp extends AbsListTimeLinePresent<FriendWeiboView> implements FriendWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private final static long WEIBO_REFRESH_INTERVAL = 60 * 60 * 1000;

    private NotifyManager mNotifyManager;

    private Observable<Weibo> mPublishWeiboObservable;

    private long mUid;

    private long mNextCursor;

    public FriendWeiboPresentImp(long uid, FriendWeiboView view, StatusApi statusApi,
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
        mPublishWeiboObservable = RxBus.getDefault().register(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS);
        mPublishWeiboObservable.doOnNext(new Action1<Weibo>() {
                @Override
                public void call(Weibo weibo) {
                    doSpanNext(weibo);
                }
            })
            .compose(SchedulerTransformer.<Weibo>create())
            .subscribe(new Action1<Weibo>() {
                @Override
                public void call(Weibo weibo) {
                    mView.onWeiboPublishSuccess(weibo);
                }
            });
    }

    private void getLocalFriendStatus() {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<List<Weibo>>() {
            @Override
            public void call(Subscriber<? super List<Weibo>> subscriber) {
                subscriber.onNext(mStatusManager.getFriendWeibo(mUid, 0, 0, PAGE_COUNT , 1));
                subscriber.onCompleted();
            }
        }).doOnNext(new Action1<List<Weibo>>() {
            @Override
            public void call(List<Weibo> weibos) {
                doSpanNext(weibos);
            }
        })
        .compose(new SchedulerTransformer<List<Weibo>>())
        .subscribe(new SubscriberAdapter<List<Weibo>>() {

            @Override
            public void onError(Throwable e) {
                mView.toRefresh();
            }

            @Override
            public void onNext(List<Weibo> weibos) {
                mWeibos.addAll(weibos);
                mView.setEntities(mWeibos);

                if (weibos.size() > 1) {
                    mNextCursor = weibos.get(weibos.size() - 1).getId();
                }else {
                    mNextCursor = 0;
                }

                long preRefreshTime  = new SPUtil.SPBuilder(AppApplication.getInstance())
                        .openDefault()
                        .getLong(Key.FRIEND_WEIBO_UPDATE_TIME, -1);
                if (System.currentTimeMillis() - preRefreshTime > WEIBO_REFRESH_INTERVAL
                        || weibos.size() <= 0) {
                    mView.toRefresh();
                }

                if (weibos.size() >= PAGE_COUNT){
                    mView.onLoadComplete(true);
                }
            }
        });
        addSubscription(subscription);
    }

    @Override
    public void refresh() {
        Subscription subscription = getFriendWeiboObservable(0)
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                       saveStatusRefreshTime();
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mView.onRefreshComplete();
                    }
                })
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1);

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
        Subscription subscription = getFriendWeiboObservable(mNextCursor)
            .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {

                        @Override
                        protected void onFail(Throwable e) {
                            mView.onLoadComplete(true);
                        }

                        @Override
                        public void onNext(List<Weibo> weibos) {
                            mWeibos.addAll(weibos);
                            mView.notifyItemRangeInserted(mWeibos, mWeibos.size() - weibos.size(), weibos.size());
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                        }
                    });
        addSubscription(subscription);
    }

    private Observable<List<Weibo>> getFriendWeiboObservable(long maxId) {
        return mStatusApi.getFriendWeibo(mUid, 0, maxId, PAGE_COUNT, 1)
                .compose(ErrorCheckerTransformer.<QueryWeiboResponse>create())
                .flatMap(new Func1<QueryWeiboResponse, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(QueryWeiboResponse response) {
                        mNextCursor = response.getNext_cursor();
                        return Observable.just(response.getStatuses());
                    }
                })
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        doSpanNext(weibos);
                        mStatusManager.saveWeibos(weibos);
                    }
                })
                .compose(SchedulerTransformer.<List<Weibo>>create());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_PUBLISH_WEIBO_SUCCESS, mPublishWeiboObservable);
    }

}

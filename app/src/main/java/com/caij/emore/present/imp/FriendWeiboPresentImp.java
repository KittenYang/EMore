package com.caij.emore.present.imp;

import com.caij.emore.AppApplication;
import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.account.Account;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.ui.view.FriendWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.SPUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
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

    private Observable<Weibo> mPublishWeiboObservable;
    private MessageSource mLocalMessageSource;

    private long mNextCursor;

    public FriendWeiboPresentImp(Account account, FriendWeiboView view, WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource, MessageSource localMessageSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mLocalMessageSource = localMessageSource;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initEvent();
        getLocalFriendWeibos();
    }

    private void initEvent() {
        mPublishWeiboObservable = RxBus.getDefault().register(Event.EVENT_PUBLISH_WEIBO_SUCCESS);
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

    private void getLocalFriendWeibos() {
        Subscription subscription = mLocalWeiboSource.getFriendWeibo(mAccount.getToken().getAccess_token(), mAccount.getUid(),
                0, 0, PAGE_COUNT , 1)
                .flatMap(new Func1<QueryWeiboResponse, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(QueryWeiboResponse response) {
                        return Observable.just(response.getStatuses());
                    }
                })
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        doSpanNext(weibos);
                    }
                })
                .compose(new SchedulerTransformer<List<Weibo>>())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

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

                        if (weibos.size() > PAGE_COUNT){
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
                        new SPUtil.SPBuilder(AppApplication.getInstance())
                                .openDefault().edit()
                                .putLong(Key.FRIEND_WEIBO_UPDATE_TIME, System.currentTimeMillis())
                                .apply();
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

                        MessageUtil.resetLocalUnReadMessage(mAccount.getToken().getAccess_token(),
                                UnReadMessage.TYPE_STATUS, 0, mAccount.getUid(), mLocalMessageSource);
                    }
                });
        addSubscription(subscription);
    }


    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = getFriendWeiboObservable(mNextCursor)
            .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                        @Override
                        public void onCompleted() {

                        }

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
        return mServerWeiboSource.getFriendWeibo(mAccount.getToken().getAccess_token(), mAccount.getUid(),
                0, maxId, PAGE_COUNT, 1)
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
                        mLocalWeiboSource.saveWeibos(mAccount.getToken().getAccess_token(), weibos);
                    }
                })
                .compose(new SchedulerTransformer<List<Weibo>>());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(Event.EVENT_PUBLISH_WEIBO_SUCCESS, mPublishWeiboObservable);
    }

}

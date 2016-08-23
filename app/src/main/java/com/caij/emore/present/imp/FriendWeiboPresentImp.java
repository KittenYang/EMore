package com.caij.emore.present.imp;

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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/5/31.
 */
public class FriendWeiboPresentImp extends AbsListTimeLinePresent<FriendWeiboView> implements FriendWeiboPresent {

    private final static int PAGE_COUNT = 20;

    Observable<Weibo> mPublishWeiboObservable;
    MessageSource mLocalMessageSource;

    public FriendWeiboPresentImp(Account account, FriendWeiboView view, WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource, MessageSource localMessageSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mLocalMessageSource = localMessageSource;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Subscription subscription = mLocalWeiboSource.getFriendWeibo(mAccount.getEmoreToken().getAccess_token(), mAccount.getUid(),
                0, 0, PAGE_COUNT * 2, 1)
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        return weibo;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        doSpanNext(weibos);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

                        if (System.currentTimeMillis() -
                                SPUtil.getLong(Key.FRIEND_WEIBO_UPDATE_TIME + mAccount.getUsername(), -1) > 30 * 60 * 1000 ||
                                weibos.size() <= 0) {
                            mView.toRefresh();
                        }

                        if (weibos.size() > 5){
                            mView.onLoadComplete(true);
                        }
                    }
                });
        addSubscription(subscription);

        mPublishWeiboObservable = RxBus.getDefault().register(Event.EVENT_PUBLISH_WEIBO_SUCCESS);
        mPublishWeiboObservable.doOnNext(new Action1<Weibo>() {
                @Override
                public void call(Weibo weibo) {
                    doSpanNext(weibo);
                }
            })
            .compose(new SchedulerTransformer<Weibo>())
            .subscribe(new Action1<Weibo>() {
                @Override
                public void call(Weibo weibo) {
                    mView.onWeiboPublishSuccess(weibo);
                }
             });
    }

    @Override
    public void refresh() {
        Subscription subscription = createObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplete();
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1);

                        MessageUtil.resetLocalUnReadMessage(mAccount.getWeiCoToken().getAccess_token(),
                                UnReadMessage.TYPE_STATUS, 0, mAccount.getUid(), mLocalMessageSource);

                        SPUtil.saveLong(Key.FRIEND_WEIBO_UPDATE_TIME + mAccount.getUsername(), System.currentTimeMillis());
                    }
                });
        addSubscription(subscription);
    }


    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription subscription = createObservable(maxId, false)
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
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 2); //这里有一条重复的 所以需要-1
                        }
                    });
        addSubscription(subscription);
    }

    private Observable<List<Weibo>> createObservable(long maxId, final boolean isRefresh) {
        return mServerWeiboSource.getFriendWeibo(mAccount.getWeiCoToken().getAccess_token(), mAccount.getUid(),
                0, maxId, PAGE_COUNT, 1)
                .compose(new ErrorCheckerTransformer<QueryWeiboResponse>())
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return isRefresh || !mWeibos.contains(weibo);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        return weibo;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        mLocalWeiboSource.saveWeibos(mAccount.getEmoreToken().getAccess_token(), weibos);
                        doSpanNext(weibos);
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

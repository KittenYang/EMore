package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.ui.view.WeiboAttitudesView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboAttitudesPresentImp implements WeiboRepostsPresent {

    private static final int PAGE_COUNET = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    WeiboSource mServerWeiboSource;
    WeiboSource mLocalWeiboSource;
    WeiboAttitudesView mView;
    List<Attitude> mAttitudes;
    private int mPage;
    private Observable<Attitude> mAttitudeObservable;
    Observable<List<Attitude>> mWeiboRefreshObservable;

    public WeiboAttitudesPresentImp(String token, long weiboId,
                                    WeiboSource serverWeiboSource,
                                    WeiboSource localWeiboSource,
                                    WeiboAttitudesView view) {
        mToken = token;
        mServerWeiboSource = serverWeiboSource;
        mLocalWeiboSource = localWeiboSource;
        mView = view;
        mWeiboId = weiboId;
        mLoginCompositeSubscription = new CompositeSubscription();
        mAttitudes = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mAttitudeObservable = RxBus.getDefault().register(Event.EVENT_ATTITUDE_WEIBO_SUCCESS);
        mAttitudeObservable.filter(new Func1<Attitude, Boolean>() {
            @Override
            public Boolean call(Attitude attitude) {
                return attitude.getStatus().getId() == mWeiboId && !mAttitudes.contains(attitude);
            }
        }).subscribe(new Action1<Attitude>() {
            @Override
            public void call(Attitude attitude) {
                mAttitudes.add(0, attitude);
                mView.onAttitudeSuccess(mAttitudes);
            }
        });

        mWeiboRefreshObservable = RxBus.getDefault().register(Event.EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE);
        mWeiboRefreshObservable.filter(new Func1<List<Attitude>, Boolean>() {
            @Override
            public Boolean call(List<Attitude> attitudes) {
                return attitudes != null && attitudes.size() > 0 && attitudes.get(0).getStatus().getId() == mWeiboId;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Attitude>>() {
                    @Override
                    public void call(List<Attitude> attitudes) {
                        LogUtil.d(WeiboAttitudesPresentImp.this, "accept refresh event");

                        addRefreshDate(attitudes);

                    }
                });
    }

    @Override
    public void userFirstVisible() {
        initEventListener();

        Subscription subscription = createObservable(1, true)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        if (mAttitudes.size() == 0) {
                            mView.showErrorView();
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        addRefreshDate(attitudes);
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    private void addRefreshDate(List<Attitude> attitudes) {
        mAttitudes.clear();
        mAttitudes.addAll(attitudes);
        mView.setEntities(mAttitudes);

        mView.onLoadComplete(attitudes.size() >= PAGE_COUNET - 3);

        mPage = 2;
    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage, false)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.notifyItemRangeInserted(mAttitudes, mAttitudes.size() - attitudes.size(), attitudes.size());
                        mView.onLoadComplete(attitudes.size() >= 15);
                        mPage ++;
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    private  Observable<List<Attitude>> createObservable(int page, final boolean isRefresh) {
        return mServerWeiboSource.getWeiboAttiyudes(mToken, mWeiboId, page, PAGE_COUNET)
                .compose(new ErrorCheckerTransformer<QueryWeiboAttitudeResponse>())
                .flatMap(new Func1<QueryWeiboAttitudeResponse, Observable<Attitude>>() {
                    @Override
                    public Observable<Attitude> call(QueryWeiboAttitudeResponse queryWeiboAttitudeResponse) {
                        updateWeiboAttitudeCount(queryWeiboAttitudeResponse);
                        return Observable.from(queryWeiboAttitudeResponse.getAttitudes());
                    }
                })
                .filter(new Func1<Attitude, Boolean>() {
                    @Override
                    public Boolean call(Attitude attitude) {
                        return !mAttitudes.contains(attitude) || isRefresh;
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<Attitude>>());
    }

    private void updateWeiboAttitudeCount(final QueryWeiboAttitudeResponse queryWeiboAttitudeResponse) {
        Subscription subscription = mLocalWeiboSource.getWeiboById(mToken, mWeiboId)
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return weibo != null;
                    }
                }).doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        weibo.setAttitudes_count(queryWeiboAttitudeResponse.getTotal_number());
                        weibo.setUpdate_time(System.currentTimeMillis());
                        mLocalWeiboSource.saveWeibo(mToken, weibo);
                    }
                }).subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weibo weibo) {
                        RxBus.getDefault().post(Event.EVENT_WEIBO_UPDATE, weibo);
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
        RxBus.getDefault().unregister(Event.EVENT_ATTITUDE_WEIBO_SUCCESS, mAttitudeObservable);
        RxBus.getDefault().unregister(Event.EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE, mWeiboRefreshObservable);
    }
}

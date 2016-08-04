package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.present.view.WeiboAttitudesView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboAttitudesPresentImp implements WeiboRepostsPresent {

    private static final int PAGE_COUNET = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    WeiboSource mServerRepostSource;
    WeiboAttitudesView mView;
    List<Attitude> mAttitudes;
    private int mPage;
    private Observable<Attitude> mAttitudeObservable;
    Observable<List<Attitude>> mWeiboRefreshObservable;

    public WeiboAttitudesPresentImp(String token, long weiboId, WeiboSource repostSource, WeiboAttitudesView view) {
        mToken = token;
        mServerRepostSource = repostSource;
        mView = view;
        mWeiboId = weiboId;
        mLoginCompositeSubscription = new CompositeSubscription();
        mAttitudes = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mAttitudeObservable = RxBus.get().register(Key.EVENT_ATTITUDE_WEIBO_SUCCESS);
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

        mWeiboRefreshObservable = RxBus.get().register(Key.EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE);
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

                        mAttitudes.clear();
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(mAttitudes);
                        if (attitudes.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplete(attitudes.size() >= PAGE_COUNET - 3);
                        }
                        mPage = 2;
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
                        mView.onDefaultLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.clear();
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(mAttitudes);
                        if (attitudes.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplete(attitudes.size() >= PAGE_COUNET);
                        }
                        mPage = 2;
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage, false)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.onDefaultLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(mAttitudes);
                        mView.onLoadComplete(attitudes.size() >= 15);
                        mPage ++;
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    private  Observable<List<Attitude>> createObservable(int page, final boolean isRefresh) {
        return mServerRepostSource.getWeiboAttiyudes(mToken, mWeiboId, page, PAGE_COUNET)
                .compose(new ErrorCheckerTransformer<QueryWeiboAttitudeResponse>())
                .flatMap(new Func1<QueryWeiboAttitudeResponse, Observable<Attitude>>() {
                    @Override
                    public Observable<Attitude> call(QueryWeiboAttitudeResponse queryWeiboAttitudeResponse) {
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

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
        RxBus.get().unregister(Key.EVENT_ATTITUDE_WEIBO_SUCCESS, mAttitudeObservable);
        RxBus.get().unregister(Key.EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE, mWeiboRefreshObservable);
    }
}

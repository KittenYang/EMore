package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.response.AttitudeResponse;
import com.caij.emore.bean.response.WeiboAttitudeResponse;
import com.caij.emore.database.bean.User;
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

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboAttitudesPresentImp extends AbsBasePresent implements WeiboRepostsPresent {

    private static final int PAGE_COUNET = 20;

    private String mToken;
    private long mWeiboId;
    WeiboSource mServerWeiboSource;
    WeiboSource mLocalWeiboSource;
    WeiboAttitudesView mView;
    List<User> mAttitudes;
    private int mPage;
    private Observable<User> mAttitudeObservable;
    Observable<List<User>> mWeiboRefreshObservable;

    public WeiboAttitudesPresentImp(String token, long weiboId,
                                    WeiboSource serverWeiboSource,
                                    WeiboSource localWeiboSource,
                                    WeiboAttitudesView view) {
        mToken = token;
        mServerWeiboSource = serverWeiboSource;
        mLocalWeiboSource = localWeiboSource;
        mView = view;
        mWeiboId = weiboId;
        mAttitudes = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mAttitudeObservable = RxBus.getDefault().register(Event.EVENT_ATTITUDE_WEIBO_SUCCESS);
        mAttitudeObservable.subscribe(new Action1<User>() {
            @Override
            public void call(User user) {
                mAttitudes.add(0, user);
                mView.setEntities(mAttitudes);
            }
        });

        mWeiboRefreshObservable = RxBus.getDefault().register(Event.EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE);
        mWeiboRefreshObservable.filter(new Func1<List<User>, Boolean>() {
            @Override
            public Boolean call(List<User> attitudes) {
                return attitudes != null && attitudes.size() > 0 && attitudes.get(0).getStatus().getId() == mWeiboId;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        LogUtil.d(WeiboAttitudesPresentImp.this, "accept refresh event");

                        addRefreshDate(users);

                    }
                });
    }

    @Override
    public void userFirstVisible() {
        initEventListener();

        Subscription subscription = createObservable(1, true)
                .subscribe(new DefaultResponseSubscriber<List<User>>(mView) {
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
                    public void onNext(List<User> users) {
                        addRefreshDate(users);
                    }
                });
        addSubscription(subscription);
    }

    private void addRefreshDate(List<User> users) {
        mAttitudes.clear();
        mAttitudes.addAll(users);
        mView.setEntities(mAttitudes);

        mView.onLoadComplete(users.size() >= PAGE_COUNET - 3);

        mPage = 2;
    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage, false)
                .subscribe(new DefaultResponseSubscriber<List<User>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        mAttitudes.addAll(users);
                        mView.notifyItemRangeInserted(mAttitudes, mAttitudes.size() - users.size(), users.size());
                        mView.onLoadComplete(users.size() >= 15);
                        mPage ++;
                    }
                });

        addSubscription(subscription);
    }

    private  Observable<List<User>> createObservable(int page, final boolean isRefresh) {
        return mServerWeiboSource.getWeiboAttiyudes(mToken, mWeiboId, page, PAGE_COUNET)
                .compose(new ErrorCheckerTransformer<WeiboAttitudeResponse>())
                .flatMap(new Func1<WeiboAttitudeResponse, Observable<User>>() {
                    @Override
                    public Observable<User> call(WeiboAttitudeResponse queryWeiboAttitudeResponse) {
                        updateWeiboAttitudeCount(queryWeiboAttitudeResponse);
                        return Observable.from(queryWeiboAttitudeResponse.getUsers());
                    }
                })
                .filter(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return !mAttitudes.contains(user) || isRefresh;
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<User>>());
    }

    private void updateWeiboAttitudeCount(final WeiboAttitudeResponse queryWeiboAttitudeResponse) {
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
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(Event.EVENT_ATTITUDE_WEIBO_SUCCESS, mAttitudeObservable);
        RxBus.getDefault().unregister(Event.EVENT_WEIBO_ATTITUDE_REFRESH_COMPLETE, mWeiboRefreshObservable);
    }
}

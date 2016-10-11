package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.bean.response.WeiboAttitudeResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.ui.view.WeiboAttitudesView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
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

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboAttitudesPresentImp extends AbsBasePresent implements WeiboRepostsPresent, Action1<Event> {

    private static final int PAGE_COUNET = 20;

    private String mToken;
    private long mWeiboId;
    WeiboAttitudesView mView;
    List<User> mAttitudes;
    private int mPage;
    private Observable<Event> mAttitudeObservable;
    Observable<Event> mWeiboRefreshObservable;

    private AttitudeApi mAttitudeApi;

    public WeiboAttitudesPresentImp(String token, long weiboId,
                                    AttitudeApi attitudeApi,
                                    WeiboAttitudesView view) {
        mToken = token;
        mAttitudeApi = attitudeApi;
        mView = view;
        mWeiboId = weiboId;
        mAttitudes = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mAttitudeObservable = RxBus.getDefault().register(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS);
        mAttitudeObservable.subscribe(this);

        mWeiboRefreshObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_REFRESH);
        mWeiboRefreshObservable.subscribe(this);
    }


    @Override
    public void call(Event event) {
        if (EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS.equals(event.type)) {
            StatusAttitudeEvent statusAttitudeEvent = (StatusAttitudeEvent) event;
            if (statusAttitudeEvent.statusId == mWeiboId) {
                if (statusAttitudeEvent.isAttitude) {
                    mAttitudes.add(0, statusAttitudeEvent.user);
                    mView.setEntities(mAttitudes);
                } else {
                    mAttitudes.remove(statusAttitudeEvent.user);
                    mView.setEntities(mAttitudes);
                }
            }
        }else if (EventTag.EVENT_STATUS_REFRESH.equals(event.type)) {
            StatusRefreshEvent statusRefreshEvent = (StatusRefreshEvent) event;
            if (mWeiboId == statusRefreshEvent.statusId) {
                refresh();
            }
        }
    }

    private void refresh() {
        Subscription subscription = createObservable(1, true)
                .subscribe(new DefaultResponseSubscriber<List<User>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        addRefreshDate(users);
                    }
                });
        addSubscription(subscription);
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
        return mAttitudeApi.getWeiboAttiyudes(mWeiboId, page, PAGE_COUNET)
                .compose(new ErrorCheckerTransformer<WeiboAttitudeResponse>())
                .flatMap(new Func1<WeiboAttitudeResponse, Observable<User>>() {
                    @Override
                    public Observable<User> call(WeiboAttitudeResponse queryWeiboAttitudeResponse) {
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


    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS, mAttitudeObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_REFRESH, mWeiboRefreshObservable);
    }

}

package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.StatusActionCountUpdateEvent;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.bean.response.StatusAttitudesResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.StatusRelayPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.ui.view.StatusAttitudesView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/6/28.
 */
public class StatusAttitudesPresentImp extends AbsBasePresent implements StatusRelayPresent, Action1<Event> {

    private static final int PAGE_COUNT = 20;

    private long mStatusId;
    private StatusAttitudesView mView;
    private List<User> mAttitudes;
    private int mPage;
    private Observable<Event> mAttitudeObservable;
    private Observable<Event> mStatusRefreshObservable;

    private AttitudeApi mAttitudeApi;

    public StatusAttitudesPresentImp(long statusId,
                                     AttitudeApi attitudeApi,
                                     StatusAttitudesView view) {
        mAttitudeApi = attitudeApi;
        mView = view;
        mStatusId = statusId;
        mAttitudes = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mAttitudeObservable = RxBus.getDefault().register(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS);
        mAttitudeObservable.subscribe(this);

        mStatusRefreshObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_REFRESH);
        mStatusRefreshObservable.subscribe(this);
    }


    @Override
    public void call(Event event) {
        if (EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS.equals(event.type)) {
            StatusAttitudeEvent statusAttitudeEvent = (StatusAttitudeEvent) event;
            if (statusAttitudeEvent.statusId == mStatusId) {
                if (statusAttitudeEvent.isAttitude) {
                    mAttitudes.add(0, statusAttitudeEvent.user);
                    mView.notifyItemRangeInserted(mAttitudes, mAttitudes.size() - 1 - 1, 1);
                } else {
                    for (int i = 0; i < mAttitudes.size(); i ++) {
                        User user = mAttitudes.get(i);
                        if (user.getId().equals(((StatusAttitudeEvent) event).user.getId())) {
                            mAttitudes.remove(i);
                            mView.notifyItemRemoved(mAttitudes, i);
                            break;
                        }
                    }
                }
            }
        }else if (EventTag.EVENT_STATUS_REFRESH.equals(event.type)) {
            StatusRefreshEvent statusRefreshEvent = (StatusRefreshEvent) event;
            if (mStatusId == statusRefreshEvent.statusId) {
                refresh();
            }
        }
    }

    private void refresh() {
        Subscription subscription = createObservable(1)
                .subscribe(new ResponseSubscriber<List<User>>(mView) {
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

    @Override
    public void userFirstVisible() {
        initEventListener();

        refresh();
    }

    private void addRefreshDate(List<User> users) {
        mAttitudes.clear();
        mAttitudes.addAll(users);
        mView.setEntities(mAttitudes);

        mView.onLoadComplete(users.size() >= PAGE_COUNT - 3);

        mPage = 2;
    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage)
                .subscribe(new ResponseSubscriber<List<User>>(mView) {
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

    private  Observable<List<User>> createObservable(int page) {
        return mAttitudeApi.getStatusAttitudes(mStatusId, page, PAGE_COUNT)
                .compose(new ErrorCheckerTransformer<StatusAttitudesResponse>())
                .flatMap(new Func1<StatusAttitudesResponse, Observable<List<User>>>() {
                    @Override
                    public Observable<List<User>> call(StatusAttitudesResponse queryStatusAttitudesResponse) {
                        StatusActionCountUpdateEvent event = new StatusActionCountUpdateEvent(EventTag.EVENT_STATUS_ATTITUDE_COUNT_UPDATE,
                                mStatusId, queryStatusAttitudesResponse.getTotal_number());
                        RxBus.getDefault().post(event.type, event);
                        return Observable.just(queryStatusAttitudesResponse.getUsers());
                    }
                })
                .compose(new SchedulerTransformer<List<User>>());
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_ATTITUDE_WEIBO_SUCCESS, mAttitudeObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_REFRESH, mStatusRefreshObservable);
    }

}

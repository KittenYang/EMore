package com.caij.emore.present.imp;

import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.UserStatusPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.TimeLineStatusView;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class UserStatusPresentImp extends AbsListTimeLinePresent<TimeLineStatusView> implements UserStatusPresent {

    private final static int PAGE_COUNT = 20;

    private int mFeature = 0;
    private long mUid;

    public UserStatusPresentImp(long uid, TimeLineStatusView view, StatusApi statusApi,
                                StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mUid = uid;
    }

    @Override
    public void filter(int feature) {
        if (mFeature != feature) {
            mFeature = feature;
            mStatuses.clear();
            mView.setEntities(mStatuses);
            refresh();
        }
    }

    @Override
    public void refresh() {
        Subscription subscription = createObservable(0, true)
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        if (mStatuses.size() == 0) {
                            mView.showErrorView();
                        }
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.clear();
                        mStatuses.addAll(statuses);
                        mView.setEntities(mStatuses);

                        mView.onLoadComplete(statuses.size() >= PAGE_COUNT);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mStatuses.size() > 0) {
            maxId = mStatuses.get(mStatuses.size() - 1).getId();
        }
        Subscription subscription = createObservable(maxId, false)
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.addAll(statuses);
                        mView.notifyItemRangeInserted(mStatuses, mStatuses.size() - statuses.size(), statuses.size());
                        mView.onLoadComplete(statuses.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        addSubscription(subscription);
    }

    private Observable<List<Status>> createObservable(long maxId, final boolean isRefresh) {
        return mStatusApi.getUseWeibo(mUid, mFeature, 0, maxId, PAGE_COUNT, 1)
                .compose(new ErrorCheckerTransformer<UserWeiboResponse>())
                .flatMap(new Func1<UserWeiboResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Status, Boolean>() {
                    @Override
                    public Boolean call(Status status) {
                        return isRefresh || !mStatuses.contains(status);
                    }
                })
                .compose(StatusContentSpannableConvertTransformer.create(false))
                .toList()
                .compose(new SchedulerTransformer<List<Status>>());
    }
}

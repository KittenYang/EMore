package com.caij.emore.present.imp;

import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.bean.response.UserStatusesResponse;
import com.caij.emore.database.bean.Status;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.present.GroupStatusPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.TimeLineStatusView;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/11/2.
 */

public class GroupStatusPresentImp extends AbsListTimeLinePresent<TimeLineStatusView>  implements GroupStatusPresent {

    private final static int PAGE_COUNT = 20;

    private long mGroupId;

    private long mNextCursor;

    public GroupStatusPresentImp(long groupId, TimeLineStatusView view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mGroupId = groupId;
    }

    @Override
    public void refresh() {
        Subscription subscription = createObservable(mGroupId, 0, true)
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

                        mView.onLoadComplete(mNextCursor > 0);

                        mView.setEntities(mStatuses);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mGroupId, mNextCursor, false)
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.addAll(statuses);

                        mView.notifyItemRangeInserted(mStatuses, mStatuses.size() - statuses.size(), statuses.size());
                        mView.onLoadComplete(mNextCursor > 0);
                    }
                });
        addSubscription(subscription);
    }

    private Observable<List<Status>> createObservable(long groupId, long maxId, final boolean isRefresh) {
        return mStatusApi.getGroupStatus(groupId, 0, maxId, PAGE_COUNT, 1)
                .compose(new ErrorCheckerTransformer<QueryStatusResponse>())
                .flatMap(new Func1<QueryStatusResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(QueryStatusResponse response) {
                        mNextCursor = response.getNext_cursor();
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

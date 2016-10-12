package com.caij.emore.present.imp;

import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.WeiboMentionPresent;
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
 * Created by Caij on 2016/7/4.
 */
public class TopicPresentImp extends AbsListTimeLinePresent<TimeLineStatusView> implements WeiboMentionPresent {

    private static final int COUNT = 20;

    private String mTopic;
    private int page;

    public TopicPresentImp(String topic, TimeLineStatusView view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
        mTopic = topic;
    }

    @Override
    public void userFirstVisible() {
    }

    @Override
    public void refresh() {
        Subscription su = createObservable(1, true)
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.addAll(statuses);
                        mView.setEntities(mStatuses);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(statuses.size() >= COUNT );

                        page = 2;
                    }
                });
        addSubscription(su);
    }

    @Override
    public void loadMore() {
        Subscription su = createObservable(page, false)
                .subscribe(new ResponseSubscriber<List<Status>>(mView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatuses.addAll(statuses);
                        mView.notifyItemRangeInserted(mStatuses, mStatuses.size() - statuses.size(), statuses.size());

                        mView.onLoadComplete(statuses.size() > COUNT - 1);

                        page++;
                    }
                });
        addSubscription(su);
    }

    private Observable<List<Status>> createObservable(int page, final boolean isRefresh) {
        return mStatusApi.getTopicsByKey(mTopic, page, COUNT)
                .compose(ErrorCheckerTransformer.<QueryStatusResponse>create())
                .flatMap(new Func1<QueryStatusResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(QueryStatusResponse queryWeiboResponse) {
                        return Observable.from(queryWeiboResponse.getStatuses());
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

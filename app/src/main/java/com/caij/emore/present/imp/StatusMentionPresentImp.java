package com.caij.emore.present.imp;

import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.manager.StatusManager;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.WeiboMentionPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.remote.NotifyApi;
import com.caij.emore.ui.view.TimeLineStatusView;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class StatusMentionPresentImp extends AbsListTimeLinePresent<TimeLineStatusView> implements WeiboMentionPresent {

    private static final int COUNT = 20;

    private long mUid;

    private NotifyApi mNotifyApi;
    private NotifyManager mNotifyManager;

    public StatusMentionPresentImp(long uid, TimeLineStatusView view, StatusApi statusApi,
                                   StatusManager statusManager, AttitudeApi attitudeApi,
                                   NotifyApi notifyApi, NotifyManager notifyManager) {
        super(view, statusApi, statusManager, attitudeApi);
        mUid = uid;
        mNotifyApi = notifyApi;
        mNotifyManager = notifyManager;
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void refresh() {
        Subscription su = createObservable(0, true)
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
                        mView.onLoadComplete(statuses.size() > COUNT - 2);

                        MessageUtil.resetUnReadMessage(UnReadMessage.TYPE_MENTION_STATUS, mUid,
                                mNotifyApi, mNotifyManager);
                    }
                });
        addSubscription(su);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mStatuses != null && mStatuses.size() > 1) {
            maxId = mStatuses.get(mStatuses.size() - 1).getId();
        }
        Subscription su = createObservable(maxId, false)
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
                    }
                });
        addSubscription(su);
    }

    private Observable<List<Status>> createObservable(long maxId, final boolean isRefresh) {
        return mStatusApi.getWeiboMentions(0, maxId, COUNT, 1)
                .compose(new ErrorCheckerTransformer<QueryStatusResponse>())
                .flatMap(new Func1<QueryStatusResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(QueryStatusResponse queryWeiboResponse) {
                        return Observable.from(queryWeiboResponse.getStatuses());
                    }
                })
                .filter(new Func1<Status, Boolean>() {
                    @Override
                    public Boolean call(Status weibo) {
                        return isRefresh || !mStatuses.contains(weibo);
                    }
                })
                .compose(StatusContentSpannableConvertTransformer.create(false))
                .toList()
                .compose(SchedulerTransformer.<List<Status>>create());
    }

}

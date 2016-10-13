package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.RelayStatusEvent;
import com.caij.emore.bean.event.StatusActionCountUpdateEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.bean.response.QueryRelayStatusResponse;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.StatusRelayPresent;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.StatusRelayView;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.utils.SpannableStringUtil;
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
public class StatusRelayPresentImp extends AbsBasePresent implements StatusRelayPresent, Action1<Event> {

    private static final int PAGE_COUNT = 20;

    private long mStatusId;
    private StatusRelayView mStatusRelayView;
    private List<Status> mStatus;
    private Observable<Event> mStatusObservable;
    private Observable<Event> mStatusRefreshObservable;

    private long mNextCursor;

    private StatusApi mStatusApi;

    public StatusRelayPresentImp(long statudId, StatusApi statusApi,
                                 StatusRelayView statusRelayView) {
        mStatusApi = statusApi;
        mStatusRelayView = statusRelayView;
        mStatusId = statudId;
        mStatus = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
        initEventListener();

        refresh();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mStatusObservable = RxBus.getDefault().register(EventTag.EVENT_REPOST_WEIBO_SUCCESS);
        mStatusObservable.subscribe(this);

        mStatusRefreshObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_REFRESH);
        mStatusRefreshObservable.subscribe(this);
    }

    @Override
    public void call(Event event) {
        if (EventTag.EVENT_REPOST_WEIBO_SUCCESS.equals(event.type)) {
            RelayStatusEvent relayStatusEvent = (RelayStatusEvent) event;
            if (mStatusId == relayStatusEvent.weiboId) {
                SpannableStringUtil.paraeSpannable(relayStatusEvent.weibo);
                mStatus.add(0, relayStatusEvent.weibo);
                mStatusRelayView.onRelayStatusSuccess(mStatus);
            }
        }else if (EventTag.EVENT_STATUS_REFRESH.equals(event.type)) {
            StatusRefreshEvent statusRefreshEvent = (StatusRefreshEvent) event;
            if (mStatusId == statusRefreshEvent.statusId) {
                refresh();
            }
        }
    }

    private void refresh() {
        Subscription subscription = createObservable(0)
                .subscribe(new ResponseSubscriber<List<Status>>(mStatusRelayView) {
                    @Override
                    protected void onFail(Throwable e) {
                        if (mStatus.size() == 0) {
                            mStatusRelayView.showErrorView();
                        }
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        addRefreshDate(statuses);
                    }
                });

        addSubscription(subscription);
    }

    private void addRefreshDate(List<Status> statuses) {
        mStatus.clear();
        mStatus.addAll(statuses);
        mStatusRelayView.setEntities(statuses);

        mStatusRelayView.onLoadComplete(mNextCursor != 0);
    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mNextCursor)
                .subscribe(new ResponseSubscriber<List<Status>>(mStatusRelayView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mStatusRelayView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mStatus.addAll(statuses);
                        mStatusRelayView.notifyItemRangeInserted(mStatus, mStatus.size() - statuses.size(), statuses.size());
                        mStatusRelayView.onLoadComplete(mNextCursor != 0);
                    }
                });

        addSubscription(subscription);
    }

    private  Observable<List<Status>> createObservable(long maxId) {
        return mStatusApi.getRepostWeibos(mStatusId, 0, maxId, PAGE_COUNT, 1)
                .compose(new ErrorCheckerTransformer<QueryRelayStatusResponse>())
                .flatMap(new Func1<QueryRelayStatusResponse, Observable<Status>>() {
                    @Override
                    public Observable<Status> call(QueryRelayStatusResponse queryRelayStatusResponse) {
                        mNextCursor = queryRelayStatusResponse.getNext_cursor();
                        StatusActionCountUpdateEvent event = new StatusActionCountUpdateEvent(EventTag.EVENT_STATUS_RELAY_COUNT_UPDATE,
                                mStatusId, queryRelayStatusResponse.getTotal_number());
                        RxBus.getDefault().post(event.type, event);
                        return Observable.from(queryRelayStatusResponse.getReposts());
                    }
                })
                .compose(AbsTimeLinePresent.StatusContentSpannableConvertTransformer.create(false))
                .toList()
                .compose(SchedulerTransformer.<List<Status>>create());
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_REPOST_WEIBO_SUCCESS, mStatusObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_REFRESH, mStatusRefreshObservable);
    }

}

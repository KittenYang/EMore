package com.caij.emore.present.imp;

import com.caij.emore.manager.StatusManager;
import com.caij.emore.present.FriendStatusPresent;
import com.caij.emore.present.HotStatusPresent;
import com.caij.emore.remote.AttitudeApi;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.TimeLineStatusView;

/**
 * Created by Caij on 2016/5/31.
 */
public class HotStatusPresentImp extends AbsListTimeLinePresent<TimeLineStatusView> implements FriendStatusPresent, HotStatusPresent {

    private final static int PAGE_COUNT = 20;

    private int page;

    public HotStatusPresentImp(TimeLineStatusView view, StatusApi statusApi, StatusManager statusManager, AttitudeApi attitudeApi) {
        super(view, statusApi, statusManager, attitudeApi);
    }

//    public HotStatusPresentImp(Account account, TimeLineStatusView view, WeiboSource serverWeiboSource,
//                              WeiboSource localWeiboSource) {
//        super(account, view, serverWeiboSource, localWeiboSource);
//    }

    @Override
    public void refresh() {
//        Subscription subscription = createObservable(1, true)
//                .subscribe(new ResponseSubscriber<List<Status>>(mView) {
//                    @Override
//                    public void onCompleted() {
//                        mView.onRefreshComplete();
//                    }
//
//                    @Override
//                    protected void onFail(Throwable e) {
//                        mView.onRefreshComplete();
//                    }
//
//                    @Override
//                    public void onNext(List<Status> statuses) {
//                        mStatuses.clear();
//                        mStatuses.addAll(statuses);
//                        mView.setEntities(mStatuses);
//
//                        mView.onLoadComplete(statuses.size() >= PAGE_COUNT - 3);
//
//                        page = 2;
//                    }
//                });
//        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
//        Subscription subscription = createObservable(page, false)
//            .subscribe(new ResponseSubscriber<List<Status>>(mView) {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        protected void onFail(Throwable e) {
//                            mView.onLoadComplete(true);
//                        }
//
//                        @Override
//                        public void onNext(List<Status> statuses) {
//                            page ++;
//                            mStatuses.addAll(statuses);
//                            mView.notifyItemRangeInserted(mStatuses, mStatuses.size() - statuses.size(), statuses.size());
//                            mView.onLoadComplete(statuses.size() >= PAGE_COUNT - 2); //这里有一条重复的 所以需要-1
//                        }
//                    });
//        addSubscription(subscription);
    }
//
//    private Observable<List<Status>> createObservable(int page, final boolean isRefresh) {
//        return mStatusApi.getHotWeibosIds(token, page)
//                .flatMap(new Func1<WeiboIds, Observable<QueryStatusResponse>>() {
//                    @Override
//                    public Observable<QueryStatusResponse> call(WeiboIds weiboIds) {
//                        StringBuilder sb = new StringBuilder();
//                        for (long id : weiboIds.getIds()) {
//                            sb.append(id).append(",");
//                        }
//                        return mServerWeiboSource.getStatusByIds(token, sb.toString());
//                    }
//                })
//                .compose(new ErrorCheckerTransformer<QueryStatusResponse>())
//                .flatMap(new Func1<QueryStatusResponse, Observable<Status>>() {
//                    @Override
//                    public Observable<Status> call(QueryStatusResponse response) {
//                        return Observable.from(response.getStatuses());
//                    }
//                })
//                .filter(new Func1<Status, Boolean>() {
//                    @Override
//                    public Boolean call(Status weibo) {
//                        return isRefresh || !mStatuses.contains(weibo);
//                    }
//                })
//                .toList()
//                .doOnNext(new Action1<List<Status>>() {
//                    @Override
//                    public void call(List<Status> statuses) {
//                        mLocalWeiboSource.saveStatuses(mAccount.getToken().getAccess_token(), statuses);
//                    }
//                })
//                .compose(new SchedulerTransformer<List<Status>>());
//    }

}

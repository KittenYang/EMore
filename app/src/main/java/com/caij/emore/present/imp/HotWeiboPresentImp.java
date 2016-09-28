package com.caij.emore.present.imp;

import com.caij.emore.account.Account;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.present.HotWeiboPresent;
import com.caij.emore.ui.view.TimeLineWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class HotWeiboPresentImp extends AbsListTimeLinePresent<TimeLineWeiboView> implements FriendWeiboPresent, HotWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private int page;

    public HotWeiboPresentImp(Account account, TimeLineWeiboView view, WeiboSource serverWeiboSource,
                              WeiboSource localWeiboSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
    }

    @Override
    public void refresh() {
        Subscription subscription = createObservable(1, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplete();
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 3);

                        page = 2;
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(page, false)
            .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        protected void onFail(Throwable e) {
                            mView.onLoadComplete(true);
                        }

                        @Override
                        public void onNext(List<Weibo> weibos) {
                            page ++;
                            mWeibos.addAll(weibos);
                            mView.notifyItemRangeInserted(mWeibos, mWeibos.size() - weibos.size(), weibos.size());
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 2); //这里有一条重复的 所以需要-1
                        }
                    });
        addSubscription(subscription);
    }

    private Observable<List<Weibo>> createObservable(int page, final boolean isRefresh) {
        final String token = mAccount.getToken().getAccess_token();
        return mServerWeiboSource.getHotWeibosIds(token, page)
                .flatMap(new Func1<WeiboIds, Observable<QueryWeiboResponse>>() {
                    @Override
                    public Observable<QueryWeiboResponse> call(WeiboIds weiboIds) {
                        StringBuilder sb = new StringBuilder();
                        for (long id : weiboIds.getIds()) {
                            sb.append(id).append(",");
                        }
                        return mServerWeiboSource.getWeibosByIds(token, sb.toString());
                    }
                })
                .compose(new ErrorCheckerTransformer<QueryWeiboResponse>())
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return isRefresh || !mWeibos.contains(weibo);
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        mLocalWeiboSource.saveWeibos(mAccount.getToken().getAccess_token(), weibos);
                    }
                })
                .compose(new SchedulerTransformer<List<Weibo>>());
    }

}

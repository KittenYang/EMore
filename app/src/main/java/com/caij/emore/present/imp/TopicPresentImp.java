package com.caij.emore.present.imp;

import com.caij.emore.bean.Account;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboMentionPresent;
import com.caij.emore.ui.view.TimeLineWeiboView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class TopicPresentImp extends AbsListTimeLinePresent<TimeLineWeiboView> implements WeiboMentionPresent {

    private static final int COUNT = 20;

    private String mTopic;
    private int page;

    public TopicPresentImp(Account account, String topic, WeiboSource serverWeiboSource,
                           WeiboSource localWeiboSource,
                           TimeLineWeiboView timeLineWeiboView) {
        super(account, timeLineWeiboView, serverWeiboSource, localWeiboSource);
        mTopic = topic;
    }

    @Override
    public void userFirstVisible() {
    }

    @Override
    public void refresh() {
        Subscription su = createObservable(1, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);

                        mView.onRefreshComplete();
                        mView.onLoadComplete(weibos.size() > COUNT - 1);

                        page = 2;
                    }
                });
        mCompositeSubscription.add(su);
    }

    @Override
    public void loadMore() {
        Subscription su = createObservable(page, false)
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
                        mWeibos.addAll(weibos);
                        mView.notifyItemRangeInserted(mWeibos, mWeibos.size() - weibos.size(), weibos.size());

                        mView.onLoadComplete(weibos.size() > COUNT - 1);

                        page++;
                    }
                });
        mCompositeSubscription.add(su);
    }

    private Observable<List<Weibo>> createObservable(int page, final boolean isRefresh) {
        return mServerWeiboSource.getTopicsByKey(mAccount.getWeicoToken().getAccess_token(), mTopic, page, COUNT)
                .compose(new ErrorCheckerTransformer<QueryWeiboResponse>())
                .flatMap(new Func1<QueryWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.from(queryWeiboResponse.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return isRefresh || !mWeibos.contains(weibo);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        weibo.setAttitudes(mLocalWeiboSource.getAttitudes(weibo.getId()));
                        toGetImageSize(weibo);
                        return weibo;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        doSpanNext(weibos);
                    }
                })
                .compose(new SchedulerTransformer<List<Weibo>>());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.clear();
    }


}

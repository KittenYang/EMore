package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.bean.event.CommentEvent;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.RepostStatusEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.ui.view.WeiboRepostsView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboRepostsPresentImp extends AbsBasePresent implements WeiboRepostsPresent, Action1<Event> {

    private static final int PAGE_COUNET = 20;

    private long mWeiboId;
    private WeiboRepostsView mWeiboRepostsView;
    private List<Weibo> mWeobos;
    private Observable<Event> mWeiboObservable;
    private Observable<Event> mWeiboRefreshObservable;

    private StatusApi mStatusApi;

    public WeiboRepostsPresentImp(long weiboId, StatusApi statusApi,
                                  WeiboRepostsView repostsView) {
        mStatusApi = statusApi;
        mWeiboRepostsView = repostsView;
        mWeiboId = weiboId;
        mWeobos = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
        initEventListener();

        Subscription subscription = createObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mWeiboRepostsView) {
                    @Override
                    protected void onFail(Throwable e) {
                        if (mWeobos.size() == 0) {
                            mWeiboRepostsView.showErrorView();
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        addRefreshDate(weibos);
                    }
                });

        addSubscription(subscription);
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mWeiboObservable = RxBus.getDefault().register(EventTag.EVENT_REPOST_WEIBO_SUCCESS);
        mWeiboObservable.subscribe(this);

        mWeiboRefreshObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_REFRESH);
        mWeiboRefreshObservable.subscribe(this);
    }


    @Override
    public void call(Event event) {
        if (EventTag.EVENT_REPOST_WEIBO_SUCCESS.equals(event.type)) {
            RepostStatusEvent repostStatusEvent = (RepostStatusEvent) event;
            if (mWeiboId == repostStatusEvent.weiboId) {
                doSpanNext(repostStatusEvent.weibo);
                mWeobos.add(0, repostStatusEvent.weibo);
                mWeiboRepostsView.onRepostWeiboSuccess(mWeobos);
            }
        }else if (EventTag.EVENT_STATUS_REFRESH.equals(event.type)) {
            StatusRefreshEvent statusRefreshEvent = (StatusRefreshEvent) event;
            if (mWeiboId == statusRefreshEvent.statusId) {
                refresh();
            }
        }
    }

    private void refresh() {
        Subscription subscription = createObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mWeiboRepostsView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        addRefreshDate(weibos);
                    }
                });

        addSubscription(subscription);
    }

    private void addRefreshDate(List<Weibo> weibos) {
        mWeobos.clear();
        mWeobos.addAll(weibos);
        mWeiboRepostsView.setEntities(weibos);

        mWeiboRepostsView.onLoadComplete(weibos.size() >= PAGE_COUNET - 5);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mWeobos.size() > 0) {
            maxId = mWeobos.get(mWeobos.size() - 1).getId();
        }
        Subscription subscription = createObservable(maxId, false)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mWeiboRepostsView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mWeiboRepostsView.onLoadComplete(true);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeobos.addAll(weibos);
                        mWeiboRepostsView.notifyItemRangeInserted(mWeobos, mWeobos.size() - weibos.size(), weibos.size());
                        mWeiboRepostsView.onLoadComplete(weibos.size() >= 15);
                    }
                });

        addSubscription(subscription);
    }

    private  Observable<List<Weibo>> createObservable(long maxId, final boolean isRefresh) {
        return mStatusApi.getRepostWeibos(mWeiboId, 0, maxId, PAGE_COUNET, 1)
                .compose(new ErrorCheckerTransformer<QueryRepostWeiboResponse>())
                .flatMap(new Func1<QueryRepostWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryRepostWeiboResponse queryRepostWeiboResponse) {
                        return Observable.from(queryRepostWeiboResponse.getReposts());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return isRefresh || !mWeobos.contains(weibo);
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
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_REPOST_WEIBO_SUCCESS, mWeiboObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_REFRESH, mWeiboRefreshObservable);
    }

    protected void doSpanNext(List<Weibo> weibos) {
        for (Weibo weibo : weibos) {
            SpannableStringUtil.paraeSpannable(weibo);
        }
    }

    protected void doSpanNext(Weibo weibo) {
        SpannableStringUtil.paraeSpannable(weibo);
    }

}

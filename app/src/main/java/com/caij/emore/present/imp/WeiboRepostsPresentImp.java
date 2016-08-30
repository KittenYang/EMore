package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.source.UrlSource;
import com.caij.emore.source.local.LocalUrlSource;
import com.caij.emore.source.server.ServerUrlSource;
import com.caij.emore.ui.view.WeiboRepostsView;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboRepostsPresentImp extends AbsBasePresent implements WeiboRepostsPresent {

    private static final int PAGE_COUNET = 20;

    private String mToken;
    private long mWeiboId;
    WeiboSource mServerRepostSource;
    WeiboSource mLocalWeiboSource;
    WeiboRepostsView mWeiboRepostsView;
    List<Weibo> mWeobos;
    Observable<Weibo> mWeiboObservable;
    private UrlSource mServerUrlSource;
    private UrlSource mLocalUrlSource;
    Observable<List<Weibo>> mWeiboRefreshObservable;

    public WeiboRepostsPresentImp(String token, long weiboId, WeiboSource repostSource,
                                  WeiboSource localWeiboSource,
                                  WeiboRepostsView repostsView) {
        mToken = token;
        mServerRepostSource = repostSource;
        mLocalWeiboSource = localWeiboSource;
        mLocalUrlSource = new LocalUrlSource();
        mServerUrlSource = new ServerUrlSource();
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
        mWeiboObservable = RxBus.getDefault().register(Event.EVENT_REPOST_WEIBO_SUCCESS);
        mWeiboObservable.filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return weibo.getId() == mWeiboId;
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        doSpanNext(weibo);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mWeobos.add(0, weibo);
                        mWeiboRepostsView.onRepostWeiboSuccess(mWeobos);
                    }
                });

        mWeiboRefreshObservable = RxBus.getDefault().register(Event.EVENT_REPOST_WEIBO_REFRESH_COMPLETE);
        mWeiboRefreshObservable.filter(new Func1<List<Weibo>, Boolean>() {
                @Override
                public Boolean call(List<Weibo> weibos) {
                    return weibos != null && weibos.size() > 0 && weibos.get(0).getId() == mWeiboId;
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Weibo>>() {
                @Override
                public void call(List<Weibo> weibos) {
                    LogUtil.d(WeiboRepostsPresentImp.this, "accept refresh event");

                    addRefreshDate(weibos);
                }
            });
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
        return mServerRepostSource.getRepostWeibos(mToken, mWeiboId, 0, maxId, PAGE_COUNET, 1)
                .compose(new ErrorCheckerTransformer<QueryRepostWeiboResponse>())
                .flatMap(new Func1<QueryRepostWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(QueryRepostWeiboResponse queryRepostWeiboResponse) {
                        updateWeiboRepostCount(queryRepostWeiboResponse);
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

    private void updateWeiboRepostCount(final QueryRepostWeiboResponse queryRepostWeiboResponse) {
        mLocalWeiboSource.getWeiboById(mToken, mWeiboId)
        .filter(new Func1<Weibo, Boolean>() {
            @Override
            public Boolean call(Weibo weibo) {
                return weibo != null;
            }
        }).doOnNext(new Action1<Weibo>() {
            @Override
            public void call(Weibo weibo) {
                weibo.setReposts_count(queryRepostWeiboResponse.getTotal_number());
                weibo.setUpdate_time(System.currentTimeMillis());
                mLocalWeiboSource.saveWeibo(mToken, weibo);
            }
        }).subscribe(new Subscriber<Weibo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Weibo weibo) {
                RxBus.getDefault().post(Event.EVENT_WEIBO_UPDATE, weibo);
            }
        });
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(Event.EVENT_REPOST_WEIBO_SUCCESS, mWeiboObservable);
        RxBus.getDefault().unregister(Event.EVENT_REPOST_WEIBO_REFRESH_COMPLETE, mWeiboRefreshObservable);
    }

    protected void doSpanNext(List<Weibo> weibos) {
        List<String> shortUrls  = SpannableStringUtil.getWeiboTextHttpUrl(weibos);
        Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap = UrlUtil.getShortUrlInfos(shortUrls, mServerUrlSource,
                mLocalUrlSource, mToken);
        for (Weibo weibo : weibos) {
            SpannableStringUtil.paraeSpannable(weibo, shortLongLinkMap);
        }
    }

    protected void doSpanNext(Weibo weibo) {
        List<String> shortUrls  = SpannableStringUtil.getWeiboTextHttpUrl(weibo, false, null);
        Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap = UrlUtil.getShortUrlInfos(shortUrls, mServerUrlSource,
                mLocalUrlSource, mToken);
        SpannableStringUtil.paraeSpannable(weibo, shortLongLinkMap);
    }
}

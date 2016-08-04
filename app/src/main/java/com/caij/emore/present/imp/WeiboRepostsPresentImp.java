package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.present.view.WeiboRepostsView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.UrlSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboRepostsPresentImp implements WeiboRepostsPresent {

    private static final int PAGE_COUNET = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    WeiboSource mServerRepostSource;
    WeiboRepostsView mWeiboRepostsView;
    List<Weibo> mWeobos;
    Observable<Weibo> mWeiboObservable;
    private UrlSource mServerUrlSource;
    private UrlSource mLocalUrlSource;
    Observable<List<Weibo>> mWeiboRefreshObservable;

    public WeiboRepostsPresentImp(String token, long weiboId, WeiboSource repostSource, UrlSource servreUrlSource,
                                  UrlSource localUrlSource, WeiboRepostsView repostsView) {
        mToken = token;
        mServerRepostSource = repostSource;
        mLocalUrlSource = localUrlSource;
        mServerUrlSource = servreUrlSource;
        mWeiboRepostsView = repostsView;
        mWeiboId = weiboId;
        mLoginCompositeSubscription = new CompositeSubscription();
        mWeobos = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
        initEventListener();

        Subscription subscription = createObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mWeiboRepostsView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mWeiboRepostsView.onLoadComplete(false);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeobos.clear();
                        mWeobos.addAll(weibos);
                        mWeiboRepostsView.setEntities(weibos);
                        if (weibos.size() == 0) {
                            mWeiboRepostsView.onEmpty();
                        }else {
                            mWeiboRepostsView.onLoadComplete(weibos.size() >= PAGE_COUNET - 5);
                        }
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mWeiboObservable = RxBus.get().register(Key.EVENT_REPOST_WEIBO_SUCCESS);
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

        mWeiboRefreshObservable = RxBus.get().register(Key.EVENT_REPOST_WEIBO_REFRESH_COMPLETE);
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

                    mWeobos.clear();
                    mWeobos.addAll(weibos);
                    mWeiboRepostsView.setEntities(weibos);

                    if (weibos.size() == 0) {
                        mWeiboRepostsView.onEmpty();
                    }else {
                        mWeiboRepostsView.onLoadComplete(weibos.size() >= PAGE_COUNET - 5);
                    }
                }
            });
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
                        mWeiboRepostsView.setEntities(mWeobos);
                        mWeiboRepostsView.onLoadComplete(weibos.size() >= 15);
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    private  Observable<List<Weibo>> createObservable(long maxId, final boolean isRefresh) {
        return mServerRepostSource.getRepostWeibos(mToken, mWeiboId, 0, maxId, PAGE_COUNET, 1)
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
        mLoginCompositeSubscription.clear();
        RxBus.get().unregister(Key.EVENT_REPOST_WEIBO_SUCCESS, mWeiboObservable);
        RxBus.get().unregister(Key.EVENT_REPOST_WEIBO_REFRESH_COMPLETE, mWeiboRefreshObservable);
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
        List<String> shortUrls  = SpannableStringUtil.getWeiboTextHttpUrl(weibo, null);
        Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap = UrlUtil.getShortUrlInfos(shortUrls, mServerUrlSource,
                mLocalUrlSource, mToken);
        SpannableStringUtil.paraeSpannable(weibo, shortLongLinkMap);
    }
}

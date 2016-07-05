package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.FriendWeiboPresent;
import com.caij.weiyo.present.view.TimeLineWeiboView;
import com.caij.weiyo.source.DefaultResponseSubscriber;
import com.caij.weiyo.source.WeiboSource;
import com.caij.weiyo.utils.SpannableStringUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/5/31.
 */
public class FriendWeiboPresentImp extends AbsTimeLinePresent implements FriendWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private WeiboSource mLocalWeiboSource;
    private List<Weibo> mWeibos;

    public FriendWeiboPresentImp(String token, TimeLineWeiboView view, WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource) {
        super(token, view, serverWeiboSource);
        mLocalWeiboSource = localWeiboSource;
        mWeibos = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        Subscription subscription = mLocalWeiboSource.getFriendWeibo(mToken, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {
                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        SpannableStringUtil.paraeSpannable(weibo, mView.getContent().getApplicationContext());
                        return weibo;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setWeibos(mWeibos);
                        mView.toRefresh();
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onRefresh() {
        Subscription subscription = mServerWeiboSource.getFriendWeibo(mToken, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        SpannableStringUtil.paraeSpannable(weibo, mView.getContent().getApplicationContext());
                        return weibo;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        mLocalWeiboSource.saveFriendWeibo(mToken, weibos);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplite();
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onComnLoadError();
                        mView.onRefreshComplite();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setWeibos(mWeibos);
                        if (weibos.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplite(weibos.size() >= PAGE_COUNT);
                        }
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onLoadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription subscription = mServerWeiboSource.getFriendWeibo(mToken, 0, maxId, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return !mWeibos.contains(weibo);
                    }
                })
                .map(new Func1<Weibo, Weibo>() {

                    @Override
                    public Weibo call(Weibo weibo) {
                        toGetImageSize(weibo);
                        SpannableStringUtil.paraeSpannable(weibo, mView.getContent().getApplicationContext());
                        return weibo;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onComnLoadError();
                        mView.onLoadComplite(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setWeibos(mWeibos);
                        mView.onLoadComplite(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

package com.caij.emore.present.imp;

import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.present.view.FriendWeiboView;
import com.caij.emore.source.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

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
public class FriendWeiboPresentImp extends AbsTimeLinePresent<FriendWeiboView> implements FriendWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private List<Weibo> mWeibos;

    public FriendWeiboPresentImp(String token, FriendWeiboView view, WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource) {
        super(token, view, serverWeiboSource, localWeiboSource);
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
                .compose(new WeiboTransformer())
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toRefresh();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);
                        mView.toRefresh();
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void refresh() {
        Subscription subscription = mServerWeiboSource.getFriendWeibo(mToken, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<List<Weibo>, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(List<Weibo> weibos) {
                        return Observable.from(weibos);
                    }
                })
                .compose(new WeiboTransformer())
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                    mLocalWeiboSource.saveWeibos(mToken, weibos);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplete();
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);
                        if (weibos.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1);
                        }
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
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
                .compose(new WeiboTransformer())
                .toList()
                .doOnNext(new Action1<List<Weibo>>() {
                    @Override
                    public void call(List<Weibo> weibos) {
                        mLocalWeiboSource.saveWeibos(mToken, weibos);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);
                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 2); //这里有一条重复的 所以需要-1
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

}

package com.caij.emore.present.imp;

import com.caij.emore.bean.Account;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.UserWeiboPresent;
import com.caij.emore.present.view.TimeLineWeiboView;
import com.caij.emore.source.WeiboSource;

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
public class UserWeiboPresentImp extends AbsTimeLinePresent<TimeLineWeiboView> implements UserWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private List<Weibo> mWeibos;

    private String mUsername;
    private int mFeature = 0;

    public UserWeiboPresentImp(Account account, String name, TimeLineWeiboView view, WeiboSource serverWeiboSource, WeiboSource localWeiboSource) {
        super(account, view, serverWeiboSource, localWeiboSource);
        mUsername = name;
        mWeibos = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }

    @Override
    public void filter(int feature) {
        if (mFeature != feature) {
            mFeature = feature;
            mWeibos.clear();
            mView.setEntities(mWeibos);
            refresh();
        }
    }

    @Override
    public void refresh() {
        Subscription subscription = createObservable(0, true)
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
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
                            mView.onLoadComplete(weibos.size() >= PAGE_COUNT);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription subscription = createObservable(maxId, false)
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onDefaultLoadError();
                        mView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setEntities(mWeibos);
                        mView.onLoadComplete(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private Observable<List<Weibo>> createObservable(long maxId, final boolean isRefresh) {
        return mServerWeiboSource.getUseWeibo(mAccount.getWeicoToken().getAccess_token(), mUsername, mFeature, 0, maxId, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

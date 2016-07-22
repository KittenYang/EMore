package com.caij.emore.present.imp;

import com.caij.emore.bean.Attitude;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.present.view.BaseListView;
import com.caij.emore.source.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/28.
 */
public class WeiboAttitudesPresentImp implements WeiboRepostsPresent {

    private static final int PAGE_COUNET = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    WeiboSource mServerRepostSource;
    BaseListView<Attitude> mView;
    List<Attitude> mAttitudes;
    private int mPage;

    public WeiboAttitudesPresentImp(String token, long weiboId, WeiboSource repostSource, BaseListView<Attitude> view) {
        mToken = token;
        mServerRepostSource = repostSource;
        mView = view;
        mWeiboId = weiboId;
        mLoginCompositeSubscription = new CompositeSubscription();
        mAttitudes = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
        Subscription subscription = createObservable(1)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.onDefaultLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(attitudes);
                        if (attitudes.size() == 0) {
                            mView.onEmpty();
                        }else {
                            mView.onLoadComplete(attitudes.size() >= PAGE_COUNET);
                        }
                        mPage = 2;
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void loadMore() {
        Subscription subscription = createObservable(mPage)
                .subscribe(new DefaultResponseSubscriber<List<Attitude>>(mView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mView.onDefaultLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Attitude> attitudes) {
                        mAttitudes.addAll(attitudes);
                        mView.setEntities(attitudes);
                        mView.onLoadComplete(attitudes.size() >= 15);
                        mPage ++;
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    private  Observable<List<Attitude>> createObservable(int page) {
        return mServerRepostSource.getWeiboAttiyudes(mToken, mWeiboId, page, PAGE_COUNET)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

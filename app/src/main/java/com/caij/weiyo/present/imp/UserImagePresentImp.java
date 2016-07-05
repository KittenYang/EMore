package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.UserWeiboResponse;
import com.caij.weiyo.present.UserWeiboPresent;
import com.caij.weiyo.present.view.TimeLineWeiboImageView;
import com.caij.weiyo.source.WeiboSource;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/5/31.
 */
public class UserImagePresentImp implements UserWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private String mToken;
    private TimeLineWeiboImageView mView;
    private WeiboSource mServerWeiboSource;
    private CompositeSubscription mLoginCompositeSubscription;
    private List<Weibo> mWeibos;
    private String mUsername;
    private List<PicUrl> mPicUrl;

    public UserImagePresentImp(String token, String name, TimeLineWeiboImageView view, WeiboSource serverWeiboSource) {
        mToken = token;
        mView = view;
        mUsername = name;
        mServerWeiboSource = serverWeiboSource;
        mLoginCompositeSubscription = new CompositeSubscription();
        mWeibos = new ArrayList<>();
        mPicUrl = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onRefresh() {
        Subscription subscription = mServerWeiboSource.getUseWeibo(mToken, mUsername, 2, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mPicUrl.addAll(weibo.getPic_urls());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Weibo>>() {
                    @Override
                    public void onCompleted() {
                        mView.onRefreshComplite();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.onComnLoadError();
                        mView.onRefreshComplite();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setImages(mPicUrl);
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
    public void deleteWeibo(Weibo weibo, int position) {

    }

    @Override
    public void collectWeibo(Weibo weibo) {

    }

    @Override
    public void uncollectWeibo(Weibo weibo) {

    }


    @Override
    public void onLoadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        Subscription subscription = mServerWeiboSource.getUseWeibo(mToken, mUsername, 2, 0, maxId, PAGE_COUNT, 1)
                .flatMap(new Func1<UserWeiboResponse, Observable<Weibo>>() {
                    @Override
                    public Observable<Weibo> call(UserWeiboResponse response) {
                        return Observable.from(response.getStatuses());
                    }
                })
                .filter(new Func1<Weibo, Boolean>() {
                    @Override
                    public Boolean call(Weibo weibo) {
                        return !mWeibos.contains(weibo);
                    }
                })
                .doOnNext(new Action1<Weibo>() {
                    @Override
                    public void call(Weibo weibo) {
                        mPicUrl.addAll(weibo.getPic_urls());
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
                        mView.onComnLoadError();
                        mView.onLoadComplite(true);
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setImages(mPicUrl);
                        mView.onLoadComplite(weibos.size() >= PAGE_COUNT - 1); //这里有一条重复的 所以需要-1
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void onFirstVisible() {
        onRefresh();
    }

    @Override
    public void filter(int feature) {

    }
}

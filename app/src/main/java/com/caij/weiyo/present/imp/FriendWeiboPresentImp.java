package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.FriendWeiboPresent;
import com.caij.weiyo.present.view.FriendWeiboView;
import com.caij.weiyo.source.DefaultResponseSubscriber;
import com.caij.weiyo.source.WeiboSource;
import com.caij.weiyo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/5/31.
 */
public class FriendWeiboPresentImp implements FriendWeiboPresent {

    private final static int PAGE_COUNT = 20;

    private String mToken;
    private FriendWeiboView mView;
    private WeiboSource mServerWeiboSource;
    private WeiboSource mLocalWeiboSource;
    private CompositeSubscription mLoginCompositeSubscription;
    private List<Weibo> mWeibos;

    public FriendWeiboPresentImp(String token, FriendWeiboView view, WeiboSource serverWeiboSource,
                                 WeiboSource localWeiboSource) {
        mToken = token;
        mView = view;
        mServerWeiboSource = serverWeiboSource;
        mLocalWeiboSource = localWeiboSource;
        mLoginCompositeSubscription = new CompositeSubscription();
        mWeibos = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        mLocalWeiboSource.getFriendWeibo(mToken, 0, 0, PAGE_COUNT, 1)
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
                        mView.setFriendWeibo(mWeibos);
                        mView.toRefresh();
                    }
                });
    }

    @Override
    public void onRefresh() {
        mServerWeiboSource.getFriendWeibo(mToken, 0, 0, PAGE_COUNT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onError(Exception e) {
                        LogUtil.d(this, "load weibo error");
                        mView.onComnLoadError();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.clear();
                        mWeibos.addAll(weibos);
                        mView.setFriendWeibo(mWeibos);
                        saveFriendWeibo(weibos);
                    }
                });

    }

    private void saveFriendWeibo(List<Weibo> weibos) {
        mLocalWeiboSource.saveFriendWeibo(mToken, weibos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(this, "save weibo error");
                    }

                    @Override
                    public void onNext(Void aVoid) {

                    }
                });
    }


    @Override
    public void onLoadMore() {
        long maxId = 0;
        if (mWeibos.size() > 0) {
            maxId = mWeibos.get(mWeibos.size() - 1).getId();
        }
        mServerWeiboSource.getFriendWeibo(mToken, 0, maxId, PAGE_COUNT, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<List<Weibo>>(mView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onError(Exception e) {
                        mView.onComnLoadError();
                    }

                    @Override
                    public void onNext(List<Weibo> weibos) {
                        mWeibos.addAll(weibos);
                        mView.setFriendWeibo(mWeibos);
                        saveFriendWeibo(weibos);
                    }
                });

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

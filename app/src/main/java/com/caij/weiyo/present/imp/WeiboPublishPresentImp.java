package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.WeiboPublishPresent;
import com.caij.weiyo.source.PublishWeiboSource;
import com.caij.weiyo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/24.
 */
public class WeiboPublishPresentImp implements WeiboPublishPresent {

    private final CompositeSubscription mLoginCompositeSubscription;
    private PublishWeiboSource mServerPublishWeiboSource;
    private String mToken;
    private String mSource;

    public WeiboPublishPresentImp(String token, String source, PublishWeiboSource serverPublishWeiboSource) {
        mServerPublishWeiboSource = serverPublishWeiboSource;
        mToken = token;
        mSource = source;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void publishWeibo(String content, ArrayList<String> imagePaths) {
        Observable<Weibo> publishWeiboObservable;
        if (imagePaths == null || imagePaths.size() == 0) {
            publishWeiboObservable = mServerPublishWeiboSource.publishWeiboOfText(mToken, content);
        }else if (imagePaths.size() == 1) {
            publishWeiboObservable = mServerPublishWeiboSource.publishWeiboOfOneImage(mToken, mSource, content,
                    imagePaths.get(0));
        }else {
            publishWeiboObservable = mServerPublishWeiboSource.publishWeiboOfMultiImage(mToken, mSource, content, imagePaths);
        }
        Subscription subscription = publishWeiboObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(WeiboPublishPresentImp.this, e.getMessage());
                    }

                    @Override
                    public void onNext(Weibo weibo) {

                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }
}

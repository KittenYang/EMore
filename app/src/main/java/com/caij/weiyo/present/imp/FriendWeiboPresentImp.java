package com.caij.weiyo.present.imp;

import com.caij.weiyo.present.FriendWeiboPresent;
import com.caij.weiyo.present.view.FriendWeiboView;
import com.caij.weiyo.source.WeiboSource;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/5/31.
 */
public class FriendWeiboPresentImp implements FriendWeiboPresent {

    private String mToken;
    private FriendWeiboView mView;
    private WeiboSource mWeiboSource;
    private CompositeSubscription mLoginCompositeSubscription;

    public FriendWeiboPresentImp(String token, FriendWeiboView view, WeiboSource weiboSource) {
        mToken = token;
        mView = view;
        mWeiboSource = weiboSource;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {
//        mWeiboSource.getFriendWeibo(mToken, )
    }

    @Override
    public void onLoadMore(long maxId, int feature) {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

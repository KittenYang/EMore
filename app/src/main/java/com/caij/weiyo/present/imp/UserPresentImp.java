package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.SimpleUserPresent;
import com.caij.weiyo.present.view.SimpleUserView;
import com.caij.weiyo.source.UserSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/3.
 */
public class UserPresentImp implements SimpleUserPresent {

    private final CompositeSubscription mLoginCompositeSubscription;
    private SimpleUserView mUserView;
    private UserSource mServerUserSource;
    private UserSource mLocalUserSource;
    private String mToken;
    private long mUid;

    public UserPresentImp(String token, long uid, SimpleUserView userView, UserSource serverUserSource, UserSource localUserSource) {
        mUserView = userView;
        mServerUserSource = serverUserSource;
        mLocalUserSource = localUserSource;
        mToken = token;
        mUid = uid;
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
    public void getWeiboUserInfoByUid() {
        mUserView.showGetUserLoading(true);
        Subscription localSubscription = mLocalUserSource.getWeiboUserInfoByUid(mToken, mUid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.onComnLoadError();
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        Subscription serverSubscription = mServerUserSource.getWeiboUserInfoByUid(mToken, mUid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mUserView.showGetUserLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.onComnLoadError();
                        mUserView.showGetUserLoading(false);
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        mLoginCompositeSubscription.add(localSubscription);
        mLoginCompositeSubscription.add(serverSubscription);
    }
}

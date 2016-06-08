package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.UserPresent;
import com.caij.weiyo.present.view.UserView;
import com.caij.weiyo.source.UserSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/3.
 */
public class UserPresentImp implements UserPresent {

    private final CompositeSubscription mLoginCompositeSubscription;
    private UserView mUserView;
    private UserSource mServerUserSource;
    private UserSource mLocalUserSource;
    private String mToken;

    public UserPresentImp(String token, UserView userView, UserSource serverUserSource, UserSource localUserSource) {
        mUserView = userView;
        mServerUserSource = serverUserSource;
        mLocalUserSource = localUserSource;
        mToken = token;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void getWeiboUserInfoByName(String name) {
        Subscription localSubscription = mLocalUserSource.getWeiboUserInfoByName(mToken, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        Subscription serverSubscription = mServerUserSource.getWeiboUserInfoByName(mToken, name)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mLocalUserSource.saveWeiboUser(user);
                    }
                })
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
        mLoginCompositeSubscription.add(localSubscription);
        mLoginCompositeSubscription.add(serverSubscription);
    }

    @Override
    public void getWeiboUserInfoByUid(long uid) {
        Subscription localSubscription = mLocalUserSource.getWeiboUserInfoByUid(mToken, uid)
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
        Subscription serverSubscription = mServerUserSource.getWeiboUserInfoByUid(mToken, uid)
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
        mLoginCompositeSubscription.add(localSubscription);
        mLoginCompositeSubscription.add(serverSubscription);
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

}

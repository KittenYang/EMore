package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.UserInfoDetailPresent;
import com.caij.weiyo.present.view.DetailUserView;
import com.caij.weiyo.source.UserSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/30.
 */
public class UserInfoDetailPresentImp implements UserInfoDetailPresent {

    private final CompositeSubscription mLoginCompositeSubscription;
    private DetailUserView mUserView;
    private UserSource mServerUserSource;
    private UserSource mLocalUserSource;
    private String mToken;
    private String mName;

    public UserInfoDetailPresentImp(String token, String name, DetailUserView userView,
                                    UserSource serverUserSource, UserSource localUserSource) {
        mUserView = userView;
        mServerUserSource = serverUserSource;
        mLocalUserSource = localUserSource;
        mToken = token;
        mName = name;
        mLoginCompositeSubscription = new CompositeSubscription();
    }


    @Override
    public void follow() {
        mUserView.showFollowLoading(true);
        Subscription subscription = mServerUserSource.followUser(mToken, mName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mUserView.showFollowLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.onComnLoadError();
                        mUserView.showFollowLoading(false);
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.onFollowSuccess();
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void unFollow() {
        mUserView.showFollowLoading(true);
        Subscription subscription = mServerUserSource.unfollowUser(mToken, mName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mUserView.showFollowLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.onComnLoadError();
                        mUserView.showFollowLoading(false);
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.onUnfollowSuccess();
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void getWeiboUserInfoByName() {
        mUserView.showGetUserLoading(true);
        Subscription localSubscription = mLocalUserSource.getWeiboUserInfoByName(mToken, mName)
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
        Subscription serverSubscription = mServerUserSource.getWeiboUserInfoByName(mToken, mName)
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

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

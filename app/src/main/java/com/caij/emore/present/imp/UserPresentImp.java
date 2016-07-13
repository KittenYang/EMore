package com.caij.emore.present.imp;

import com.caij.emore.database.bean.User;
import com.caij.emore.present.SimpleUserPresent;
import com.caij.emore.present.view.SimpleUserView;
import com.caij.emore.source.UserSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
        Subscription localSubscription = mLocalUserSource.getWeiboUserInfoByUid(mToken, mUid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        Subscription serverSubscription = mServerUserSource.getWeiboUserInfoByUid(mToken, mUid)
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

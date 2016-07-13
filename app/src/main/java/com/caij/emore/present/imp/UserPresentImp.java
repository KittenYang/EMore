package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.SimpleUserPresent;
import com.caij.emore.present.view.SimpleUserView;
import com.caij.emore.source.UserSource;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
        Observable<User> localObservable =   mLocalUserSource.getWeiboUserInfoByUid(mToken, mUid);
        Observable<User> serverObservable =   mServerUserSource.getWeiboUserInfoByUid(mToken, mUid);
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null && System.currentTimeMillis() - user.getUpdate_time() < 60 * 60 * 1000;
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
        mLoginCompositeSubscription.add(subscription);
    }
}

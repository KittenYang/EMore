package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.UserInfoDetailPresent;
import com.caij.emore.present.view.DetailUserView;
import com.caij.emore.source.DefaultResponseSubscriber;
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
        mUserView.showDialogLoading(true);
        Subscription subscription = mServerUserSource.followUser(mToken, mName)
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
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.onDefaultLoadError();
                        mUserView.showDialogLoading(false);
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
        mUserView.showDialogLoading(true);
        Subscription subscription = mServerUserSource.unfollowUser(mToken, mName)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mLocalUserSource.saveWeiboUser(user);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<User>(mUserView) {
                    @Override
                    public void onCompleted() {
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mUserView.showDialogLoading(false);
                        mUserView.onDefaultLoadError();
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
        mUserView.showDialogLoading(true);
        Observable<User> localObservable =   mLocalUserSource.getWeiboUserInfoByName(mToken, mName);
        Observable<User> serverObservable =   mServerUserSource.getWeiboUserInfoByName(mToken, mName)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mLocalUserSource.saveWeiboUser(user);
                    }
                });
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null && System.currentTimeMillis() - user.getUpdate_time() < 10 * 60 * 1000;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.showDialogLoading(false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            int code  = httpException.code();
                            if (code == 400) {
                                mUserView.showHint(R.string.user_undefine);
                                return;
                            }
                        }
                        mUserView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onRefresh() {
        Observable<User> serverObservable =   mServerUserSource.getWeiboUserInfoByName(mToken, mName)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mLocalUserSource.saveWeiboUser(user);
                    }
                });
        Subscription subscription = serverObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        mUserView.onRefreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mUserView.showDialogLoading(false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            int code  = httpException.code();
                            if (code == 400) {
                                mUserView.showHint(R.string.user_undefine);
                                return;
                            }
                        }
                        mUserView.onDefaultLoadError();
                        mUserView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

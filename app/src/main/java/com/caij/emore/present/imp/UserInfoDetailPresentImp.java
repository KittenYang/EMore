package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.UserInfoDetailPresent;
import com.caij.emore.ui.view.DetailUserView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.UserSource;
import com.caij.emore.utils.rxjava.DefaultTransformer;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/30.
 */
public class UserInfoDetailPresentImp extends AbsBasePresent implements UserInfoDetailPresent {

    private DetailUserView mUserView;
    private UserSource mServerUserSource;
    private UserSource mLocalUserSource;
    private String mToken;
    private String mName;
    private Observable<User> mUserObservable;

    public UserInfoDetailPresentImp(String token, String name, DetailUserView userView,
                                    UserSource serverUserSource, UserSource localUserSource) {
        mUserView = userView;
        mServerUserSource = serverUserSource;
        mLocalUserSource = localUserSource;
        mToken = token;
        mName = name;
    }


    @Override
    public void follow() {
        mUserView.showDialogLoading(true);
        Subscription subscription = mServerUserSource.followUser(mToken, mName)
                .compose(new ErrorCheckerTransformer<User>())
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mLocalUserSource.saveWeiboUser(user);
                    }
                })
                .compose(new DefaultTransformer<User>())
                .subscribe(new DefaultResponseSubscriber<User>(mUserView) {
                    @Override
                    public void onCompleted() {
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.onFollowSuccess();
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void unFollow() {
        mUserView.showDialogLoading(true);
        Subscription subscription = mServerUserSource.unfollowUser(mToken, mName)
                .compose(new ErrorCheckerTransformer<User>())
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mLocalUserSource.saveWeiboUser(user);
                    }
                })
                .compose(new SchedulerTransformer<User>())
                .subscribe(new DefaultResponseSubscriber<User>(mUserView) {
                    @Override
                    public void onCompleted() {
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.onUnfollowSuccess();
                    }
                });
        addSubscription(subscription);
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
                        return user != null;
                    }
                })
                .compose(new DefaultTransformer<User>())
                .subscribe(new DefaultResponseSubscriber<User>(mUserView) {
                    @Override
                    public void onCompleted() {
                        mUserView.showDialogLoading(false);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mUserView.showDialogLoading(false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            int code  = httpException.code();
                            if (code == 400) {
                                mUserView.showHint(R.string.user_undefine);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onRefresh() {
        Observable<User> serverObservable = mServerUserSource.getWeiboUserInfoByName(mToken, mName)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mLocalUserSource.saveWeiboUser(user);
                    }
                });
        Subscription subscription = serverObservable
                .compose(new DefaultTransformer<User>())
                .subscribe(new DefaultResponseSubscriber<User>(mUserView) {
                    @Override
                    public void onCompleted() {
                        mUserView.onRefreshComplete();
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mUserView.onRefreshComplete();
                        mUserView.showDialogLoading(false);
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            int code  = httpException.code();
                            if (code == 400) {
                                mUserView.showHint(R.string.user_undefine);
                            }
                        }
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {
        mUserObservable =  RxBus.getDefault().register(Event.EVENT_USER_UPDATE);
        mUserObservable.compose(new SchedulerTransformer<User>())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mUserView.setUser(user);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(Event.EVENT_USER_UPDATE, mUserObservable);
    }
}

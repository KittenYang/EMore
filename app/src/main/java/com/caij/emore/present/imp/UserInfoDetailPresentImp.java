package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.manager.UserManager;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.UserInfoDetailPresent;
import com.caij.emore.remote.UserApi;
import com.caij.emore.ui.view.DetailUserView;
import com.caij.emore.api.ex.DefaultTransformer;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/6/30.
 */
public class UserInfoDetailPresentImp extends AbsBasePresent implements UserInfoDetailPresent {

    private DetailUserView mUserView;
    private UserApi mUserApi;
    private UserManager mUserManager;
    private String mName;

    private User mUser;

    public UserInfoDetailPresentImp(String name, DetailUserView userView,
                                    UserApi userApi, UserManager userManager) {
        mUserView = userView;
        mUserApi = userApi;
        mUserManager = userManager;
        mName = name;
    }

    @Override
    public void follow() {
        mUserView.showDialogLoading(true);
        Subscription subscription = mUserApi.followUser(mName, mUser.getId())
                .compose(new ErrorCheckerTransformer<User>())
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mUserManager.saveUser(user);
                    }
                })
                .compose(new DefaultTransformer<User>())
                .subscribe(new ResponseSubscriber<User>(mUserView) {
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
        Subscription subscription = mUserApi.unfollowUser(mName, mUser.getId())
                .compose(new ErrorCheckerTransformer<User>())
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mUserManager.saveUser(user);
                    }
                })
                .compose(SchedulerTransformer.<User>create())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mUserView.showDialogLoading(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mUserView.showDialogLoading(false);
                    }
                })
                .subscribe(new ResponseSubscriber<User>(mUserView) {

                    @Override
                    protected void onFail(Throwable e) {
                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.onUnFollowSuccess();
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getUserInfoByName() {
        Observable<User> localObservable = RxUtil.createDataObservable(new RxUtil.Provider<User>() {
            @Override
            public User getData() {
                return mUserManager.getUserByName(mName);
            }
        });
        Observable<User> serverObservable =  mUserApi.getWeiboUserByName(mName)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mUserManager.saveUser(user);
                    }
                });
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .filter(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null;
                    }
                })
                .compose(ErrorCheckerTransformer.<User>create())
                .compose(SchedulerTransformer.<User>create())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mUserView.showDialogLoading(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mUserView.showDialogLoading(false);
                    }
                })
                .subscribe(new ResponseSubscriber<User>(mUserView) {

                    @Override
                    protected void onFail(Throwable e) {
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
                        mUser = user;
                        mUserView.setUser(user);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onRefresh() {
        Subscription subscription = mUserApi.getWeiboUserByName(mName)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mUserManager.saveUser(user);
                    }
                })
                .compose(new DefaultTransformer<User>())
                .subscribe(new ResponseSubscriber<User>(mUserView) {
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

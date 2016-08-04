package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.present.view.LoginView;
import com.caij.emore.source.LoginSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/5/28.
 */
public class LoginPresentImp implements LoginPresent{

    private LoginSource mLoginSource;
    private LoginView mLoginView;
    private CompositeSubscription mLoginCompositeSubscription;

    public LoginPresentImp(LoginSource loginSource, LoginView loginView) {
        mLoginSource = loginSource;
        mLoginView = loginView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void getAccessToken(String clientId, String clientSecret, String grantType, String redirectUrL) {
        mLoginView.showDialogLoading(true, R.string.logining);
        Subscription loginSubscription = mLoginSource.getAccessToken(clientId, clientSecret, grantType, redirectUrL)
                .doOnNext(new Action1<AccessToken>() {
                    @Override
                    public void call(AccessToken accessToken) {
                        UserPrefs.get().setEMoreToken(accessToken);
                    }
                })
                .compose(new SchedulerTransformer<AccessToken>())
                .subscribe(new DefaultResponseSubscriber<AccessToken>(mLoginView) {
                    @Override
                    public void onCompleted() {
                        mLoginView.showDialogLoading(false, R.string.logining);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mLoginView.showDialogLoading(false, R.string.logining);
                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        mLoginView.onLoginSuccess(accessToken);
                    }
                });
        mLoginCompositeSubscription.add(loginSubscription);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
        mLoginView = null;
    }
}

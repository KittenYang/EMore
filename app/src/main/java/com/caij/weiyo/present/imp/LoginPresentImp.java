package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.present.LoginPresent;
import com.caij.weiyo.present.view.LoginView;
import com.caij.weiyo.source.LoginSource;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
        mLoginView.showLoading(true);
        Subscription loginSubscription = mLoginSource.getAccessToken(clientId, clientSecret, grantType, redirectUrL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AccessToken>() {
                    @Override
                    public void onCompleted() {
                        mLoginView.showLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.showLoading(false);
                        mLoginView.onComnLoadError();
                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        mLoginView.showLoading(false);
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

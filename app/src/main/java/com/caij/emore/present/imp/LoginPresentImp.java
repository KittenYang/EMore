package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.present.view.LoginView;
import com.caij.emore.source.LoginSource;

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
        mLoginView.showDialogLoading(true, R.string.logining);
        Subscription loginSubscription = mLoginSource.getAccessToken(clientId, clientSecret, grantType, redirectUrL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AccessToken>() {
                    @Override
                    public void onCompleted() {
                        mLoginView.showDialogLoading(false, R.string.logining);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.showDialogLoading(false, R.string.logining);
                        mLoginView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(AccessToken accessToken) {
                        UserPrefs.get().setEMoreToken(accessToken);
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
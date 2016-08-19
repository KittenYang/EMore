package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.ui.view.LoginView;
import com.caij.emore.source.LoginSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/5/28.
 */
public class LoginPresentImp extends AbsBasePresent implements LoginPresent{

    private LoginSource mLoginSource;
    private LoginView mLoginView;

    public LoginPresentImp(LoginSource loginSource, LoginView loginView) {
        mLoginSource = loginSource;
        mLoginView = loginView;
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
        addSubscription(loginSubscription);
    }

    @Override
    public void onCreate() {

    }

}

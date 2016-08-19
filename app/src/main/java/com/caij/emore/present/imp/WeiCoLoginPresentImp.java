package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.ui.view.WeiCoLoginView;
import com.caij.emore.source.LoginSource;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/8.
 */
public class WeiCoLoginPresentImp extends AbsBasePresent implements LoginPresent {

    private LoginSource mLoginSource;
    private WeiCoLoginView mLoginView;

    public WeiCoLoginPresentImp(LoginSource loginSource, WeiCoLoginView loginView) {
        mLoginSource = loginSource;
        mLoginView = loginView;
    }

    @Override
    public void getAccessToken(String clientId, String clientSecret, String grantType, final String redirectUrL) {
        mLoginView.showDialogLoading(true, R.string.logining);
        Subscription loginSubscription = mLoginSource.getAccessToken(clientId, clientSecret, grantType, redirectUrL)
                .doOnNext(new Action1<AccessToken>() {
                    @Override
                    public void call(AccessToken accessToken) {
                        UserPrefs.get().setWeiCoToken(accessToken);
                    }
                })
                .compose(new SchedulerTransformer<AccessToken>())
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
                        mLoginView.onLoginSuccess(accessToken);
                    }
                });
        addSubscription(loginSubscription);
    }

    @Override
    public void onCreate() {

    }

}

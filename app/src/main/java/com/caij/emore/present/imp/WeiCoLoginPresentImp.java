package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.WeiYoApplication;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.present.view.WeiCoLoginView;
import com.caij.emore.source.LoginSource;
import com.sina.weibo.security.WeiboSecurityUtils;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/8.
 */
public class WeiCoLoginPresentImp implements LoginPresent {
    private LoginSource mLoginSource;
    private WeiCoLoginView mLoginView;
    private CompositeSubscription mLoginCompositeSubscription;

    public WeiCoLoginPresentImp(LoginSource loginSource, WeiCoLoginView loginView) {
        mLoginSource = loginSource;
        mLoginView = loginView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void getAccessToken(String clientId, String clientSecret, String grantType, String redirectUrL) {
        mLoginView.showDialogLoading(true, R.string.logining);
        Subscription loginSubscription = mLoginSource.getAccessToken(clientId, clientSecret, grantType, redirectUrL)
                .flatMap(new Func1<AccessToken, Observable<WeiCoLoginResponse>>() {
                    @Override
                    public Observable<WeiCoLoginResponse> call(AccessToken accessToken) {
                        UserPrefs.get().setWeiCoToken(accessToken);
                        String str = WeiboSecurityUtils.getIValue(WeiYoApplication.getInstance());
                        return mLoginSource.weicoLogin(accessToken.getAccess_token(), Key.WEICO_APP_ID, str, "1");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WeiCoLoginResponse>() {
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
                    public void onNext(WeiCoLoginResponse response) {
                        mLoginView.onLoginSuccess(response);
                        UserPrefs.get().setWeiCoLoginInfo(response);
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

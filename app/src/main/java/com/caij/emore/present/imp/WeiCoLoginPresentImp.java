package com.caij.emore.present.imp;

import com.caij.emore.AppApplication;
import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.remote.LoginApi;
import com.caij.emore.ui.view.WeiCoLoginView;
import com.caij.emore.api.ex.SchedulerTransformer;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/7/8.
 */
public class WeiCoLoginPresentImp extends AbsBasePresent implements LoginPresent {

    private LoginApi mLoginApi;
    private WeiCoLoginView mLoginView;

    public WeiCoLoginPresentImp(LoginApi loginApi, WeiCoLoginView loginView) {
        mLoginApi = loginApi;
        mLoginView = loginView;
    }

    @Override
    public void getAccessToken(String clientId, String clientSecret, String grantType, final String redirectUrL, final String username, final String pwd) {
        mLoginView.showDialogLoading(true, R.string.logining);
        Subscription loginSubscription = mLoginApi.getAccessToken(clientId, clientSecret, grantType, redirectUrL)
                .doOnNext(new Action1<Token>() {
                    @Override
                    public void call(Token accessToken) {
                        UserPrefs userPrefs = UserPrefs.get(AppApplication.getInstance());
                        Account account = new Account();
                        long uid = Long.parseLong(accessToken.getUid());
                        account.setUsername(username);
                        account.setPwd(pwd);
                        account.setStatus(Account.STATUS_USING);
                        account.setUid(uid);

                        account.setToken(accessToken);

                        userPrefs.changeAccount(account);
                    }
                })
                .compose(new SchedulerTransformer<Token>())
                .subscribe(new Subscriber<Token>() {
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
                    public void onNext(Token accessToken) {
                        mLoginView.onLoginSuccess(accessToken);
                    }
                });
        addSubscription(loginSubscription);
    }

    @Override
    public void onCreate() {

    }

}

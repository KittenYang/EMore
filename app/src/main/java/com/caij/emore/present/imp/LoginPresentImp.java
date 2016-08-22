package com.caij.emore.present.imp;

import com.caij.emore.AppApplication;
import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.LoginPresent;
import com.caij.emore.ui.view.LoginView;
import com.caij.emore.source.LoginSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import rx.Subscription;
import rx.functions.Action1;

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
    public void getAccessToken(String clientId, String clientSecret, String grantType,
                               String redirectUrL, final String username, final String pwd) {
        mLoginView.showDialogLoading(true, R.string.logining);
        Subscription loginSubscription = mLoginSource.getAccessToken(clientId, clientSecret, grantType, redirectUrL)
                .doOnNext(new Action1<Token>() {
                    @Override
                    public void call(Token accessToken) {
                        UserPrefs userPrefs = UserPrefs.get(AppApplication.getInstance());
                        Account account = new Account();
                        account.setUsername(username);
                        account.setPwd(pwd);
                        accessToken.setKey(accessToken.getUid() + "_emore");
                        account.setEmoreToken(accessToken);
                        account.setStatus(Account.STATUS_USING);
                        account.setUid(Long.parseLong(accessToken.getUid()));
                        userPrefs.commit(account);
                    }
                })
                .compose(new SchedulerTransformer<Token>())
                .subscribe(new DefaultResponseSubscriber<Token>(mLoginView) {
                    @Override
                    public void onCompleted() {
                        mLoginView.showDialogLoading(false, R.string.logining);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mLoginView.showDialogLoading(false, R.string.logining);
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

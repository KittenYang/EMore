package com.caij.emore.remote;

import com.caij.emore.account.Token;
import com.caij.emore.bean.response.WeiCoLoginResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginApi {

    public Observable<Token> getAccessToken(String clientId, String clientSecret, String grantType, String redirectUrL);

    public Observable<WeiCoLoginResponse> weicoLogin(String access_token, String source, String i, String getcookie);

}

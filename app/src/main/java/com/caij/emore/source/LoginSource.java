package com.caij.emore.source;

import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.response.WeiCoLoginResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginSource {

    public Observable<AccessToken> getAccessToken(String clientId, String clientSecret, String grantType, String redirectUrL);

    public Observable<WeiCoLoginResponse> weicoLogin(String access_token,String source, String i, String getcookie);

}
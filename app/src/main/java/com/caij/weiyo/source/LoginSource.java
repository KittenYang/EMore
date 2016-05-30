package com.caij.weiyo.source;

import com.caij.weiyo.bean.AccessToken;

import rx.Observable;
import rx.Observer;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginSource {

    public Observable<AccessToken> getAccessToken(String clientId, String clientSecret, String grantType, String redirectUrL);
}

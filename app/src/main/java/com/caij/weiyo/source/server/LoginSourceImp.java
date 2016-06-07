package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.source.LoginSource;

import rx.Observable;

/**
 * Created by Caij on 2016/5/28.
 */
public class LoginSourceImp implements LoginSource{

    @Override
    public Observable<AccessToken> getAccessToken(String clientId, String clientSecret,
                                                  String code, String redirectUrL) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getAccessToken(clientId, clientSecret, "authorization_code", code, redirectUrL);
    }
}

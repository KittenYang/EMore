package com.caij.emore.source.server;

import com.caij.emore.account.Token;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.source.LoginSource;

import rx.Observable;

/**
 * Created by Caij on 2016/5/28.
 */
public class LoginSourceImp implements LoginSource{

    @Override
    public Observable<Token> getAccessToken(String clientId, String clientSecret,
                                            String code, String redirectUrL) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getAccessToken(clientId, clientSecret, "authorization_code", code, redirectUrL);
    }

    @Override
    public Observable<WeiCoLoginResponse> weicoLogin(String access_token, String source, String i, String getcookie) {
        return WeiCoService.WeiCoFactory.create().loginForGsid(source, i, getcookie);
    }
}

package com.caij.weiyo.source.imp;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.source.UserSource;


import rx.Observable;

/**
 * Created by Caij on 2016/5/30.
 */
public class UserSourceImp implements UserSource{

    @Override
    public Observable<User> getWeiUserInfo(String accessToken, String name) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getWeiBoUserInfo(accessToken, name);
    }

}

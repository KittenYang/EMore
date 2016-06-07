package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.source.UserSource;


import rx.Observable;

/**
 * Created by Caij on 2016/5/30.
 */
public class ServerUserSource implements UserSource{

    @Override
    public Observable<User> getWeiboUserInfoByName(String accessToken, String name) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getWeiBoUserInfoByName(accessToken, name);
    }

    @Override
    public Observable<User> getWeiboUserInfoByUid(String accessToken, long uid) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getWeiBoUserInfoByUid(accessToken, uid);
    }

    @Override
    public Observable<Void> saveWeiboUser(User user) {
        return null;
    }

}

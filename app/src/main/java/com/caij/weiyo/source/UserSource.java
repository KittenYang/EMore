package com.caij.weiyo.source;

import com.caij.weiyo.bean.User;

import rx.Observable;

/**
 * Created by Caij on 2016/5/30.
 */
public interface UserSource {

    public Observable<User> getWeiboUserInfoByName(String accessToken, String name);

    public Observable<User> getWeiboUserInfoByUid(String accessToken, long uid);

    public Observable<Void> saveWeiboUser(User user);
}

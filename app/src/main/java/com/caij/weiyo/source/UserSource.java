package com.caij.weiyo.source;

import com.caij.weiyo.bean.User;

import rx.Observable;

/**
 * Created by Caij on 2016/5/30.
 */
public interface UserSource {

    public Observable<User> getWeiUserInfo(String accessToken, String name);
}

package com.caij.weiyo.source;

import com.caij.weiyo.bean.User;

import rx.Observable;

/**
 * Created by Caij on 2016/6/30.
 */
public interface FollowSource {

    Observable<User> followUser(String accessToken, String screen_name);

    Observable<User> unfollowUser(String accessToken, String screen_name);
}

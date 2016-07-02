package com.caij.weiyo.source.server;

import com.caij.weiyo.Key;
import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.source.FollowSource;

import rx.Observable;

/**
 * Created by Caij on 2016/6/30.
 */
public class ServerFollowSource implements FollowSource {

    private WeiBoService mWeiBoService;

    public ServerFollowSource() {
        mWeiBoService = WeiBoService.Factory.create();
    }

    @Override
    public Observable<User> followUser(String accessToken, String screen_name) {
        return mWeiBoService.followUser(accessToken, screen_name);
    }

    @Override
    public Observable<User> unfollowUser(String accessToken, String screen_name) {
        return mWeiBoService.unfollowUser(accessToken, screen_name);
    }
}

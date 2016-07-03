package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.response.FriendshipResponse;
import com.caij.weiyo.source.UserSource;


import rx.Observable;

/**
 * Created by Caij on 2016/5/30.
 */
public class ServerUserSource implements UserSource{

    private WeiBoService mWeiBoService;

    public ServerUserSource() {
        mWeiBoService = WeiBoService.Factory.create();
    }

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
    public void saveWeiboUser(User user) {
    }

    @Override
    public Observable<User> followUser(String accessToken, String screen_name) {
        return mWeiBoService.followUser(accessToken, screen_name);
    }

    @Override
    public Observable<User> unfollowUser(String accessToken, String screen_name) {
        return mWeiBoService.unfollowUser(accessToken, screen_name);
    }

    @Override
    public Observable<FriendshipResponse> getFriends(String accessToken, long uid, int count, int trim_status, long cursor) {
        return mWeiBoService.getFriends(accessToken, uid, count, trim_status, cursor);
    }

    @Override
    public Observable<FriendshipResponse> getFollowers(String accessToken, long uid, int count, int trim_status, long cursor) {
        return mWeiBoService.getFollowers(accessToken, uid, count, trim_status, cursor);
    }

}

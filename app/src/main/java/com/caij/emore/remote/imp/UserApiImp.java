package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.remote.UserApi;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public class UserApiImp implements UserApi {

    private WeiCoService mWeiCoService;

    public UserApiImp() {
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<User> getUserByName(String name) {
        return mWeiCoService.getWeiBoUserInfoByName(name);
    }

    @Override
    public Observable<User> getUserByUid(long uid) {
        return mWeiCoService.getWeiBoUserInfoByUid(uid);
    }

    @Override
    public Observable<User> followUser(String screen_name, long uid) {
        return mWeiCoService.followUser(screen_name, uid);
    }

    @Override
    public Observable<User> unFollowUser(String screen_name, long uid) {
        return mWeiCoService.unfollowUser(screen_name, uid);
    }

    @Override
    public Observable<FriendshipResponse> getFriends(long uid, int count, int trim_status, long cursor) {
        return mWeiCoService.getFriends(uid, count, trim_status, cursor);
    }

    @Override
    public Observable<FriendshipResponse> getFollowers(long uid, int count, int trim_status, long cursor) {
        return mWeiCoService.getFollowers(uid, count, trim_status, cursor);
    }

    @Override
    public Observable<FriendshipResponse> searchUser(String q, int page, int count) {
        return mWeiCoService.searchUsers(q, count, page);
    }

}

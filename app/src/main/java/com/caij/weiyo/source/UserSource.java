package com.caij.weiyo.source;

import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.response.FriendshipResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/5/30.
 */
public interface UserSource {

    public Observable<User> getWeiboUserInfoByName(String accessToken, String name);

    public Observable<User> getWeiboUserInfoByUid(String accessToken, long uid);

    public void saveWeiboUser(User user);

    Observable<User> followUser(String accessToken, String screen_name);

    Observable<User> unfollowUser(String accessToken, String screen_name);

    Observable<FriendshipResponse> getFriends(String accessToken, long uid, int count, int trim_status, long cursor);

    Observable<FriendshipResponse> getFollowers(String accessToken, long uid, int count, int trim_status, long cursor);
}

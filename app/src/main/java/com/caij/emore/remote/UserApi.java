package com.caij.emore.remote;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.database.bean.User;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/10/9.
 */

public interface UserApi {

    public Observable<User> getWeiboUserByName(String name);

    public Observable<User> getWeiboUserByUid(long uid);

    public Observable<User> followUser(String screen_name, long uid);

    public Observable<User> unfollowUser(String screen_name, long uid);

    public Observable<FriendshipResponse> getFriends(long uid, int count, int trim_status, long cursor);

    public Observable<FriendshipResponse> getFollowers(long uid, int count, int trim_status, long cursor);

    public Observable<FriendshipResponse> searchUser(String q, int page, int count);

}

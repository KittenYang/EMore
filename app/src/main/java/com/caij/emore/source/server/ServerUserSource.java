package com.caij.emore.source.server;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.source.UserSource;


import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/30.
 */
public class ServerUserSource implements UserSource{

    private WeiBoService mWeiBoService;
    private WeiCoService mWeiCoService;

    public ServerUserSource() {
        mWeiBoService = WeiBoService.Factory.create();
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<User> getWeiboUserInfoByName(String accessToken, String name) {
        return mWeiCoService.getWeiBoUserInfoByName(name);
    }

    @Override
    public Observable<User> getWeiboUserInfoByUid(String accessToken, long uid) {
        return mWeiCoService.getWeiBoUserInfoByUid(uid);
    }

    @Override
    public void saveWeiboUser(User user) {
    }

    @Override
    public Observable<User> followUser(String accessToken, String screen_name, long uid) {
        return mWeiCoService.followUser(screen_name, uid);
    }

    @Override
    public Observable<User> unfollowUser(String accessToken, String screen_name, long uid) {
        return mWeiCoService.unfollowUser(screen_name, uid);
    }

    @Override
    public Observable<FriendshipResponse> getFriends(String accessToken, long uid, int count, int trim_status, long cursor) {
        return mWeiCoService.getFriends(uid, count, trim_status, cursor);
    }

    @Override
    public Observable<FriendshipResponse> getFollowers(String accessToken, long uid, int count, int trim_status, long cursor) {
        return mWeiCoService.getFollowers(uid, count, trim_status, cursor);
    }

    @Override
    public Observable<List<User>> getSearchUser(String access_token, String q, int page, int count) {
        return mWeiCoService.searchUsers(q, count, page)
                .flatMap(new Func1<FriendshipResponse, Observable<List<User>>>() {
                    @Override
                    public Observable<List<User>> call(FriendshipResponse queryWeiboResponse) {
                        return Observable.just(queryWeiboResponse.getUsers());
                    }
                });
    }

}

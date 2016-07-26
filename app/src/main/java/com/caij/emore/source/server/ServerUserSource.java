package com.caij.emore.source.server;

import com.caij.emore.Key;
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

    @Override
    public Observable<List<User>> getSearchUser(String access_token, String q, int page, int count) {
        return mWeiCoService.searchUsers(access_token, Key.WEICO_APP_ID, Key.WEICO_APP_FROM, q, count, page)
                .flatMap(new Func1<FriendshipResponse, Observable<List<User>>>() {
                    @Override
                    public Observable<List<User>> call(FriendshipResponse queryWeiboResponse) {
                        return Observable.just(queryWeiboResponse.getUsers());
                    }
                });
    }

}

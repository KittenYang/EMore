package com.caij.emore.source.local;

import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.source.UserSource;


import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/6/2.
 */
public class LocalUserSource implements UserSource{

    @Override
    public Observable<User> getWeiboUserInfoByName(String accessToken, final String name) {
        return Observable.create(new Observable.OnSubscribe<User>() {

            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
//                    LocalUserDao dao = DBManager.getDaoSession().getLocalUserDao();
//                    List<LocalUser> localUsers = dao.queryBuilder().where(LocalUserDao.Properties.Name.eq(name)).list();
//                    if (localUsers.size() > 0 ) {
//                        LocalUser localUser = localUsers.get(0);
//                        subscriber.onNext(User.localUser2User(localUser));
//                    }else {
//                        subscriber.onNext(null);
//                    }
                }catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<User> getWeiboUserInfoByUid(String accessToken, final long uid) {
        return Observable.create(new Observable.OnSubscribe<User>() {

            @Override
            public void call(Subscriber<? super User> subscriber) {
                try {
//                    LocalUserDao dao = DBManager.getDaoSession().getLocalUserDao();
//                    LocalUser localUser = dao.load(uid);
//                    subscriber.onNext(User.localUser2User(localUser));
                }catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void saveWeiboUser(final User user) {
//        LocalUserDao dao = DBManager.getDaoSession().getLocalUserDao();
//        dao.insertOrReplace(User.user2LocalUser(user));
    }

    @Override
    public Observable<User> followUser(String accessToken, String screen_name) {
        return null;
    }

    @Override
    public Observable<User> unfollowUser(String accessToken, String screen_name) {
        return null;
    }

    @Override
    public Observable<FriendshipResponse> getFriends(String accessToken, long uid, int count, int trim_status, long cursor) {
        return null;
    }

    @Override
    public Observable<FriendshipResponse> getFollowers(String accessToken, long uid, int count, int trim_status, long cursor) {
        return null;
    }
}

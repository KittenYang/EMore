package com.caij.emore.source.local;

import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.source.UserSource;
import com.caij.emore.utils.db.DBManager;


import java.util.List;

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
                    UserDao dao = DBManager.getDaoSession().getUserDao();
                    List<User> localUsers = dao.queryBuilder().where(UserDao.Properties.Name.eq(name)).list();
                    if (localUsers.size() > 0 ) {
                        User localUser = localUsers.get(0);
                        subscriber.onNext(localUser);
                    }else {
                        subscriber.onNext(null);
                    }
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
                    UserDao dao = DBManager.getDaoSession().getUserDao();
                    User localUser = dao.load(uid);
                    subscriber.onNext(localUser);
                }catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void saveWeiboUser(final User user) {
        UserDao dao = DBManager.getDaoSession().getUserDao();
        dao.insertOrReplace(user);
    }

    @Override
    public Observable<User> followUser(String accessToken, String screen_name, long uid) {
        return null;
    }

    @Override
    public Observable<User> unfollowUser(String accessToken, String screen_name, long uid) {
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

    @Override
    public Observable<List<User>> getSearchUser(String access_token, String q, int page, int count) {
        return null;
    }
}

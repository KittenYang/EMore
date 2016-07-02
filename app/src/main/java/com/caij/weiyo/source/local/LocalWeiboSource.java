package com.caij.weiyo.source.local;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.UserWeiboResponse;
import com.caij.weiyo.database.bean.FriendWeibo;
import com.caij.weiyo.database.dao.DBManager;
import com.caij.weiyo.database.dao.FriendWeiboDao;
import com.caij.weiyo.source.WeiboSource;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/6/2.
 */
public class LocalWeiboSource implements WeiboSource {

    @Override
    public Observable<List<Weibo>> getFriendWeibo(String accessToken, long since_id, final long max_id,
                                                  final int count, final int page) {
        return Observable.create(new Observable.OnSubscribe<List<Weibo>>() {

            @Override
            public void call(Subscriber<? super List<Weibo>> subscriber) {
                try {
                    FriendWeiboDao dao = DBManager.getDaoSession().getFriendWeiboDao();
                    FriendWeibo maxFriendWeibo = dao.load(max_id);
                    long maxCreateTime = Long.MAX_VALUE;
                    if (maxFriendWeibo != null) {
                        maxCreateTime = maxFriendWeibo.getCreate_time();
                    }
                    List<FriendWeibo> friendWeibos = dao.queryBuilder()
                            .where(FriendWeiboDao.Properties.Create_time.lt(maxCreateTime))
                            .limit(count).offset(page - 1)
                            .orderDesc(FriendWeiboDao.Properties.Create_time).list();
                    List<Weibo> weibos = new ArrayList<Weibo>(friendWeibos.size());
                    for (FriendWeibo friendWeibo : friendWeibos) {
                        Weibo weibo = Weibo.friendWeibo2Weibo(friendWeibo);
                        weibos.add(weibo);
                    }
                    subscriber.onNext(weibos);
                }catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void saveFriendWeibo(String accessToken, final List<Weibo> weibos) {
        FriendWeiboDao dao = DBManager.getDaoSession().getFriendWeiboDao();
        List<FriendWeibo> friendWeibos = new ArrayList<>(weibos.size());
        for (Weibo weibo : weibos) {
            FriendWeibo friendWeibo = Weibo.weibo2FriendWeibo(weibo);
            friendWeibos.add(friendWeibo);
        }
        dao.insertOrReplaceInTx(friendWeibos);
    }

    @Override
    public Observable<UserWeiboResponse> getUseWeibo(String accessToken, String name,  int feature, long since_id, long max_id, int count, int page) {
        return null;
    }
}

package com.caij.emore.source.local;

import com.caij.emore.bean.Comment;
import com.caij.emore.bean.Weibo;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.FriendWeibo;
import com.caij.emore.database.dao.DBManager;
import com.caij.emore.database.dao.FriendWeiboDao;
import com.caij.emore.source.WeiboSource;

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


    @Override
    public Observable<Weibo> publishWeiboOfText(String token, String content) {
        return null;
    }

    @Override
    public Observable<Weibo> publishWeiboOfOneImage(String token, String content, String imagePath) {
        return null;
    }

    @Override
    public Observable<Weibo> publishWeiboOfMultiImage(String weiyoToken, String weicoToken,
                                                      String content, List<String> imagePaths) {
        return null;
    }

    @Override
    public Observable<Weibo> deleteWeibo(String accessToken, long id) {
        return null;
    }

    @Override
    public Observable<FavoritesCreateResponse> collectWeibo(String accessToken, long id) {
        return null;
    }

    @Override
    public Observable<FavoritesCreateResponse> uncollectWeibo(String accessToken, long id) {
        return null;
    }

    @Override
    public Observable<Comment> commentForWeibo(String accessToken, String status, long weiboId) {
        return null;
    }

    @Override
    public Observable<QueryRepostWeiboResponse> getRepostWeibos(String accessToken, long id, long since_id, long max_id, int count, int page) {
        return null;
    }

    @Override
    public Observable<List<Comment>> getCommentsByWeibo(String accessToken, long id, long since_id, long max_id, int count, int page) {
        return null;
    }

    @Override
    public Observable<Comment> deleteComment(String accessToken, long cid) {
        return null;
    }

    @Override
    public Observable<Comment> replyComment(String accessToken, String comment, long cid, long weiboId) {
        return null;
    }

    @Override
    public Observable<Weibo> repostWeibo(String accessToken, String status, long weiboId) {
        return null;
    }

    @Override
    public Observable<QueryWeiboResponse> getWeiboMentions(String accessToken, long since_id, long max_id, int count, int page) {
        return null;
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getCommentsMentions(String accessToken, long since_id, long max_id, int count, int page) {
        return null;
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getPublishComments(String accessToken, long since_id, long max_id, int count, int page) {
        return null;
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getAcceptComments(String accessToken, long since_id, long max_id, int count, int page) {
        return null;
    }
}

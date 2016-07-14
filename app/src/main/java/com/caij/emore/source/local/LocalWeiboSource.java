package com.caij.emore.source.local;

import android.database.Cursor;
import android.text.TextUtils;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.Geo;
import com.caij.emore.database.bean.LikeBean;
import com.caij.emore.database.bean.PicUrl;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Visible;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.database.dao.GeoDao;
import com.caij.emore.database.dao.LikeBeanDao;
import com.caij.emore.database.dao.PicUrlDao;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.database.dao.VisibleDao;
import com.caij.emore.database.dao.WeiboDao;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.db.DBManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/6/2.
 */
public class LocalWeiboSource implements WeiboSource {

    private final GeoDao geoDao;
    private final VisibleDao visibleDao;
    private final UserDao userDao;
    private final PicUrlDao picUrlDao;
    private final WeiboDao weiboDao;

    public LocalWeiboSource() {
        geoDao = DBManager.getDaoSession().getGeoDao();
        visibleDao = DBManager.getDaoSession().getVisibleDao();
        userDao = DBManager.getDaoSession().getUserDao();
        picUrlDao = DBManager.getDaoSession().getPicUrlDao();
        weiboDao = DBManager.getDaoSession().getWeiboDao();
    }

    @Override
    public Observable<List<Weibo>> getFriendWeibo(String accessToken, final long since_id, final long max_id,
                                                  final int count, final int page) {
        return Observable.create(new Observable.OnSubscribe<List<Weibo>>() {

            @Override
            public void call(Subscriber<? super List<Weibo>> subscriber) {
                try {
                    Cursor cursor = userDao.queryBuilder().where(UserDao.Properties.Following.eq(true))
                            .buildCursor().query();
                    List<Long> followerUserIds = new ArrayList<Long>();
                    try {
                        int idColumn = cursor.getColumnIndex(UserDao.Properties.Id.columnName);
                        while (cursor.moveToNext()) {
                            long uid = cursor.getLong(idColumn);
                            followerUserIds.add(uid);
                        }
                    }finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                    }

                    // TODO: 2016/7/13  bad
                    long selfUid  = Long.parseLong(UserPrefs.get().getEMoreToken().getUid());
                    followerUserIds.add(selfUid);

                    Weibo sinceWeibo = weiboDao.load(since_id);
                    String sinceCreateTime = "";
                    if (sinceWeibo != null) {
                        sinceCreateTime = sinceWeibo.getCreated_at();
                    }

                    Weibo maxFriendWeibo = weiboDao.load(max_id);
                    String maxCreateTime = "";
                    if (maxFriendWeibo != null) {
                        maxCreateTime = maxFriendWeibo.getCreated_at();
                    }

                    QueryBuilder<Weibo> queryBuilder = weiboDao.queryBuilder();
                    if(!TextUtils.isEmpty(sinceCreateTime) && !TextUtils.isEmpty(maxCreateTime)) {
                        queryBuilder.and(WeiboDao.Properties.Created_at.lt(maxCreateTime),
                                WeiboDao.Properties.Created_at.ge(sinceCreateTime));
                    }else if (!TextUtils.isEmpty(maxCreateTime)) {
                        queryBuilder.where(WeiboDao.Properties.Created_at.lt(maxCreateTime));
                    }else if (!TextUtils.isEmpty(sinceCreateTime)) {
                        queryBuilder.where(WeiboDao.Properties.Created_at.ge(sinceCreateTime));
                    }
                    if (followerUserIds.size() > 0) {
                        queryBuilder.where(WeiboDao.Properties.User_id.in(followerUserIds));
                    }
                    List<Weibo> friendWeibos = queryBuilder.limit(count).offset(page - 1)
                            .orderDesc(WeiboDao.Properties.Created_at).list();
                    for (Weibo weibo : friendWeibos) {
                        selectWeibo(weibo);
                    }

                    subscriber.onNext(friendWeibos);
                }catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void saveWeibos(String accessToken, final List<Weibo> weibos) {
        weiboDao.getDatabase().beginTransaction();
        for (Weibo weibo : weibos) {
            insertWeibo(weibo);
        }
        weiboDao.getDatabase().setTransactionSuccessful();
        weiboDao.getDatabase().endTransaction();
    }

    private void insertWeibo(Weibo weibo) {
        String weiboId  = String.valueOf(weibo.getId());
        Geo geo = weibo.getGeo();
        if (geo != null) {
            weibo.setGeo_id(weiboId);
            geo.setId(weiboId);
            geoDao.insertOrReplace(geo);
        }

        Visible visible = weibo.getVisible();
        if (visible != null) {
            weibo.setVisible_id(weiboId);
            visible.setId(weiboId);
            visibleDao.insertOrReplace(visible);
        }

        User user = weibo.getUser();
        weibo.setUser_id(user.getId());
        userDao.insertOrReplace(user);

        List<PicUrl> picUrls = weibo.getPic_urls();
        for (PicUrl picUrl : picUrls) {
            picUrl.setWeibo_id(weibo.getId());
            picUrl.setId(weiboId + picUrl.getThumbnail_pic());
            picUrlDao.insertOrReplace(picUrl);
        }

        if (weibo.getRetweeted_status() != null) {
            weibo.setRetweeted_status_id(weibo.getRetweeted_status().getId());
            insertWeibo(weibo.getRetweeted_status());
        }
        weiboDao.insertOrReplace(weibo);
    }

    private void selectWeibo(Weibo weibo) {
        if (!TextUtils.isEmpty(weibo.getGeo_id())) {
            weibo.setGeo(geoDao.load(weibo.getGeo_id()));
        }

        if (!TextUtils.isEmpty(weibo.getVisible_id())) {
            weibo.setVisible(visibleDao.load(weibo.getVisible_id()));
        }

        weibo.setUser(userDao.load(weibo.getUser_id()));

        List<PicUrl> picUrls = picUrlDao.queryBuilder().where(PicUrlDao.Properties.Weibo_id.eq(weibo.getId())).list();
        weibo.setPic_urls(picUrls);

        if (weibo.getRetweeted_status_id() != null && weibo.getRetweeted_status_id() != 0) {
            Weibo repostWeibo = weiboDao.load(weibo.getRetweeted_status_id());
            selectWeibo(repostWeibo);
            weibo.setRetweeted_status(repostWeibo);
        }
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
    public Observable<Weibo> deleteWeibo(String accessToken, final long id) {
        return Observable.create(new Observable.OnSubscribe<Weibo>() {
            @Override
            public void call(Subscriber<? super Weibo> subscriber) {
                try {
                    weiboDao.deleteByKey(id);
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<FavoritesCreateResponse> collectWeibo(String accessToken, final long id) {
        return Observable.create(new Observable.OnSubscribe<FavoritesCreateResponse>() {
            @Override
            public void call(Subscriber<? super FavoritesCreateResponse> subscriber) {
                Weibo weibo = weiboDao.load(id);
                selectWeibo(weibo);
                weibo.setFavorited(true);
                insertWeibo(weibo);
            }
        });
    }

    @Override
    public Observable<FavoritesCreateResponse> uncollectWeibo(String accessToken, final long id) {
        return Observable.create(new Observable.OnSubscribe<FavoritesCreateResponse>() {
            @Override
            public void call(Subscriber<? super FavoritesCreateResponse> subscriber) {
                Weibo weibo = weiboDao.load(id);
                selectWeibo(weibo);
                weibo.setFavorited(false);
                insertWeibo(weibo);
            }
        });
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

    @Override
    public Observable<Response> attitudesWeibo(Map<String, Object> paramMap, String attitude, final long weiboId) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    LikeBeanDao dao = DBManager.getDaoSession().getLikeBeanDao();
                    LikeBean likeBean = new LikeBean(weiboId, true);
                    dao.insertOrReplace(likeBean);
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Response> destoryAttitudesWeibo(Map<String, Object> paramMap, String attitude, final long weiboId) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                try {
                    LikeBeanDao dao = DBManager.getDaoSession().getLikeBeanDao();
                    LikeBean likeBean = new LikeBean(weiboId, false);
                    dao.insertOrReplace(likeBean);
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public boolean getAttitudes(long id) {
        LikeBeanDao dao = DBManager.getDaoSession().getLikeBeanDao();
        LikeBean likeBean = dao.load(id);
        return likeBean != null && likeBean.getIsLike();
    }

    @Override
    public Observable<Weibo> getWeiboById(String accessToken, final long id) {
       return Observable.create(new Observable.OnSubscribe<Weibo>() {
            @Override
            public void call(Subscriber<? super Weibo> subscriber) {
                try {
                    Weibo weibo = weiboDao.load(id);
                    selectWeibo(weibo);
                    subscriber.onNext(weibo);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}

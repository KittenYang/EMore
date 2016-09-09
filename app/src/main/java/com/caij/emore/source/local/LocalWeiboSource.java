package com.caij.emore.source.local;

import android.database.Cursor;
import android.text.TextUtils;

import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.bean.PageInfo;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.Geo;
import com.caij.emore.database.bean.LongText;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Visible;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.database.dao.UploadImageResponseDao;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.database.dao.WeiboDao;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.db.DBManager;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/6/2.
 */
public class LocalWeiboSource implements WeiboSource {

    private final UserDao userDao;
    private final WeiboDao weiboDao;

    public LocalWeiboSource() {
        userDao = DBManager.getDaoSession().getUserDao();
        weiboDao = DBManager.getDaoSession().getWeiboDao();
    }

    @Override
    public Observable<QueryWeiboResponse> getFriendWeibo(String accessToken, final long uid, final long since_id, final long max_id,
                                                         final int count, final int page) {
        return Observable.create(new Observable.OnSubscribe<QueryWeiboResponse>() {

            @Override
            public void call(Subscriber<? super QueryWeiboResponse> subscriber) {
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

                    //添加自己
                    followerUserIds.add(uid);

                    Weibo sinceWeibo = weiboDao.load(since_id);
                    Date sinceCreateTime = null;
                    if (sinceWeibo != null) {
                        sinceCreateTime = sinceWeibo.getCreated_at();
                    }

                    Weibo maxFriendWeibo = weiboDao.load(max_id);
                    Date maxCreateTime = null;
                    if (maxFriendWeibo != null) {
                        maxCreateTime = maxFriendWeibo.getCreated_at();
                    }

                    QueryBuilder<Weibo> queryBuilder = weiboDao.queryBuilder();
                    if(sinceCreateTime != null && maxCreateTime != null) {
                        queryBuilder.where(queryBuilder.and(WeiboDao.Properties.Created_at.lt(maxCreateTime),
                                WeiboDao.Properties.Created_at.gt(sinceCreateTime)));
                    }else if (maxCreateTime != null) {
                        queryBuilder.where(WeiboDao.Properties.Created_at.lt(maxCreateTime));
                    }else if (sinceCreateTime != null) {
                        queryBuilder.where(WeiboDao.Properties.Created_at.ge(sinceCreateTime));
                    }

                    if (followerUserIds.size() == 0) {
                        followerUserIds.add(0L);
                    }

                    queryBuilder.where(WeiboDao.Properties.User_id.in(followerUserIds));
                    List<Weibo> friendWeibos = queryBuilder.limit(count).offset(page - 1)
                            .orderDesc(WeiboDao.Properties.Created_at).list();
                    for (Weibo weibo : friendWeibos) {
                        selectWeibo(weibo);
                    }

                    QueryWeiboResponse queryWeiboResponse = new QueryWeiboResponse();
                    queryWeiboResponse.setStatuses(friendWeibos);
                    subscriber.onNext(queryWeiboResponse);
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
            weibo.setGeo_json_string(GsonUtils.toJson(geo));
        }

        Visible visible = weibo.getVisible();
        if (visible != null) {
            weibo.setVisible_json_string(GsonUtils.toJson(visible));
        }

        User user = weibo.getUser();
        if (user != null) {
            weibo.setUser_id(user.getId());
            userDao.insertOrReplace(user);
        }else {
            weibo.setUser_id(-1L);
        }

        List<String> picIds = weibo.getPic_ids();
        if (picIds != null && picIds.size() > 0) {
           weibo.setPic_ids_json_string(GsonUtils.toJson(picIds));
        }

        LinkedHashMap<String, ImageInfo> pic_infos = weibo.getPic_infos();
        if (pic_infos != null && pic_infos.size() > 0) {
            weibo.setPic_infos_json_string(GsonUtils.toJson(pic_infos));
        }

        LongText longText =  weibo.getLongText();
        if (weibo.getIsLongText() != null && weibo.getIsLongText() && longText != null
                && !TextUtils.isEmpty(longText.getContent())) {
            weibo.setLong_text_json_string(GsonUtils.toJson(longText));
        }

        List<ShortUrl> urlInfos = weibo.getUrl_struct();
        if (urlInfos != null && urlInfos.size() > 0) {
            weibo.setUrl_struct_json_string(GsonUtils.toJson(urlInfos));
        }

        PageInfo pageInfo = weibo.getPage_info();
        if (pageInfo != null) {
            weibo.setPage_info_json_string(GsonUtils.toJson(pageInfo));
        }

        if (weibo.getRetweeted_status() != null) {
            weibo.setRetweeted_status_id(weibo.getRetweeted_status().getId());
            insertWeibo(weibo.getRetweeted_status());
        }else {
            weibo.setRetweeted_status_id(-1L);
        }
        weiboDao.insertOrReplace(weibo);
    }

    private void selectWeibo(Weibo weibo) {
        if (weibo.getUser_id() != null && weibo.getUser_id() > 0) { //如果user 为null  表示微博被删除了
            weibo.setUser(userDao.load(weibo.getUser_id()));
            if (!TextUtils.isEmpty(weibo.getGeo_json_string())) {
                weibo.setGeo(GsonUtils.fromJson(weibo.getGeo_json_string(), Geo.class));
            }

            if (!TextUtils.isEmpty(weibo.getVisible_json_string())) {
                weibo.setVisible(GsonUtils.fromJson(weibo.getVisible_json_string(), Visible.class));
            }

            if (!TextUtils.isEmpty(weibo.getPic_ids_json_string())) {
                List<String> picIds = GsonUtils.fromJson(weibo.getPic_ids_json_string(),
                        new TypeToken<List<String>>() {
                        }.getType());
                weibo.setPic_ids(picIds);
            }

            if (!TextUtils.isEmpty(weibo.getPic_infos_json_string())) {
                LinkedHashMap<String, ImageInfo> pic_infos = GsonUtils.fromJson(weibo.getPic_infos_json_string(),
                        new TypeToken<LinkedHashMap<String, ImageInfo>>(){}.getType());
                weibo.setPic_infos(pic_infos);
            }

            if (weibo.getIsLongText() != null && weibo.getIsLongText()
                    && !TextUtils.isEmpty(weibo.getLong_text_json_string())) {
                LongText longText = GsonUtils.fromJson(weibo.getLong_text_json_string(), LongText.class);
                weibo.setLongText(longText);
            }

            if (!TextUtils.isEmpty(weibo.getUrl_struct_json_string())) {
                List<ShortUrl> shortUrls = GsonUtils.fromJson(weibo.getUrl_struct_json_string(),
                        new TypeToken<List<ShortUrl>>() {
                        }.getType());
                weibo.setUrl_struct(shortUrls);
            }

            if (!TextUtils.isEmpty(weibo.getPage_info_json_string())) {
                PageInfo pageInfo = GsonUtils.fromJson(weibo.getPage_info_json_string(), PageInfo.class);
                weibo.setPage_info(pageInfo);
            }

            if (weibo.getRetweeted_status_id() != null && weibo.getRetweeted_status_id() > 0) {
                Weibo repostWeibo = weiboDao.load(weibo.getRetweeted_status_id());
                selectWeibo(repostWeibo);
                weibo.setRetweeted_status(repostWeibo);
            }
        }
    }

    @Override
    public Observable<UserWeiboResponse> getUseWeibo(String accessToken, long uid, int feature, long since_id, long max_id, int count, int page) {
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
    public Observable<UploadImageResponse> uploadWeiboOfOneImage(String access_token, final String imagePath) throws IOException {
        return Observable.create(new Observable.OnSubscribe<UploadImageResponse>() {
            @Override
            public void call(Subscriber<? super UploadImageResponse> subscriber) {
                try {
                    UploadImageResponseDao dao = DBManager.getDaoSession().getUploadImageResponseDao();
                    List<UploadImageResponse> uploadImageResponses = dao.queryBuilder().
                            where(UploadImageResponseDao.Properties.ImagePath.eq(imagePath)).list();
                    UploadImageResponse uploadImageResponse = null;
                    if (uploadImageResponses != null && uploadImageResponses.size() > 0) {
                        uploadImageResponse = uploadImageResponses.get(0);
                    }
                    subscriber.onNext(uploadImageResponse);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                    LogUtil.d(LocalWeiboSource.this, "uploadWeiboOfOneImage %s", e.getMessage());
                }
            }
        });
    }

    @Override
    public Observable<Weibo> publishWeiboOfMultiImage(String accessToken, String status, String picIds) {
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
    public Observable<QueryWeiboCommentResponse> getCommentsByWeibo(String accessToken, long id, long since_id, long max_id, int count, int page) {
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
    public Observable<Attitude> attitudesWeibo(String token, String source, String attitude, final long weiboId) {
        return null;
    }

    @Override
    public Observable<Response> destoryAttitudesWeibo(String token, final String source, String attitude, final long weiboId) {
        Weibo weibo = weiboDao.load(weiboId);
        if (weibo != null) {
            weibo.setAttitudes_status(0);
            insertWeibo(weibo);
        }
        return null;
    }


    @Override
    public Observable<Weibo> getWeiboById(String token, String source, int isGetLongText, final long id) {
        return Observable.create(new Observable.OnSubscribe<Weibo>() {
            @Override
            public void call(Subscriber<? super Weibo> subscriber) {
                try {
                    Weibo weibo = weiboDao.load(id);
                    if (weibo != null) {
                        selectWeibo(weibo);
                    }
                    subscriber.onNext(weibo);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Weibo> getWeiboById(String token, final long id) {
        return Observable.create(new Observable.OnSubscribe<Weibo>() {
            @Override
            public void call(Subscriber<? super Weibo> subscriber) {
                try {
                    Weibo weibo = weiboDao.load(id);
                    if (weibo != null) {
                        selectWeibo(weibo);
                    }
                    subscriber.onNext(weibo);
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<QueryWeiboAttitudeResponse> getWeiboAttiyudes(String token, long id, int page, int count) {
        return null;
    }

    @Override
    public void saveWeibo(String mToken, Weibo weibo) {
        insertWeibo(weibo);
    }

    @Override
    public void saveUploadImageResponse(UploadImageResponse uploadImageResponse) {
        DBManager.getDaoSession().getUploadImageResponseDao().insertOrReplace(uploadImageResponse);
    }

    @Override
    public Observable<QueryWeiboAttitudeResponse> getToMeAttiyudes(String token, long maxId,
                                                                   long sinceId, String source, String from,
                                                                   int page, int count) {
        return null;
    }

    @Override
    public Observable<QueryWeiboResponse> getWeibosByIds(String access_token, String ids) {
        return null;
    }

    @Override
    public Observable<WeiboIds> getHotWeibosIds(String access_token, int page) {
        return null;
    }

    @Override
    public Observable<QueryWeiboResponse> getTopicsByKey(String access_token, String q, int page, int count) {
        return null;
    }

    @Override
    public Observable<QueryWeiboResponse> getSearchWeibo(String access_token, String q, int page, int count) {
        return null;
    }

}

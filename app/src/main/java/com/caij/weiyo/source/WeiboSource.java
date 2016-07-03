package com.caij.weiyo.source;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.FavoritesCreateResponse;
import com.caij.weiyo.bean.response.QueryRepostWeiboResponse;
import com.caij.weiyo.bean.response.UserWeiboResponse;

import java.util.List;

import retrofit2.http.Field;
import rx.Observable;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboSource {


    /**
     * @param accessToken
     * @param since_id
     * @param max_id
     * @param count
     * @param page 查询页，最低从1开始
     * @return
     */
    public Observable<List<Weibo>> getFriendWeibo(String accessToken, long since_id, long max_id,
                                                  int count, int page);

    public void saveFriendWeibo(String accessToken, List<Weibo> weibos);

    public Observable<UserWeiboResponse> getUseWeibo(String accessToken,  String name, int feature, long since_id, long max_id,
                                                     int count, int page);

    public Observable<Weibo> publishWeiboOfText(String token, String content);

    public Observable<Weibo> publishWeiboOfOneImage(String token, String content, String imagePath);

    public Observable<Weibo> publishWeiboOfMultiImage(String weiyoToken, String weicoToken, String content, List<String> imagePaths);

    Observable<Weibo> deleteWeibo(String accessToken, long id);

    Observable<FavoritesCreateResponse> collectWeibo(String accessToken, long id);

    Observable<FavoritesCreateResponse> uncollectWeibo(String accessToken, long id);

    public Observable<Comment> commentForWeibo(String accessToken, String status, long weiboId);

    public Observable<QueryRepostWeiboResponse> getRepostWeibos(String accessToken, long id,
                                                                long since_id, long max_id,
                                                                int count, int page);


    public Observable<List<Comment>> getCommentsByWeibo(String accessToken, long id,
                                                        long since_id, long max_id,
                                                        int count, int page);


    public Observable<Comment> deleteComment(String accessToken,long cid);

    public Observable<Comment> replyComment(String accessToken, String comment, long cid,long weiboId);

    public Observable<Weibo> repostWeibo(String accessToken, String status, long weiboId);
}

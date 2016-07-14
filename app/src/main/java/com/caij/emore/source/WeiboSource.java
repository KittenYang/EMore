package com.caij.emore.source;

import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.Weibo;

import java.util.List;
import java.util.Map;

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

    public void saveWeibos(String accessToken, List<Weibo> weibos);

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

    Observable<QueryWeiboResponse> getWeiboMentions(String accessToken, long since_id, long max_id,
                                                           int count, int page);

    Observable<QueryWeiboCommentResponse> getCommentsMentions(String accessToken, long since_id, long max_id,
                                                               int count, int page);

    Observable<QueryWeiboCommentResponse> getPublishComments(String accessToken, long since_id, long max_id,
                                                             int count, int page);

    Observable<QueryWeiboCommentResponse> getAcceptComments(String accessToken, long since_id, long max_id,
                                                            int count,  int page);

    public Observable<Response> attitudesWeibo(Map<String, Object> paramMap,
                                               String attitude, long weiboId);

    public Observable<Response> destoryAttitudesWeibo(Map<String, Object> paramMap,
                                      String attitude, long weiboId);

    boolean getAttitudes(long id);

    Observable<Weibo> getWeiboById(String accessToken, long id);
}
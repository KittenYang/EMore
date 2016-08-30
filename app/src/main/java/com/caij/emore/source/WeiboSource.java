package com.caij.emore.source;

import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboSource {

    public Observable<QueryWeiboResponse> getFriendWeibo(String accessToken, long uid, long since_id, long max_id,
                                                         int count, int page);

    public void saveWeibos(String accessToken, List<Weibo> weibos);

    public Observable<UserWeiboResponse> getUseWeibo(String accessToken, long uid, int feature, long since_id, long max_id,
                                                     int count, int page);

    public Observable<Weibo> publishWeiboOfText(String token, String content);

    public Observable<Weibo> publishWeiboOfOneImage(String token, String content, String imagePath);


    Observable<UploadImageResponse> uploadWeiboOfOneImage(String access_token, String imagePath) throws IOException;

    Observable<Weibo> publishWeiboOfMultiImage( String accessToken,String status, String picIds);

    Observable<Weibo> deleteWeibo(String accessToken, long id);

    Observable<FavoritesCreateResponse> collectWeibo(String accessToken, long id);

    Observable<FavoritesCreateResponse> uncollectWeibo(String accessToken, long id);

    public Observable<Comment> commentForWeibo(String accessToken, String status, long weiboId);

    public Observable<QueryRepostWeiboResponse> getRepostWeibos(String accessToken, long id,
                                                                long since_id, long max_id,
                                                                int count, int page);


    public Observable<QueryWeiboCommentResponse> getCommentsByWeibo(String accessToken, long id,
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

    public Observable<Attitude> attitudesWeibo(String token, String source,
                                               String attitude, long weiboId);

    public Observable<Response> destoryAttitudesWeibo(String token, String source,
                                      String attitude, long weiboId);

    public Observable<Weibo> getWeiboById(String token, String source, int isGetLongText, long id);

    public Observable<Weibo> getWeiboById(String token, long id);

    public Observable<QueryWeiboAttitudeResponse> getWeiboAttiyudes(String token, long id, int page, int count);

    void saveWeibo(String mToken, Weibo weibo);

    void saveUploadImageResponse(UploadImageResponse uploadImageResponse);

    public Observable<QueryWeiboAttitudeResponse> getToMeAttiyudes(String token, long maxId, long sinceId,
                                                       String source,  String from, int page, int count);

    Observable<QueryWeiboResponse> getWeibosByIds(String access_token, String ids);

    Observable<WeiboIds> getHotWeibosIds(String access_token, int page);

    Observable<QueryWeiboResponse> getTopicsByKey(String access_token, String q, int page, int count);

    Observable<QueryWeiboResponse> getSearchWeibo(String access_token, String q, int page, int count);

}

package com.caij.weiyo.source;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.response.QueryWeiboCommentResponse;


import java.util.List;

import retrofit2.Response;
import rx.Observable;

/**
 * Created by Caij on 2016/6/16.
 */
public interface CommentSource {

    public Observable<List<Comment>> getCommentsByWeibo(String accessToken, long id,
                                                        long since_id, long max_id,
                                                        int count, int page);

    public Observable<Comment> commentForWeibo(String accessToken, String comment, long weiboId);
}

package com.caij.emore.remote;

import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public interface CommentApi {

    public Observable<QueryWeiboCommentResponse> getCommentsByWeibo(long id, long since_id, long max_id, int count, int page);

    public Observable<Comment> commentToWeibo(String comment, long weiboId);

    public Observable<Comment> deleteComment(long cid);

    public Observable<Comment> replyComment(String comment, long cid, long weiboId);

    public Observable<QueryWeiboCommentResponse> getCommentsMentions(long since_id, long max_id, int count, int page);

    public Observable<QueryWeiboCommentResponse> getPublishComments(long since_id, long max_id, int count, int page);

    public Observable<QueryWeiboCommentResponse> getAcceptComments(long since_id, long max_id, int count, int page);

}

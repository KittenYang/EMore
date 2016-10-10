package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.remote.CommentApi;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public class CommentApiImp implements CommentApi {

    private WeiCoService mWeiCoService;

    public CommentApiImp() {
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getCommentsByWeibo(long id, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getCommentsByWeibo(id, since_id, max_id, count, page);
    }

    @Override
    public Observable<Comment> commentToWeibo(String comment, long weiboId) {
        return mWeiCoService.createCommentForWeibo(comment, weiboId);
    }

    @Override
    public Observable<Comment> deleteComment(long cid) {
        return mWeiCoService.deleteComment(cid);
    }

    @Override
    public Observable<Comment> replyComment(String comment, long cid, long weiboId) {
        return mWeiCoService.replyComment(cid, weiboId, comment);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getCommentsMentions(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getCommentsMentions(since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getPublishComments(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getPublishComments(since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getAcceptComments(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getAcceptComments(since_id, max_id, count, page);
    }
}

package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryStatusCommentResponse;
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
    public Observable<QueryStatusCommentResponse> getCommentsByStatusId(long id, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getCommentsByWeibo(id, since_id, max_id, count, page);
    }

    @Override
    public Observable<Comment> commentToStatus(String comment, long statusId) {
        return mWeiCoService.createCommentForWeibo(comment, statusId);
    }

    @Override
    public Observable<Comment> deleteComment(long cid) {
        return mWeiCoService.deleteComment(cid);
    }

    @Override
    public Observable<Comment> replyComment(String comment, long cid, long statusId) {
        return mWeiCoService.replyComment(cid, statusId, comment);
    }

    @Override
    public Observable<QueryStatusCommentResponse> getCommentsMentions(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getCommentsMentions(since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryStatusCommentResponse> getPublishComments(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getPublishComments(since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryStatusCommentResponse> getAcceptComments(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getAcceptComments(since_id, max_id, count, page);
    }
}

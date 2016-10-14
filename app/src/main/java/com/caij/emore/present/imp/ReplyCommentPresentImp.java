package com.caij.emore.present.imp;

import com.caij.emore.bean.Comment;
import com.caij.emore.present.ReplyCommentWeiboPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.CommentStatusView;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.api.ex.DefaultTransformer;

import rx.Subscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class ReplyCommentPresentImp extends AbsBasePresent implements ReplyCommentWeiboPresent {

    private CommentApi mCommentApi;
    private CommentStatusView mCommentStatusView;

    private long mStatusId;
    private long mCid;

    public ReplyCommentPresentImp(long statusId, long cid,
                                  CommentApi commentApi, CommentStatusView commentStatusView) {
        mCommentApi = commentApi;
        mCid = cid;
        mStatusId = statusId;
        mCommentStatusView = commentStatusView;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void toReplyComment(String comment) {
        mCommentStatusView.showDialogLoading(true);
        Subscription subscription = mCommentApi.replyComment(comment, mCid, mStatusId)
                .compose(new DefaultTransformer<Comment>())
                .subscribe(new ResponseSubscriber<Comment>(mCommentStatusView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mCommentStatusView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Comment response) {
                        mCommentStatusView.showDialogLoading(false);
                        mCommentStatusView.onCommentSuccess(response);
                    }
                });
        addSubscription(subscription);
    }
}

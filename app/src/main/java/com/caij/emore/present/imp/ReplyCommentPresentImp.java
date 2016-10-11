package com.caij.emore.present.imp;

import com.caij.emore.bean.Comment;
import com.caij.emore.present.ReplyCommentWeiboPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.CommentWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;

import rx.Subscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class ReplyCommentPresentImp extends AbsBasePresent implements ReplyCommentWeiboPresent {

    private CommentApi mCommentApi;
    private CommentWeiboView mCommentWeiboView;

    private long mWeiboId;
    private long mCid;

    public ReplyCommentPresentImp(long weiboId, long cid,
                                  CommentApi commentApi, CommentWeiboView commentWeiboView) {
        mCommentApi = commentApi;
        mCid = cid;
        mWeiboId = weiboId;
        mCommentWeiboView = commentWeiboView;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void toReplyComment(String comment) {
        mCommentWeiboView.showDialogLoading(true);
        Subscription subscription = mCommentApi.replyComment(comment, mCid, mWeiboId)
                .compose(new DefaultTransformer<Comment>())
                .subscribe(new DefaultResponseSubscriber<Comment>(mCommentWeiboView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mCommentWeiboView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Comment response) {
                        mCommentWeiboView.showDialogLoading(false);
                        mCommentWeiboView.onCommentSuccess(response);
                    }
                });
        addSubscription(subscription);
    }
}

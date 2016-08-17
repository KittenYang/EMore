package com.caij.emore.present.imp;

import com.caij.emore.bean.Comment;
import com.caij.emore.present.ReplyCommentWeiboPresent;
import com.caij.emore.ui.view.CommentWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultTransformer;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class ReplyCommentPresentImp implements ReplyCommentWeiboPresent {

    private CompositeSubscription mLoginCompositeSubscription;
    private WeiboSource mCommentSource;
    private CommentWeiboView mCommentWeiboView;

    private String mToken;
    private long mWeiboId;
    private long mCid;

    public ReplyCommentPresentImp(String token, long weiboId, long cid,
                                  WeiboSource commentSource, CommentWeiboView commentWeiboView) {
        mCommentSource = commentSource;
        mToken = token;
        mCid = cid;
        mWeiboId = weiboId;
        mCommentWeiboView = commentWeiboView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void toReplyComment(String comment) {
        mCommentWeiboView.showDialogLoading(true);
        Subscription subscription = mCommentSource.replyComment(mToken, comment, mCid, mWeiboId)
                .compose(new DefaultTransformer<Comment>())
                .subscribe(new DefaultResponseSubscriber<Comment>(mCommentWeiboView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mCommentWeiboView.showDialogLoading(false);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Comment response) {
                        mCommentWeiboView.showDialogLoading(false);
                        mCommentWeiboView.onCommentSuccess(response);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }
}

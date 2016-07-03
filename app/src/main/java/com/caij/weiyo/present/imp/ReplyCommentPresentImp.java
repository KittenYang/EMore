package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.present.ReplyCommentWeiboPresent;
import com.caij.weiyo.present.view.CommentWeiboView;
import com.caij.weiyo.source.DefaultResponseSubscriber;
import com.caij.weiyo.source.WeiboSource;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
        mCommentWeiboView.showLoading(true);
        Subscription subscription = mCommentSource.replyComment(mToken, comment, mCid, mWeiboId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<Comment>(mCommentWeiboView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mCommentWeiboView.showLoading(false);
                        mCommentWeiboView.onComnLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Comment response) {
                        mCommentWeiboView.showLoading(false);
                        mCommentWeiboView.onCommentSuccess(response);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }
}

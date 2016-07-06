package com.caij.weiyo.present.imp;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.present.CommentWeiboPresent;
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
public class CommentWeiboPresentImp implements CommentWeiboPresent {

    private CompositeSubscription mLoginCompositeSubscription;
    private WeiboSource mCommentSource;
    private CommentWeiboView mCommentWeiboView;

    private String mToken;
    private long mWeiboId;

    public CommentWeiboPresentImp(String token, long weiboId,
                                  WeiboSource commentSource, CommentWeiboView commentWeiboView) {
        mCommentSource = commentSource;
        mToken = token;
        mWeiboId = weiboId;
        mCommentWeiboView = commentWeiboView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void toCommentWeibo(String comment) {
        mCommentWeiboView.showDialogLoading(true, R.string.commenting);
        Subscription subscription = mCommentSource.commentForWeibo(mToken, comment, mWeiboId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<Comment>(mCommentWeiboView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mCommentWeiboView.showDialogLoading(false, R.string.commenting);
                        mCommentWeiboView.onDefaultLoadError();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Comment response) {
                        mCommentWeiboView.showDialogLoading(false, R.string.commenting);
                        mCommentWeiboView.onCommentSuccess(response);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

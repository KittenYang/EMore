package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.present.CommentWeiboPresent;
import com.caij.weiyo.present.view.CommentWeiboView;
import com.caij.weiyo.source.CommentSource;
import com.caij.weiyo.source.DefaultResponseSubscriber;

import retrofit2.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class CommentWeiboPresentImp implements CommentWeiboPresent {

    private CompositeSubscription mLoginCompositeSubscription;
    private CommentSource mCommentSource;
    private CommentWeiboView mCommentWeiboView;

    private String mToken;
    private long mWeiboId;

    public CommentWeiboPresentImp(String token, long weiboId,
                                  CommentSource commentSource, CommentWeiboView commentWeiboView) {
        mCommentSource = commentSource;
        mToken = token;
        mWeiboId = weiboId;
        mCommentWeiboView = commentWeiboView;
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void toCommentWeibo(String comment) {
        mCommentWeiboView.showLoading(true);
        Subscription subscription = mCommentSource.commentForWeibo(mToken, comment, mWeiboId)
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


    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }
}

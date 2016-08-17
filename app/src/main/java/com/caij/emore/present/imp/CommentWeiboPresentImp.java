package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.R;
import com.caij.emore.bean.Comment;
import com.caij.emore.present.CommentWeiboPresent;
import com.caij.emore.ui.view.CommentWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultTransformer;

import rx.Subscription;
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
                .compose(new DefaultTransformer<Comment>())
                .subscribe(new DefaultResponseSubscriber<Comment>(mCommentWeiboView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mCommentWeiboView.showDialogLoading(false, R.string.commenting);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(Comment response) {
                        mCommentWeiboView.showDialogLoading(false, R.string.commenting);
                        mCommentWeiboView.onCommentSuccess(response);
                        RxBus.getDefault().post(Event.EVENT_COMMENT_WEIBO_SUCCESS, response);
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

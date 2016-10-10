package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.R;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.event.CommentEvent;
import com.caij.emore.present.CommentWeiboPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.CommentWeiboView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultTransformer;

import rx.Subscription;

/**
 * Created by Caij on 2016/6/27.
 */
public class CommentWeiboPresentImp extends AbsBasePresent implements CommentWeiboPresent {

    private CommentWeiboView mCommentWeiboView;

    private String mToken;
    private long mWeiboId;

    private CommentApi mCommentApi;

    public CommentWeiboPresentImp(String token, long weiboId,
                                  CommentApi commentApi, CommentWeiboView commentWeiboView) {
        super();
        mCommentApi = commentApi;
        mToken = token;
        mWeiboId = weiboId;
        mCommentWeiboView = commentWeiboView;
    }

    @Override
    public void toCommentWeibo(String comment) {
        mCommentWeiboView.showDialogLoading(true, R.string.commenting);
        Subscription subscription = mCommentApi.commentToWeibo(comment, mWeiboId)
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

                        CommentEvent commentEvent = new CommentEvent(EventTag.EVENT_COMMENT_WEIBO_SUCCESS, response, mWeiboId);
                        RxBus.getDefault().post(commentEvent.type, commentEvent);
                    }
                });
        addSubscription(subscription);
    }


    @Override
    public void onCreate() {

    }

}

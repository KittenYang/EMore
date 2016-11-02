package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.R;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.event.CommentEvent;
import com.caij.emore.present.CommentStatusPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.CommentStatusView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.DefaultTransformer;

import rx.Subscription;
import rx.functions.Action0;

/**
 * Created by Caij on 2016/6/27.
 */
public class CommentStatusPresentImp extends AbsBasePresent implements CommentStatusPresent {

    private CommentStatusView mCommentStatusView;

    private long mStatusId;

    private CommentApi mCommentApi;

    public CommentStatusPresentImp(long statusId,
                                   CommentApi commentApi, CommentStatusView commentStatusView) {
        super();
        mCommentApi = commentApi;
        mStatusId = statusId;
        mCommentStatusView = commentStatusView;
    }

    @Override
    public void toCommentStatus(String comment) {
        Subscription subscription = mCommentApi.commentToStatus(comment, mStatusId)
                .compose(new DefaultTransformer<Comment>())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCommentStatusView.showDialogLoading(true, R.string.commenting);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mCommentStatusView.showDialogLoading(false, R.string.commenting);
                    }
                })
                .subscribe(new ResponseSubscriber<Comment>(mCommentStatusView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Comment response) {
                        mCommentStatusView.onCommentSuccess(response);

                        CommentEvent commentEvent = new CommentEvent(EventTag.EVENT_COMMENT_WEIBO_SUCCESS, response, mStatusId);
                        RxBus.getDefault().post(commentEvent.type, commentEvent);
                    }
                });
        addSubscription(subscription);
    }


    @Override
    public void onCreate() {

    }

}

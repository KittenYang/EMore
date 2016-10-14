package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryStatusCommentResponse;
import com.caij.emore.present.PublishCommentsPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.MyPublishCommentsView;
import com.caij.emore.api.ex.DefaultTransformer;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class PublishCommentsPresentImp extends AbsBasePresent implements PublishCommentsPresent {

    private static final int COUNT = 20;

    private CommentApi mCommentApi;
    private MyPublishCommentsView mMentionView;
    private List<Comment> mComments;

    public PublishCommentsPresentImp(CommentApi commentApi, MyPublishCommentsView mentionView) {
        mCommentApi = commentApi;
        mMentionView = mentionView;
        mComments = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void deleteComment(final Comment comment, final int position) {
       Subscription subscription = mCommentApi.deleteComment(comment.getId())
                .compose(new DefaultTransformer<Comment>())
               .doOnSubscribe(new Action0() {
                   @Override
                   public void call() {
                       mMentionView.showDialogLoading(true, R.string.deleting);
                   }
               })
               .doOnTerminate(new Action0() {
                   @Override
                   public void call() {
                       mMentionView.showDialogLoading(false, R.string.deleting);
                   }
               })
                .subscribe(new ResponseSubscriber<Comment>(mMentionView) {

                    @Override
                    protected void onFail(Throwable e) {
                    }

                    @Override
                    public void onNext(Comment c) {
                        mMentionView.onDeleteCommentSuccess(comment, position);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void refresh() {
        Subscription su =  createCommentsObservable(0, true)
                .subscribe(new ResponseSubscriber<List<Comment>>(mMentionView) {

                    @Override
                    protected void onFail(Throwable e) {
                        mMentionView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mMentionView.setEntities(mComments);

                        mMentionView.onRefreshComplete();
                        mMentionView.onLoadComplete(comments.size() > COUNT - 1);
                    }
                });
        addSubscription(su);
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mComments != null && mComments.size() > 1) {
            maxId = mComments.get(mComments.size() - 1).getId();
        }
        Subscription su = createCommentsObservable(maxId, false)
                .subscribe(new ResponseSubscriber<List<Comment>>(mMentionView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mMentionView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mMentionView.notifyItemRangeInserted(mComments, mComments.size() - comments.size(),
                                comments.size());

                        mMentionView.onLoadComplete(comments.size() > COUNT - 1);
                    }
                });
        addSubscription(su);
    }

    private Observable<List<Comment>> createCommentsObservable(long maxId, final boolean isRefresh) {
        return mCommentApi.getPublishComments(0, maxId, COUNT, 1)
                .compose(new ErrorCheckerTransformer<QueryStatusCommentResponse>())
                .flatMap(new Func1<QueryStatusCommentResponse, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(QueryStatusCommentResponse response) {
                        return Observable.from(response.getComments());
                    }
                })
                .filter(new Func1<Comment, Boolean>() {
                    @Override
                    public Boolean call(Comment comment) {
                        return !mComments.contains(comment) || isRefresh;
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<Comment>>());
    }
}

package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.present.PublishCommentsPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.MyPublishComentsView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class PublishCommentsPresentImp extends AbsBasePresent implements PublishCommentsPresent {

    private static final int COUNT = 20;

    private String mToken;
    private CommentApi mCommentApi;
    private MyPublishComentsView mMentionView;
    private List<Comment> mComments;

    public PublishCommentsPresentImp(String token, CommentApi commentApi, MyPublishComentsView mentionView) {
        mToken = token;
        mCommentApi = commentApi;
        mMentionView = mentionView;
        mComments = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void deleteComment(final Comment comment, final int position) {
        mMentionView.showDialogLoading(true, R.string.deleting);
       Subscription subscription = mCommentApi.deleteComment(comment.getId())
                .compose(new DefaultTransformer<Comment>())
                .subscribe(new DefaultResponseSubscriber<Comment>(mMentionView) {
                    @Override
                    public void onCompleted() {
                        mMentionView.showDialogLoading(false, R.string.deleting);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mMentionView.showDialogLoading(false, R.string.deleting);
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
                .subscribe(new DefaultResponseSubscriber<List<Comment>>(mMentionView) {
                    @Override
                    public void onCompleted() {

                    }

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
                .subscribe(new DefaultResponseSubscriber<List<Comment>>(mMentionView) {
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
                .compose(new ErrorCheckerTransformer<QueryWeiboCommentResponse>())
                .flatMap(new Func1<QueryWeiboCommentResponse, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(QueryWeiboCommentResponse response) {
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

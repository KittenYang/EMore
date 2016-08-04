package com.caij.emore.present.imp;

import com.caij.emore.R;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.present.PublishCommentsPresent;
import com.caij.emore.present.view.MyPublishComentsView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/4.
 */
public class PublishCommentsPresentImp implements PublishCommentsPresent {

    private static final int COUNT = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private WeiboSource mWeiboSource;
    private MyPublishComentsView mMentionView;
    private List<Comment> mComments;

    public PublishCommentsPresentImp(String token, WeiboSource weiboSource, MyPublishComentsView mentionView) {
        mToken = token;
        mWeiboSource = weiboSource;
        mMentionView = mentionView;
        mComments = new ArrayList<>();
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
    public void deleteComment(final Comment comment, final int position) {
        mMentionView.showDialogLoading(true, R.string.deleting);
       Subscription subscription = mWeiboSource.deleteComment(mToken, comment.getId())
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
        mLoginCompositeSubscription.add(subscription);
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
        mLoginCompositeSubscription.add(su);
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
                        mMentionView.setEntities(mComments);

                        mMentionView.onLoadComplete(comments.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
    }

    private Observable<List<Comment>> createCommentsObservable(long maxId, final boolean isRefresh) {
        return mWeiboSource.getPublishComments(mToken, 0, maxId, COUNT, 1)
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

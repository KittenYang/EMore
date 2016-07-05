package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.response.QueryWeiboCommentResponse;
import com.caij.weiyo.present.PublishCommentsPresent;
import com.caij.weiyo.present.view.MyPublishComentsView;
import com.caij.weiyo.source.WeiboSource;

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
    public void onUserFirstVisible() {
        mMentionView.toRefresh();
    }

    @Override
    public void onRefresh() {
        Subscription su =  mWeiboSource.getPublishComments(mToken, 0 ,0, COUNT, 1)
                .flatMap(new Func1<QueryWeiboCommentResponse, Observable<List<Comment>>>() {
                    @Override
                    public Observable<List<Comment>> call(QueryWeiboCommentResponse response) {
                        return Observable.just(response.getComments());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Comment>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mMentionView.onComnLoadError();
                        mMentionView.onRefreshComplite();
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mMentionView.setEntities(mComments);

                        mMentionView.onRefreshComplite();
                        mMentionView.onLoadComplite(comments.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
    }

    @Override
    public void onLoadMore() {
        long maxId = 0;
        if (mComments != null && mComments.size() > 1) {
            maxId = mComments.get(mComments.size() - 1).getId();
        }
        Subscription su = mWeiboSource.getPublishComments(mToken, 0, maxId, COUNT, 1)
                .flatMap(new Func1<QueryWeiboCommentResponse, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(QueryWeiboCommentResponse response) {
                        return Observable.from(response.getComments());
                    }
                })
                .filter(new Func1<Comment, Boolean>() {
                    @Override
                    public Boolean call(Comment comment) {
                        return !mComments.contains(comment);
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Comment>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mMentionView.onComnLoadError();
                        mMentionView.onLoadComplite(true);
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mMentionView.setEntities(mComments);

                        mMentionView.onLoadComplite(comments.size() > COUNT - 1);
                    }
                });
        mLoginCompositeSubscription.add(su);
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
        mMentionView.showDialogLoading(true);
       Subscription subscription = mWeiboSource.deleteComment(mToken, comment.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Comment>() {
                    @Override
                    public void onCompleted() {
                        mMentionView.showDialogLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMentionView.onComnLoadError();
                        mMentionView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Comment c) {
                        mMentionView.onDeleteCommentSuccess(comment, position);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }
}

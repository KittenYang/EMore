package com.caij.emore.present.imp;

import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.view.RefreshListView;
import com.caij.emore.source.WeiboSource;

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
public class CommentMentionPresentImp implements RefreshListPresent {

    private static final int COUNT = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private WeiboSource mWeiboSource;
    private RefreshListView<Comment> mMentionView;
    private List<Comment> mComments;

    public CommentMentionPresentImp(String token, WeiboSource weiboSource, RefreshListView<Comment> mentionView) {
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
    public void refresh() {
        Subscription su =  mWeiboSource.getCommentsMentions(mToken, 0 ,0, COUNT, 1)
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
                        mMentionView.onDefaultLoadError();
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
        Subscription su = mWeiboSource.getCommentsMentions(mToken, 0, maxId, COUNT, 1)
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
                        mMentionView.onDefaultLoadError();
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
}

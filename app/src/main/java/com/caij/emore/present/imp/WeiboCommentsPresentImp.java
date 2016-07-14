package com.caij.emore.present.imp;

import com.caij.emore.bean.Comment;
import com.caij.emore.present.WeiboCommentsPresent;
import com.caij.emore.present.view.WeiboCommentsView;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.SpannableStringUtil;

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
 * Created by Caij on 2016/6/16.
 */
public class WeiboCommentsPresentImp implements WeiboCommentsPresent {

    private static final int PAGE_COUNET = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mWeiboId;
    WeiboSource mServerCommentSource;
    WeiboCommentsView mWeiboCommentsView;
    List<Comment> mComments;

    public WeiboCommentsPresentImp(String token, long weiboId,
                                   WeiboSource serverCommentSource,
                                   WeiboCommentsView weiboCommentsView) {
        mToken = token;
        mServerCommentSource = serverCommentSource;
        mWeiboCommentsView = weiboCommentsView;
        mWeiboId = weiboId;
        mLoginCompositeSubscription = new CompositeSubscription();
        mComments = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
        Subscription subscription = mServerCommentSource.getCommentsByWeibo(mToken, mWeiboId, 0, 0, PAGE_COUNET, 1)
                .flatMap(new Func1<List<Comment>, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(List<Comment> comments) {
                        return Observable.from(comments);
                    }
                })
                .map(new Func1<Comment, Comment>() {
                    @Override
                    public Comment call(Comment comment) {
                        SpannableStringUtil.paraeSpannable(comment, mWeiboCommentsView.getContent().getApplicationContext());
                        return comment;
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
                        mWeiboCommentsView.onDefaultLoadError();
                        mWeiboCommentsView.onLoadComplete(false);
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mWeiboCommentsView.setEntities(mComments);
                        if (comments.size() == 0) {
                            mWeiboCommentsView.onEmpty();
                        }else {
                            mWeiboCommentsView.onLoadComplete(comments.size() >= 10);
                        }
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mComments.size() > 0) {
            maxId = mComments.get(mComments.size() - 1).getId();
        }
        Subscription subscription = mServerCommentSource.getCommentsByWeibo(mToken, mWeiboId, 0, maxId, PAGE_COUNET, 1)
                .flatMap(new Func1<List<Comment>, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(List<Comment> comments) {
                        return Observable.from(comments);
                    }
                })
                .filter(new Func1<Comment, Boolean>() {
                    @Override
                    public Boolean call(Comment comment) {
                        return !mComments.contains(comment);
                    }
                })
                .map(new Func1<Comment, Comment>() {
                    @Override
                    public Comment call(Comment comment) {
                        SpannableStringUtil.paraeSpannable(comment, mWeiboCommentsView.getContent().getApplicationContext());
                        return comment;
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
                        mWeiboCommentsView.onDefaultLoadError();
                        mWeiboCommentsView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mWeiboCommentsView.setEntities(mComments);
                        mWeiboCommentsView.onLoadComplete(comments.size() > 15);
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void deleteComment(final Comment comment) {
        mWeiboCommentsView.showDialogLoading(true);
        Subscription subscription = mServerCommentSource.deleteComment(mToken, comment.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Comment>() {
                    @Override
                    public void onCompleted() {
                        mWeiboCommentsView.showDialogLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mWeiboCommentsView.onDefaultLoadError();
                        mWeiboCommentsView.showDialogLoading(false);
                    }

                    @Override
                    public void onNext(Comment c) {
                        mWeiboCommentsView.onDeleteSuccess(comment);
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

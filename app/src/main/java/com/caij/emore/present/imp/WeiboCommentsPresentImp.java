package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboCommentsPresent;
import com.caij.emore.present.view.WeiboCommentsView;
import com.caij.emore.source.UrlSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.local.LocalUrlSource;
import com.caij.emore.source.server.ServerUrlSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    WeiboSource mLocalWeiboSource;
    WeiboCommentsView mWeiboCommentsView;
    List<Comment> mComments;
    protected UrlSource mLocalUrlSource;
    protected UrlSource mServerUrlSource;
    private Observable<Comment> mCommentObservable;
    private Observable<List<Comment>> mWeiboRefreshObservable;

    public WeiboCommentsPresentImp(String token, long weiboId,
                                   WeiboSource serverCommentSource,
                                   WeiboSource localWeiboSource,
                                   WeiboCommentsView weiboCommentsView) {
        mToken = token;
        mServerCommentSource = serverCommentSource;
        mLocalWeiboSource = localWeiboSource;
        mWeiboCommentsView = weiboCommentsView;
        mWeiboId = weiboId;
        mLocalUrlSource = new LocalUrlSource();
        mServerUrlSource = new ServerUrlSource();
        mLoginCompositeSubscription = new CompositeSubscription();
        mComments = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void initEventListener() {
        mCommentObservable = RxBus.getDefault().register(Event.EVENT_COMMENT_WEIBO_SUCCESS);
        mCommentObservable
                .filter(new Func1<Comment, Boolean>() {
                    @Override
                    public Boolean call(Comment comment) {
                        return comment.getStatus().getId().longValue() == mWeiboId;
                    }
                })
                .doOnNext(new Action1<Comment>() {
                    @Override
                    public void call(Comment comment) {
                        SpannableStringUtil.paraeSpannable(comment);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Comment>() {
                    @Override
                    public void call(Comment comment) {
                        mComments.add(0, comment);
                        mWeiboCommentsView.onCommentSuccess(mComments);
                    }
                });


        mWeiboRefreshObservable = RxBus.getDefault().register(Event.EVENT_WEIBO_COMMENTS_REFRESH_COMPLETE);
        mWeiboRefreshObservable.filter(new Func1<List<Comment>, Boolean>() {
                @Override
                public Boolean call(List<Comment> comments) {
                    return comments != null && comments.size() > 0 && comments.get(0).getStatus().getId() == mWeiboId;
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<Comment>>() {
                @Override
                public void call(List<Comment> comments) {
                    LogUtil.d(WeiboCommentsPresentImp.this, "accept refresh event");

                    addRefreshDate(comments);
                }
            });
    }

    @Override
    public void userFirstVisible() {
        initEventListener();

        Subscription subscription =  createObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<Comment>>(mWeiboCommentsView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        if (mComments.size() == 0) {
                            mWeiboCommentsView.showErrorView();
                        }
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        addRefreshDate(comments);
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    private void addRefreshDate(List<Comment> comments) {
        mComments.clear();
        mComments.addAll(comments);
        mWeiboCommentsView.setEntities(mComments);

        mWeiboCommentsView.onLoadComplete(comments.size() >= PAGE_COUNET - 5);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mComments.size() > 0) {
            maxId = mComments.get(mComments.size() - 1).getId();
        }
        Subscription subscription = createObservable(maxId, false)
                .subscribe(new DefaultResponseSubscriber<List<Comment>>(mWeiboCommentsView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mWeiboCommentsView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mWeiboCommentsView.setEntities(mComments);
                        mWeiboCommentsView.onLoadComplete(comments.size() > PAGE_COUNET - 5);
                    }
                });

        mLoginCompositeSubscription.add(subscription);
    }

    private Observable<List<Comment>> createObservable(long maxId, final boolean isRefresh) {
        return mServerCommentSource.getCommentsByWeibo(mToken, mWeiboId, 0, maxId, PAGE_COUNET, 1)
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
                        return isRefresh || !mComments.contains(comment);
                    }
                })
                .toList()
                .doOnNext(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        if (comments.size() > 0) {
                            Weibo weibo = comments.get(0).getStatus();
                            RxBus.getDefault().post(Event.EVENT_WEIBO_UPDATE, weibo);
                            mLocalWeiboSource.saveWeibo(mToken, weibo);
                        }
                        for (Comment comment : comments) {
                            SpannableStringUtil.paraeSpannable(comment);
                        }
                    }
                })
                .compose(new SchedulerTransformer<List<Comment>>());
    }

    @Override
    public void deleteComment(final Comment comment) {
        mWeiboCommentsView.showDialogLoading(true);
        Subscription subscription = mServerCommentSource.deleteComment(mToken, comment.getId())
                .compose(new DefaultTransformer<Comment>())
                .subscribe(new DefaultResponseSubscriber<Comment>(mWeiboCommentsView) {
                    @Override
                    public void onCompleted() {
                        mWeiboCommentsView.showDialogLoading(false);
                    }

                    @Override
                    protected void onFail(Throwable e) {
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
        RxBus.getDefault().unregister(Event.EVENT_COMMENT_WEIBO_SUCCESS, mCommentObservable);
        RxBus.getDefault().unregister(Event.EVENT_WEIBO_COMMENTS_REFRESH_COMPLETE, mWeiboRefreshObservable);
    }

}

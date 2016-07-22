package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryUrlResponse;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.present.WeiboCommentsPresent;
import com.caij.emore.present.view.WeiboCommentsView;
import com.caij.emore.source.UrlSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.source.local.LocalUrlSource;
import com.caij.emore.source.server.ServerUrlSource;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxbus.RxBus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
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
    WeiboCommentsView mWeiboCommentsView;
    List<Comment> mComments;
    protected UrlSource mLocalUrlSource;
    protected UrlSource mServerUrlSource;
    private Observable<Comment> mCommentObservable;

    public WeiboCommentsPresentImp(String token, long weiboId,
                                   WeiboSource serverCommentSource,
                                   WeiboCommentsView weiboCommentsView) {
        mToken = token;
        mServerCommentSource = serverCommentSource;
        mWeiboCommentsView = weiboCommentsView;
        mWeiboId = weiboId;
        mLocalUrlSource = new LocalUrlSource();
        mServerUrlSource = new ServerUrlSource();
        mLoginCompositeSubscription = new CompositeSubscription();
        mComments = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
        Subscription subscription =  createObservable(0, true)
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
        Subscription subscription = createObservable(maxId, false)
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

    private Observable<List<Comment>> createObservable(long maxId, final boolean isRefresh) {
        return mServerCommentSource.getCommentsByWeibo(mToken, mWeiboId, 0, maxId, PAGE_COUNET, 1)
                .flatMap(new Func1<List<Comment>, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(List<Comment> comments) {
                        return Observable.from(comments);
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
                        List<String> shortUrls  = SpannableStringUtil.getCommentTextHttpUrl(comments);
                        Map<String, UrlInfo> shortLongLinkMap = UrlUtil.getShortUrlInfos(shortUrls, mServerUrlSource, mLocalUrlSource, mToken);
                        for (Comment comment : comments) {
                            SpannableStringUtil.paraeSpannable(comment, shortLongLinkMap);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
        mCommentObservable = RxBus.get().register(Key.EVENT_COMMENT_WEIBO_SUCCESS);
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
                        List<String> shortUrls  = SpannableStringUtil.getCommentTextHttpUrl(comment, null);
                        Map<String, UrlInfo> shortLongLinkMap = UrlUtil.getShortUrlInfos(shortUrls, mServerUrlSource, mLocalUrlSource, mToken);
                        SpannableStringUtil.paraeSpannable(comment, shortLongLinkMap);
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
    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
        RxBus.get().unregister(Key.EVENT_COMMENT_WEIBO_SUCCESS, mCommentObservable);
    }


}

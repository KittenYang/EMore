package com.caij.emore.present.imp;

import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.view.RefreshListView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

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
    MessageSource mServerMessageSource;
    MessageSource mLocalMessageSource;

    public CommentMentionPresentImp(String token, WeiboSource weiboSource,
                                    MessageSource serverMessageSource,
                                    MessageSource localMessageSource,
                                    RefreshListView<Comment> mentionView) {
        mToken = token;
        mWeiboSource = weiboSource;
        mMentionView = mentionView;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
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
        Subscription su = createGetCommentObservable(0, true)
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

                        MessageUtil.resetUnReadMessage(mToken,
                                UnReadMessage.TYPE_MENTION_CMT, mServerMessageSource, mLocalMessageSource);
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
        Subscription su = createGetCommentObservable(maxId, false)
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

    private Observable<List<Comment>> createGetCommentObservable(long maxId, final boolean isRefresh) {
        return mWeiboSource.getCommentsMentions(mToken, 0, maxId, COUNT, 1)
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

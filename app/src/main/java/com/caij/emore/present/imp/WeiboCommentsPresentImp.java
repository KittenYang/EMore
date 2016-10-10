package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.event.CommentEvent;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.StatusAttitudeEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.present.WeiboCommentsPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.WeiboCommentsView;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.DefaultTransformer;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/6/16.
 */
public class WeiboCommentsPresentImp extends AbsBasePresent implements WeiboCommentsPresent, Action1<Event> {

    private static final int PAGE_COUNET = 20;

    private String mToken;
    private long mWeiboId;
    private WeiboCommentsView mWeiboCommentsView;
    private List<Comment> mComments;
    private Observable<Event> mCommentObservable;
    private Observable<Event> mWeiboRefreshObservable;

    private long mNextCursor;

    private CommentApi mCommentApi;

    public WeiboCommentsPresentImp(String token, long weiboId,
                                   CommentApi commentApi,
                                   WeiboCommentsView weiboCommentsView) {
        mToken = token;
        mCommentApi = commentApi;
        mWeiboCommentsView = weiboCommentsView;
        mWeiboId = weiboId;
        mComments = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的  如果不可见  可见时会自动刷新
     */
    private void registerEvent() {
        mCommentObservable = RxBus.getDefault().register(EventTag.EVENT_COMMENT_WEIBO_SUCCESS);
        mCommentObservable.subscribe(this);

        mWeiboRefreshObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_REFRESH);
        mWeiboRefreshObservable.subscribe(this);
    }

    @Override
    public void call(Event event) {
        if (EventTag.EVENT_COMMENT_WEIBO_SUCCESS.equals(event.type)) {
            CommentEvent commentEvent = (CommentEvent) event;
            if (commentEvent.statusId == mWeiboId) {
                SpannableStringUtil.paraeSpannable(commentEvent.comment);
                mComments.add(0, commentEvent.comment);
                mWeiboCommentsView.onCommentSuccess(mComments);
            }
        }else if (EventTag.EVENT_STATUS_REFRESH.equals(event.type)) {
            StatusRefreshEvent statusRefreshEvent = (StatusRefreshEvent) event;
            if (mWeiboId == statusRefreshEvent.statusId) {
                refresh();
            }
        }
    }

    private void refresh() {
        mNextCursor = 0L;
        Subscription subscription =  getCommentsObservable(mNextCursor)
                .subscribe(new DefaultResponseSubscriber<List<Comment>>(mWeiboCommentsView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        addRefreshDate(comments);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {
        registerEvent();

        mNextCursor = 0L;
        Subscription subscription =  getCommentsObservable(mNextCursor)
                .subscribe(new DefaultResponseSubscriber<List<Comment>>(mWeiboCommentsView) {

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

        addSubscription(subscription);
    }

    private void addRefreshDate(List<Comment> comments) {
        mComments.clear();
        mComments.addAll(comments);
        mWeiboCommentsView.setEntities(mComments);

        mWeiboCommentsView.onLoadComplete(comments.size() >= PAGE_COUNET - 5);
    }

    @Override
    public void loadMore() {
        Subscription subscription = getCommentsObservable(mNextCursor)
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
                        mWeiboCommentsView.notifyItemRangeInserted(mComments, mComments.size() - comments.size(),
                                comments.size());
                        mWeiboCommentsView.onLoadComplete(comments.size() > PAGE_COUNET - 5);
                    }
                });

        addSubscription(subscription);
    }

    private Observable<List<Comment>> getCommentsObservable(long maxId) {
        return mCommentApi.getCommentsByWeibo(mWeiboId, 0, maxId, PAGE_COUNET, 1)
                .compose(new ErrorCheckerTransformer<QueryWeiboCommentResponse>())
                .flatMap(new Func1<QueryWeiboCommentResponse, Observable<List<Comment>>>() {
                    @Override
                    public Observable<List<Comment>> call(QueryWeiboCommentResponse response) {
                        mNextCursor = response.getNext_cursor();
                        return Observable.just(response.getComments());
                    }
                })
                .doOnNext(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        for (Comment comment : comments) {
                            SpannableStringUtil.paraeSpannable(comment);
                        }
                    }
                })
                .compose(new SchedulerTransformer<List<Comment>>());
    }

    @Override
    public void deleteComment(final Comment comment) {
        Subscription subscription = mCommentApi.deleteComment(comment.getId())
                .compose(new DefaultTransformer<Comment>())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mWeiboCommentsView.showDialogLoading(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mWeiboCommentsView.showDialogLoading(false);
                    }
                })
                .subscribe(new DefaultResponseSubscriber<Comment>(mWeiboCommentsView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Comment c) {
                        mWeiboCommentsView.onDeleteSuccess(comment);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_COMMENT_WEIBO_SUCCESS, mCommentObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_REFRESH, mWeiboRefreshObservable);
    }


}

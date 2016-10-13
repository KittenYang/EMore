package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.event.CommentEvent;
import com.caij.emore.bean.event.Event;
import com.caij.emore.bean.event.StatusActionCountUpdateEvent;
import com.caij.emore.bean.event.StatusRefreshEvent;
import com.caij.emore.bean.response.QueryStatusCommentResponse;
import com.caij.emore.present.StatusCommentsPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.ui.view.StatusCommentsView;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.DefaultTransformer;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/6/16.
 */
public class StatusCommentsPresentImp extends AbsBasePresent implements StatusCommentsPresent, Action1<Event> {

    private static final int PAGE_COUNT = 20;

    private long mStatusId;
    private StatusCommentsView mStatusCommentsView;
    private CommentApi mCommentApi;

    private Observable<Event> mCommentObservable;
    private Observable<Event> mStatusRefreshObservable;

    private List<Comment> mComments;
    private long mNextCursor;


    public StatusCommentsPresentImp(long statusId,
                                    CommentApi commentApi,
                                    StatusCommentsView statusCommentsView) {
        mCommentApi = commentApi;
        mStatusCommentsView = statusCommentsView;
        mStatusId = statusId;
        mComments = new ArrayList<>();
    }

    /**
     * 这里是在fist visible 后添加事件的
     */
    private void registerEvent() {
        mCommentObservable = RxBus.getDefault().register(EventTag.EVENT_COMMENT_WEIBO_SUCCESS);
        mCommentObservable.subscribe(this);

        mStatusRefreshObservable = RxBus.getDefault().register(EventTag.EVENT_STATUS_REFRESH);
        mStatusRefreshObservable.subscribe(this);
    }

    @Override
    public void call(Event event) {
        if (EventTag.EVENT_COMMENT_WEIBO_SUCCESS.equals(event.type)) {
            CommentEvent commentEvent = (CommentEvent) event;
            if (commentEvent.statusId == mStatusId) {
                SpannableStringUtil.paraeSpannable(commentEvent.comment);
                mComments.add(0, commentEvent.comment);
                mStatusCommentsView.onCommentSuccess(mComments);
            }
        }else if (EventTag.EVENT_STATUS_REFRESH.equals(event.type)) {
            StatusRefreshEvent statusRefreshEvent = (StatusRefreshEvent) event;
            if (mStatusId == statusRefreshEvent.statusId) {
                refresh();
            }
        }
    }

    private void refresh() {
        Subscription subscription = getCommentsObservable(0)
                .subscribe(new ResponseSubscriber<List<Comment>>(mStatusCommentsView) {

                    @Override
                    protected void onFail(Throwable e) {
                        if (mComments.size() == 0) {
                            mStatusCommentsView.showErrorView();
                        }
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

        refresh();
    }

    private void addRefreshDate(List<Comment> comments) {
        mComments.clear();
        mComments.addAll(comments);
        mStatusCommentsView.setEntities(mComments);

        mStatusCommentsView.scrollToPosition(0);

        mStatusCommentsView.onLoadComplete(mNextCursor != 0);
    }

    @Override
    public void loadMore() {
        Subscription subscription = getCommentsObservable(mNextCursor)
                .subscribe(new ResponseSubscriber<List<Comment>>(mStatusCommentsView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mStatusCommentsView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mStatusCommentsView.notifyItemRangeInserted(mComments, mComments.size() - comments.size(),
                                comments.size());
                        mStatusCommentsView.onLoadComplete(mNextCursor != 0);
                    }
                });

        addSubscription(subscription);
    }

    private Observable<List<Comment>> getCommentsObservable(long maxId) {
        return mCommentApi.getCommentsByWeibo(mStatusId, 0, maxId, PAGE_COUNT, 1)
                .compose(ErrorCheckerTransformer.<QueryStatusCommentResponse>create())
                .flatMap(new Func1<QueryStatusCommentResponse, Observable<List<Comment>>>() {
                    @Override
                    public Observable<List<Comment>> call(QueryStatusCommentResponse response) {
                        mNextCursor = response.getNext_cursor();

                        StatusActionCountUpdateEvent event = new StatusActionCountUpdateEvent(EventTag.EVENT_STATUS_COMMENT_COUNT_UPDATE,
                                mStatusId, response.getTotal_number());
                        RxBus.getDefault().post(event.type, event);
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
                .compose(SchedulerTransformer.<List<Comment>>create());
    }

    @Override
    public void deleteComment(final Comment comment) {
        Subscription subscription = mCommentApi.deleteComment(comment.getId())
                .compose(new DefaultTransformer<Comment>())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mStatusCommentsView.showDialogLoading(true);
                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mStatusCommentsView.showDialogLoading(false);
                    }
                })
                .subscribe(new ResponseSubscriber<Comment>(mStatusCommentsView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Comment c) {
                        mStatusCommentsView.onDeleteSuccess(comment);
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
        RxBus.getDefault().unregister(EventTag.EVENT_STATUS_REFRESH, mStatusRefreshObservable);
    }


}

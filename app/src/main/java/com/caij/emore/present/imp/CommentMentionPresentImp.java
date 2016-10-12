package com.caij.emore.present.imp;

import com.caij.emore.bean.Comment;
import com.caij.emore.bean.response.QueryStatusCommentResponse;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.remote.CommentApi;
import com.caij.emore.remote.NotifyApi;
import com.caij.emore.ui.view.RefreshListView;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class CommentMentionPresentImp extends AbsBasePresent implements RefreshListPresent {

    private static final int COUNT = 20;

    private RefreshListView<Comment> mMentionView;
    private List<Comment> mComments;

    private long mUid;

    private CommentApi mCommentApi;
    private NotifyApi mNotifyApi;
    private NotifyManager mNotifyManager;

    public CommentMentionPresentImp(long uid, CommentApi commentApi,
                                    NotifyApi notifyApi,
                                    NotifyManager notifyManager,
                                    RefreshListView<Comment> mentionView) {
        super();
        mCommentApi = commentApi;
        mMentionView = mentionView;
        mNotifyApi = notifyApi;
        mNotifyManager = notifyManager;
        mComments = new ArrayList<>();
        mUid = uid;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void refresh() {
        Subscription su = getCommentMentionsObservable(0, true)
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mMentionView.onRefreshComplete();
                    }
                })
                .subscribe(new ResponseSubscriber<List<Comment>>(mMentionView) {

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mMentionView.setEntities(mComments);

                        mMentionView.onLoadComplete(comments.size() > COUNT - 1);

                        MessageUtil.resetUnReadMessage(UnReadMessage.TYPE_MENTION_CMT,
                                mUid, mNotifyApi, mNotifyManager);
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
        Subscription su = getCommentMentionsObservable(maxId, false)
                .subscribe(new ResponseSubscriber<List<Comment>>(mMentionView) {
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

    private Observable<List<Comment>> getCommentMentionsObservable(long maxId, final boolean isRefresh) {
        return mCommentApi.getCommentsMentions(0, maxId, COUNT, 1)
                .compose(new ErrorCheckerTransformer<QueryStatusCommentResponse>())
                .flatMap(new Func1<QueryStatusCommentResponse, Observable<Comment>>() {
                    @Override
                    public Observable<Comment> call(QueryStatusCommentResponse response) {
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

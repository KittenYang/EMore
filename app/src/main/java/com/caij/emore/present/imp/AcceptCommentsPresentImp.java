package com.caij.emore.present.imp;

import com.caij.emore.account.Account;
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
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/4.
 */
public class AcceptCommentsPresentImp extends AbsBasePresent implements RefreshListPresent {

    private static final int PAGE_COUNT = 20;

    private Account mAccount;

    private RefreshListView<Comment> mMentionView;
    private List<Comment> mComments;

    private NotifyManager mNotifyManager;
    private CommentApi mCommentApi;
    private NotifyApi mNotifyApi;

    public AcceptCommentsPresentImp(Account account, CommentApi commentApi, NotifyApi notifyApi,
                                    NotifyManager notifyManager, RefreshListView<Comment> mentionView) {
        super();
        mAccount = account;
        mCommentApi = commentApi;
        mMentionView = mentionView;
        mNotifyApi = notifyApi;
        mNotifyManager = notifyManager;
        mComments = new ArrayList<>();
    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void refresh() {
        Subscription su =  getCommentObservable(0, true)
                .subscribe(new ResponseSubscriber<List<Comment>>(mMentionView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mMentionView.onRefreshComplete();
                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mMentionView.setEntities(mComments);

                        mMentionView.onRefreshComplete();
                        mMentionView.onLoadComplete(comments.size() > PAGE_COUNT - 1);

                        MessageUtil.resetUnReadMessage(UnReadMessage.TYPE_CMT, mAccount.getUid(), mNotifyApi, mNotifyManager);
                    }
                });
        addSubscription(su);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mComments != null && mComments.size() > 1) {
            maxId = mComments.get(mComments.size() - 1).getId();
        }
        Subscription subscription = getCommentObservable(maxId, false)
                .subscribe(new ResponseSubscriber<List<Comment>>(mMentionView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mMentionView.onLoadComplete(true);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<Comment> comments) {
                        mComments.addAll(comments);
                        mMentionView.notifyItemRangeInserted(mComments, mComments.size() - comments.size(),
                                comments.size());

                        mMentionView.onLoadComplete(comments.size() > PAGE_COUNT - 1);
                    }
                });
        addSubscription(subscription);
    }

    private Observable<List<Comment>> getCommentObservable(long maxId, final boolean isRefresh) {
        return mCommentApi.getAcceptComments(0, maxId, PAGE_COUNT, 1)
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

    @Override
    public void onCreate() {

    }

}

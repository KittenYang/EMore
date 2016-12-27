package com.caij.emore.present.imp;

import com.caij.emore.EMApplication;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.present.StrangerMessagePresent;
import com.caij.emore.remote.MessageApi;
import com.caij.emore.remote.imp.NotifyApiImp;
import com.caij.emore.ui.view.RefreshListView;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Ca1j on 2016/12/26.
 */

public class StrangerMessagePresentImp extends AbsBasePresent implements StrangerMessagePresent {

    private static final int PAGE_COUNT = 20;
    private MessageApi mMessageApi;
    private RefreshListView<DirectMessage> mRefreshListView;

    private List<DirectMessage> mDirectMessages;

    public StrangerMessagePresentImp(MessageApi messageApi, RefreshListView<DirectMessage> refreshListView) {
        mMessageApi = messageApi;
        mRefreshListView = refreshListView;
        mDirectMessages = new ArrayList<>();
    }

    @Override
    public void refresh() {
        Subscription subscription = loadMessage(Long.MAX_VALUE)
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mRefreshListView.onRefreshComplete();
                    }
                }).subscribe(new ResponseSubscriber<List<DirectMessage>>(mRefreshListView) {
                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        mDirectMessages.clear();

                        mDirectMessages.addAll(directMessages);

                        mRefreshListView.setEntities(mDirectMessages);

                        mRefreshListView.onLoadComplete(directMessages.size() >= PAGE_COUNT);

                        MessageUtil.resetUnReadMessage(UnReadMessage.TYPE_MSG_BOX, UserPrefs.get(EMApplication.getInstance()).getAccount().getUid(),
                                new NotifyApiImp(), new NotifyManagerImp());
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        long maxId = Long.MAX_VALUE;
        if (mDirectMessages.size() > 0) {
            maxId = mDirectMessages.get(mDirectMessages.size() - 1).getId();
        }
        Subscription subscription = loadMessage(maxId)
                .subscribe(new ResponseSubscriber<List<DirectMessage>>(mRefreshListView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mRefreshListView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        mDirectMessages.addAll(directMessages);

                        mRefreshListView.notifyItemRangeInserted(mDirectMessages,
                                mDirectMessages.size() - directMessages.size() - 1, directMessages.size());

                        mRefreshListView.onLoadComplete(directMessages.size() >= PAGE_COUNT);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {

    }

    private Observable<List<DirectMessage>> loadMessage(long max_id) {
        return mMessageApi.getStrangerMessages(0, max_id, PAGE_COUNT, 1)
                .compose(ErrorCheckerTransformer.<UserMessageResponse>create())
                .flatMap(new Func1<UserMessageResponse, Observable<DirectMessage>>() {
                    @Override
                    public Observable<DirectMessage> call(UserMessageResponse userMessageResponse) {
                        return Observable.from(userMessageResponse.getDirect_messages());
                    }
                })
                .compose(new ChatPresentImp.MessageTransformer())
                .toList()
                .compose(SchedulerTransformer.<List<DirectMessage>>create());
    }

}

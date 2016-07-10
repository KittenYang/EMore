package com.caij.emore.present.imp;

import com.caij.emore.bean.MessageUser;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.present.view.RefreshListView;
import com.caij.emore.source.DefaultResponseSubscriber;
import com.caij.emore.source.MessageSource;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/10.
 */
public class MessageUserPresentImp implements MessageUserPresent {

    private final static int PAGE_COUNT = 20;

    private CompositeSubscription mCompositeSubscription;
    private String mToken;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private List<MessageUser.UserListBean> userListBeens;
    private RefreshListView<MessageUser.UserListBean> mRefreshListView;
    private MessageUser mMessageUser;

    public MessageUserPresentImp(String token, MessageSource serverMessageSource,
                                 MessageSource localMessageSource, RefreshListView<MessageUser.UserListBean> refreshListView) {
        mServerMessageSource = serverMessageSource;
        mToken = token;
        mLocalMessageSource = localMessageSource;
        mRefreshListView = refreshListView;
        userListBeens = new ArrayList<>();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void refresh() {
        Subscription subscription = mServerMessageSource.getMessageUserList(mToken, PAGE_COUNT, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<MessageUser>(mRefreshListView) {

                    @Override
                    public void onCompleted() {
                        mRefreshListView.onRefreshComplete();

                    }

                    @Override
                    public void onNext(MessageUser messageUser) {
                        mMessageUser = messageUser;
                        userListBeens.clear();
                        userListBeens.addAll(messageUser.getUser_list());
                        mRefreshListView.setEntities(userListBeens);
                        mRefreshListView.onLoadComplete(messageUser.getUser_list().size() >= PAGE_COUNT - 1);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mRefreshListView.onRefreshComplete();
                        mRefreshListView.onDefaultLoadError();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        long cursor = mMessageUser == null ? 0 : mMessageUser.getNext_cursor();
        Subscription subscription = mServerMessageSource.getMessageUserList(mToken, PAGE_COUNT, cursor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<MessageUser>(mRefreshListView) {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(MessageUser messageUser) {
                        mMessageUser = messageUser;
                        userListBeens.addAll(messageUser.getUser_list());
                        mRefreshListView.setEntities(userListBeens);
                        mRefreshListView.onLoadComplete(messageUser.getUser_list().size() >= PAGE_COUNT - 1);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mRefreshListView.onDefaultLoadError();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }
}

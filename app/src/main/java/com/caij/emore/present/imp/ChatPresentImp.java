package com.caij.emore.present.imp;

import com.caij.emore.bean.DirectMessage;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.view.BaseListView;
import com.caij.emore.present.view.RefreshListView;
import com.caij.emore.source.MessageSource;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/10.
 */
public class ChatPresentImp implements ChatPresent {

    private CompositeSubscription mCompositeSubscription;
    private String mToken;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private List<DirectMessage> mDirectMessages;
    private BaseListView<DirectMessage> mListView;
    private long mUserId;

    public ChatPresentImp(String token, long uid, MessageSource serverMessageSource, MessageSource localMessageSource,
                          BaseListView<DirectMessage> listView) {
        mToken = token;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mDirectMessages = new ArrayList<>();
        mListView = listView;
        mCompositeSubscription = new CompositeSubscription();
        mUserId = uid;
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void onCreate() {
        mServerMessageSource.getUserMessage(mToken, mUserId, 0, 0, 20, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserMessageResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mListView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(UserMessageResponse userMessageResponse) {
                        mDirectMessages.clear();
                        mDirectMessages.addAll(userMessageResponse.getDirect_messages());
                        mListView.setEntities(mDirectMessages);
                    }
                });
    }

    @Override
    public void onDestroy() {

    }

}

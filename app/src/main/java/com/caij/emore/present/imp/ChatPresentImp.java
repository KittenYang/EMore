package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.bean.DirectMessage;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.view.DirectMessageView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/10.
 */
public class ChatPresentImp implements ChatPresent {

    private static final int PAGE_COUNT = 20;

    private CompositeSubscription mCompositeSubscription;
    private String mToken;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private List<DirectMessage> mDirectMessages;
    private DirectMessageView mDirectMessageView;
    private long mUserId;
    private Observable<DirectMessage> mSendMessageObservable;

    public ChatPresentImp(String token, long uid, MessageSource serverMessageSource, MessageSource localMessageSource,
                          DirectMessageView directMessageView) {
        mToken = token;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mDirectMessages = new ArrayList<>();
        mDirectMessageView = directMessageView;
        mCompositeSubscription = new CompositeSubscription();
        mUserId = uid;
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mDirectMessages.size() > 1) {
            maxId = mDirectMessages.get(0).getId();
        }
        Subscription subscription = mServerMessageSource.getUserMessage(mToken, mUserId, 0, maxId, PAGE_COUNT, 1)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<UserMessageResponse, Observable<DirectMessage>>() {
                    @Override
                    public Observable<DirectMessage> call(UserMessageResponse userMessageResponse) {
                        return Observable.from(userMessageResponse.getDirect_messages());
                    }
                })
                .filter(new Func1<DirectMessage, Boolean>() {
                    @Override
                    public Boolean call(DirectMessage directMessage) {
                        return !mDirectMessages.contains(directMessage);
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DirectMessage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mDirectMessageView.onDefaultLoadError();
                        mDirectMessageView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        Collections.reverse(directMessages);
                        mDirectMessages.addAll(0, directMessages);
                        mDirectMessageView.setEntities(mDirectMessages);
                        if (directMessages.size() >= PAGE_COUNT - 1) {
                            mDirectMessageView.onLoadComplete(true);
                        }else {
                            mDirectMessageView.onLoadComplete(false);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {
        mSendMessageObservable = RxBus.get().register(Key.SEND_MESSAGE_RESULT_EVENT);
        mSendMessageObservable.subscribe(new Action1<DirectMessage>() {
            @Override
            public void call(DirectMessage message) {
                mDirectMessageView.onSendEnd(message);
                LogUtil.d(ChatPresentImp.this, "mSendMessageObservable subscribe call");
            }
        });

        Subscription subscription = mServerMessageSource.getUserMessage(mToken, mUserId, 0, 0, PAGE_COUNT, 1)
                .flatMap(new Func1<UserMessageResponse, Observable<DirectMessage>>() {
                    @Override
                    public Observable<DirectMessage> call(UserMessageResponse userMessageResponse) {
                        return Observable.from(userMessageResponse.getDirect_messages());
                    }
                })
                .filter(new Func1<DirectMessage, Boolean>() {
                    @Override
                    public Boolean call(DirectMessage directMessage) {
                        return !mDirectMessages.contains(directMessage);
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DirectMessage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mDirectMessageView.onDefaultLoadError();
                    }

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        mDirectMessages.clear();
                        Collections.reverse(directMessages);
                        mDirectMessages.addAll(directMessages);
                        mDirectMessageView.setEntities(mDirectMessages);
                        mDirectMessageView.toScrollBottom();
                        if (directMessages.size() >= PAGE_COUNT - 1) {
                            mDirectMessageView.onLoadComplete(true);
                        }else {
                            mDirectMessageView.onLoadComplete(false);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
        RxBus.get().unregister(Key.SEND_MESSAGE_RESULT_EVENT, mSendMessageObservable);
    }

    @Override
    public void sendMessage(DirectMessage message) {
        EventUtil.sendMessage(message);
    }
}

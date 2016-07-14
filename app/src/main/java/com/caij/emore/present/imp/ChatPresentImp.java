package com.caij.emore.present.imp;

import android.os.Handler;

import com.caij.emore.Key;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.LocakImage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.view.DirectMessageView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.local.LocalImageSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.ArrayList;
import java.util.Collections;
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
 * Created by Caij on 2016/7/10.
 */
public class ChatPresentImp implements ChatPresent {

    private static final int PAGE_COUNT = 20;

    private CompositeSubscription mCompositeSubscription;
    private String mToken;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private LocalImageSource localImageSource;
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
        localImageSource = new LocalImageSource();
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
        Subscription subscription = mLocalMessageSource.getUserMessage(mToken, mUserId, 0, maxId, PAGE_COUNT, 1)
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
                .map(new Func1<DirectMessage, DirectMessage>() {
                    @Override
                    public DirectMessage call(DirectMessage message) {
                        mLocalMessageSource.saveMessage(message);
                        if (message.getAtt_ids() != null && message.getAtt_ids().size() > 0) {
                            getMessageInfo(message);
                        }
                        return message;
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
                    public void onNext(final List<DirectMessage> directMessages) {
                        Collections.reverse(directMessages);
                        mDirectMessages.addAll(0, directMessages);
                        mDirectMessageView.setEntities(mDirectMessages);
                        mDirectMessageView.toScrollToPosition(directMessages.size());

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

        Observable<UserMessageResponse> localObservable =  mLocalMessageSource.getUserMessage(mToken, mUserId, 0, 0, PAGE_COUNT, 1);
        localObservable.flatMap(new Func1<UserMessageResponse, Observable<DirectMessage>>() {
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
                .map(new Func1<DirectMessage, DirectMessage>() {
                    @Override
                    public DirectMessage call(DirectMessage message) {
                        if (message.getAtt_ids() != null && message.getAtt_ids().size() > 0) {
                            getMessageInfo(message);
                        }
                        return message;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<DirectMessage>>() {
                    @Override
                    public void call(List<DirectMessage> directMessages) {
                        Collections.reverse(directMessages);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DirectMessage>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        loadNewMessage(0);
                    }

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        mDirectMessages.addAll(directMessages);
                        mDirectMessageView.setEntities(mDirectMessages);
                        mDirectMessageView.toScrollToPosition(mDirectMessages.size());
                        if (directMessages.size() >= PAGE_COUNT - 1) {
                            mDirectMessageView.onLoadComplete(true);
                        }else {
                            mDirectMessageView.onLoadComplete(false);
                        }
                        if (directMessages.size() == 0) {
                            loadNewMessage(0);
                        }else {
                            loadNewMessage(mDirectMessages.get(mDirectMessages.size() - 1).getId());
                        }
                    }
                });
    }

    private void loadNewMessage(final long sinceId){
        if (sinceId == 0) {
            mDirectMessageView.showDialogLoading(true);
        }
        Subscription subscription = mServerMessageSource.getUserMessage(mToken, mUserId, sinceId, 0, 100, 1)
                .flatMap(new Func1<UserMessageResponse, Observable<DirectMessage>>() {
                    @Override
                    public Observable<DirectMessage> call(UserMessageResponse userMessageResponse) {
                        return Observable.from(userMessageResponse.getDirect_messages());
                    }
                })
                .doOnNext(new Action1<DirectMessage>() {
                    @Override
                    public void call(DirectMessage message) {
                        mDirectMessages.remove(message);
                    }
                })
                .map(new Func1<DirectMessage, DirectMessage>() {
                    @Override
                    public DirectMessage call(DirectMessage message) {
                        mLocalMessageSource.saveMessage(message);
                        if (message.getAtt_ids() != null && message.getAtt_ids().size() > 0) {
                            getMessageInfo(message);
                        }
                        return message;
                    }
                })
                .toList()
                .doOnNext(new Action1<List<DirectMessage>>() {
                    @Override
                    public void call(List<DirectMessage> directMessages) {
                        Collections.reverse(directMessages);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DirectMessage>>() {
                    @Override
                    public void onCompleted() {
                        if (sinceId == 0) {
                            mDirectMessageView.showDialogLoading(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d("loadNewMessage", e.getMessage());
                        if (sinceId == 0) {
                            mDirectMessageView.showDialogLoading(false);
                        }
                    }

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        mDirectMessages.addAll(directMessages);
                        mDirectMessageView.setEntities(mDirectMessages);
                        mDirectMessageView.toScrollToPosition(mDirectMessages.size());
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private void getMessageInfo(final DirectMessage directMessage) {
        Observable<MessageImage> localObservable = mLocalMessageSource.getMessageImageInfo(mToken, directMessage.getAtt_ids().get(0));
        final Observable<MessageImage> serverObservable = mServerMessageSource.getMessageImageInfo(mToken, directMessage.getAtt_ids().get(0))
                .doOnNext(new Action1<MessageImage>() {
                    @Override
                    public void call(MessageImage messageImage) {
                        mLocalMessageSource.saveMessageImage(messageImage);
                    }
                });
        Observable.concat(localObservable, serverObservable)
                .first(new Func1<MessageImage, Boolean>() {
                    @Override
                    public Boolean call(MessageImage messageImage) {
                        return messageImage != null;
                    }
                }).subscribe(new Action1<MessageImage>() {
                    @Override
                    public void call(MessageImage messageImage) {
                        LocakImage locakImage = localImageSource.get(messageImage.getThumbnail_600());
                        if (locakImage == null) {
                            locakImage =  new LocakImage();
                            locakImage.setUrl(messageImage.getThumbnail_600());
                            Map<String, String> params =  UrlUtil.getUrlParams(messageImage.getThumbnail_600());
                            String size  = params.get("size");
                            String[] strings = size.split(",");
                            locakImage.setWidth(Integer.parseInt(strings[0]));
                            locakImage.setHeight(Integer.parseInt(strings[1]));
                            localImageSource.save(locakImage);
                        }
                        directMessage.setLocakImage(locakImage);
                    }
                });
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

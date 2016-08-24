package com.caij.emore.present.imp;

import com.caij.emore.AppApplication;
import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.bean.event.MessageResponseEvent;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.present.ChatManagerPresent;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
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
 * Created by Caij on 2016/7/15.
 */
public class ChatManagerPresentImp extends AbsBasePresent implements ChatManagerPresent {

    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private Observable<MessageResponseEvent> mMessageSendObservable;
    private String mToken;

    public ChatManagerPresentImp(String token, MessageSource serverMessageSource,
                                 MessageSource localMessageSource) {
        super();
        mToken = token;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mMessageSendObservable = RxBus.getDefault().register(Event.SEND_MESSAGE_EVENT);
        mMessageSendObservable.subscribe(new Action1<MessageResponseEvent>() {
            @Override
            public void call(MessageResponseEvent bean) {
                senMessage(bean);
            }
        });
    }

    private void senMessage(final MessageResponseEvent bean) {
        LogUtil.d(this, "senMessage");
        Observable<DirectMessage> sendMessageObservable;
        if (bean.message.getAtt_ids() == null || bean.message.getAtt_ids().size() == 0) { //文本
            sendMessageObservable = mServerMessageSource.createTextMessage(mToken,
                    bean.message.getText(), bean.message.getRecipient_id());
        }else {
            final Map<String, Object> params = new HashMap<>();
            params.put("source", Key.WEICO_APP_ID);
            params.put("from", Key.WEICO_APP_FROM);
            final File file = new File(URI.create(bean.message.getImageInfo().getUrl()));
            sendMessageObservable = Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    try {
                        subscriber.onNext(ImageUtil.compressImage(file.getAbsolutePath(),
                                AppApplication.getInstance()));
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }).flatMap(new Func1<String, Observable<MessageImage>>() {
                @Override
                public Observable<MessageImage> call(String s) {
                    return mServerMessageSource.uploadMessageImage(params, mToken,  bean.message.getRecipient_id(), s);
                }
            }).doOnNext(new Action1<MessageImage>() {
                @Override
                public void call(MessageImage messageImage) {
                    mLocalMessageSource.saveMessageImage(messageImage);
                }
            }).flatMap(new Func1<MessageImage, Observable<DirectMessage>>() {
                @Override
                public Observable<DirectMessage> call(MessageImage messageImage) {
                    long vifid = messageImage.getVfid();
                    long tofid = messageImage.getTovfid();
                    return mServerMessageSource.createImageMessage(mToken, bean.message.getText(), bean.message.getRecipient_id(),
                            bean.message.getRecipient_screen_name(), String.valueOf(vifid) + "," + tofid);
                }
            });
        }
        Subscription subscription = sendMessageObservable
                .doOnNext(new Action1<DirectMessage>() {
                    @Override
                    public void call(DirectMessage message) {
                        mLocalMessageSource.removeMessageById(bean.localMessageId);
                        message.setLocal_status(DirectMessage.STATUS_SUCCESS);
                        mLocalMessageSource.saveMessage(message);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DirectMessage directMessage = mLocalMessageSource.getMessageById(bean.localMessageId);
                        if (directMessage != null) {
                            directMessage.setLocal_status(DirectMessage.STATUS_FAIL);
                            mLocalMessageSource.saveMessage(directMessage);
                        }
                    }
                })
                .subscribeOn(Schedulers.from(ExecutorServiceUtil.SEND_MESSAGE_SERVICE)) //这里单线程发送消息， 防止消息位置错乱
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DirectMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        bean.message.setLocal_status(DirectMessage.STATUS_FAIL);
                        MessageResponseEvent responseEvent = new MessageResponseEvent(bean.localMessageId, bean.message);
                        RxBus.getDefault().post(Event.EVENT_SEND_MESSAGE_RESULT, responseEvent);
                        LogUtil.d(ChatManagerPresentImp.this, "message send error " + e.getMessage());
                    }

                    @Override
                    public void onNext(DirectMessage directMessage) {
                        MessageResponseEvent responseEvent = new MessageResponseEvent(bean.localMessageId, directMessage);
                        RxBus.getDefault().post(Event.EVENT_SEND_MESSAGE_RESULT, responseEvent);
                        LogUtil.d(ChatManagerPresentImp.this, "message send success");
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
        RxBus.getDefault().unregister(Event.SEND_MESSAGE_EVENT, mMessageSendObservable);
    }
}

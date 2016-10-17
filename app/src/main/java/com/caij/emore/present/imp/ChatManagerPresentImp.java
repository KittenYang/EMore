package com.caij.emore.present.imp;

import com.caij.emore.EMApplication;
import com.caij.emore.EventTag;
import com.caij.emore.bean.MessageImage;
import com.caij.emore.bean.event.MessageResponseEvent;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.manager.MessageManager;
import com.caij.emore.present.ChatManagerPresent;
import com.caij.emore.remote.MessageApi;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import java.io.File;
import java.net.URI;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/15.
 */
public class ChatManagerPresentImp extends AbsBasePresent implements ChatManagerPresent {

    private MessageApi mMessageApi;
    private MessageManager mMessageManager;
    private Observable<MessageResponseEvent> mMessageSendObservable;
    private String mToken;

    public ChatManagerPresentImp(String token, MessageApi messageApi,
                                 MessageManager messageManager) {
        super();
        mToken = token;
        mMessageApi = messageApi;
        mMessageManager = messageManager;
        mMessageSendObservable = RxBus.getDefault().register(EventTag.SEND_MESSAGE_EVENT);
        mMessageSendObservable.subscribe(new Action1<MessageResponseEvent>() {
            @Override
            public void call(MessageResponseEvent bean) {
                senMessage(bean);
            }
        });
    }

    private void senMessage(final MessageResponseEvent bean) {
        Observable<DirectMessage> sendMessageObservable;
        if (bean.message.getAtt_ids() == null || bean.message.getAtt_ids().size() == 0) { //文本
            sendMessageObservable = mMessageApi.createTextMessage(bean.message.getText(), bean.message.getRecipient_id());
        }else {
            final File file = new File(URI.create(bean.message.getAttachinfo().get(0).getThumbnail()));
            sendMessageObservable = RxUtil.createDataObservable(new RxUtil.Provider<String>() {
                @Override
                public String getData() throws Exception {
                    return ImageUtil.compressImage(file.getAbsolutePath(),
                            EMApplication.getInstance());
                }
            }).flatMap(new Func1<String, Observable<MessageImage>>() {
                @Override
                public Observable<MessageImage> call(String s) {
                    return mMessageApi.uploadMessageImage(bean.message.getRecipient_id(), s);
                }
            }).flatMap(new Func1<MessageImage, Observable<DirectMessage>>() {
                @Override
                public Observable<DirectMessage> call(MessageImage messageImage) {
                    long vifid = messageImage.getVfid();
                    long tofid = messageImage.getTovfid();
                    return mMessageApi.createImageMessage(bean.message.getText(), bean.message.getRecipient_id(),
                            bean.message.getRecipient_screen_name(), String.valueOf(vifid) + "," + tofid);
                }
            });
        }
        Subscription subscription = sendMessageObservable
                .doOnNext(new Action1<DirectMessage>() {
                    @Override
                    public void call(DirectMessage message) {
                        DirectMessage localMessage = mMessageManager.getMessageById(bean.localMessageId);

                        mMessageManager.deleteMessageById(bean.localMessageId);

                        localMessage.setId(message.getId());
                        localMessage.setLocal_status(DirectMessage.STATUS_SUCCESS);
                        mMessageManager.saveMessage(localMessage);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DirectMessage directMessage = mMessageManager.getMessageById(bean.localMessageId);
                        if (directMessage != null) {
                            directMessage.setLocal_status(DirectMessage.STATUS_FAIL);
                            mMessageManager.saveMessage(directMessage);
                        }
                    }
                })
                .subscribeOn(Schedulers.from(ExecutorServiceUtil.SEND_MESSAGE_SERVICE)) //这里单线程发送消息， 防止消息位置错乱
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberAdapter<DirectMessage>() {

                    @Override
                    public void onError(Throwable e) {
                        bean.message.setLocal_status(DirectMessage.STATUS_FAIL);
                        MessageResponseEvent responseEvent = new MessageResponseEvent(EventTag.EVENT_SEND_MESSAGE_RESULT, bean.localMessageId, null);
                        RxBus.getDefault().post(EventTag.EVENT_SEND_MESSAGE_RESULT, responseEvent);
                        LogUtil.d(ChatManagerPresentImp.this, "message send error " + e.getMessage());
                    }

                    @Override
                    public void onNext(DirectMessage directMessage) {
                        MessageResponseEvent responseEvent = new MessageResponseEvent(EventTag.EVENT_SEND_MESSAGE_RESULT, bean.localMessageId, directMessage);
                        RxBus.getDefault().post(EventTag.EVENT_SEND_MESSAGE_RESULT, responseEvent);
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
        RxBus.getDefault().unregister(EventTag.SEND_MESSAGE_EVENT, mMessageSendObservable);
    }
}

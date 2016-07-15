package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.present.ChatManagerPresent;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.weibo.ApiUtil;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/15.
 */
public class ChatManagerPresentImp implements ChatManagerPresent {

//    private String mToken;
    private CompositeSubscription mCompositeSubscription;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private Observable<DirectMessage> mMessageSendObservable;

    public ChatManagerPresentImp(String token, MessageSource serverMessageSource, MessageSource localMessageSource) {
//        mToken = token;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mCompositeSubscription = new CompositeSubscription();
        mMessageSendObservable = EventUtil.registSendMessageEvent();
        mMessageSendObservable.subscribe(new Action1<DirectMessage>() {
            @Override
            public void call(DirectMessage bean) {
                senMessage(bean);
            }
        });
    }

    @Override
    public void senMessage(final DirectMessage bean) {
        AccessToken token = UserPrefs.get().getWeiCoToken();
        Observable<DirectMessage> sendMessageObservable;
        if (bean.getAtt_ids() == null || bean.getAtt_ids().size() == 0) { //文本
            sendMessageObservable = mServerMessageSource.createTextMessage(token.getAccess_token(),
                    bean.getText(), bean.getRecipient_id());
        }else {
            Map<String, Object> params = new HashMap<>();
            ApiUtil.appendAuth(params);
            File file = new File(URI.create(bean.getLocakImage().getUrl()));
            sendMessageObservable = mServerMessageSource.createImageMessage(token.getAccess_token(), params,
                    "分享图片", file.getPath(), bean.getRecipient_id(), bean.getRecipient_screen_name());
        }
        Subscription subscription = sendMessageObservable
                .subscribeOn(Schedulers.from(ExecutorServiceUtil.SEND_MESSAGE_SERVICE)) //这里单线程发送消息， 防止消息位置错乱
                .doOnNext(new Action1<DirectMessage>() {
                    @Override
                    public void call(DirectMessage message) {
                        mLocalMessageSource.removeMessage(bean);
                        message.setLocal_status(DirectMessage.STATUS_SUCCESS);
                        mLocalMessageSource.saveMessage(message);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        bean.setLocal_status(DirectMessage.STATUS_FAIL);
                        mLocalMessageSource.saveMessage(bean);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DirectMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        bean.setLocal_status(DirectMessage.STATUS_FAIL);
                        RxBus.get().post(Key.SEND_MESSAGE_RESULT_EVENT, bean);
                        LogUtil.d(ChatManagerPresentImp.this, "message send error " + e.getMessage());
                    }

                    @Override
                    public void onNext(DirectMessage directMessage) {
                        bean.setId(directMessage.getId());
                        bean.setCreated_at(directMessage.getCreated_at());
                        bean.setSender_id(directMessage.getSender_id());
                        bean.setRecipient_id(directMessage.getRecipient_id());
                        bean.setRecipient(directMessage.getRecipient());
                        bean.setMedia_type(directMessage.getMedia_type());
                        bean.setLocal_status(DirectMessage.STATUS_SUCCESS);
                        RxBus.get().post(Key.SEND_MESSAGE_RESULT_EVENT, directMessage);
                        LogUtil.d(ChatManagerPresentImp.this, "message send success");
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
        EventUtil.unregistSendMessageEvent(mMessageSendObservable);
    }
}

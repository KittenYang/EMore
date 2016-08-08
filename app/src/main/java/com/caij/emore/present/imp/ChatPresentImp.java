package com.caij.emore.present.imp;

import android.graphics.BitmapFactory;
import android.net.Uri;

import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.LocakImage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.present.view.DirectMessageView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.UserSource;
import com.caij.emore.source.local.LocalImageSource;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.UrlUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.weibo.MessageUtil;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
    private static final long MESSAGE_INTERVAL_TIME = 10 * 1000;

    private CompositeSubscription mCompositeSubscription;
    private AccessToken mToken;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private UserSource mLocalUserSource;
    private LocalImageSource localImageSource;
    private List<DirectMessage> mDirectMessages;
    private DirectMessageView mDirectMessageView;
    private long mUserId;
    private Observable<DirectMessage> mSendMessageObservable;
    private Timer mLoadMessageTimer;
    private TimerTask mLoadMessageTask;

    public ChatPresentImp(AccessToken token, long uid,
                          MessageSource serverMessageSource,
                          MessageSource localMessageSource,
                          UserSource localUserSource,
                          DirectMessageView directMessageView) {
        mToken = token;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mLocalUserSource = localUserSource;
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
        Subscription subscription = mLocalMessageSource.getUserMessage(mToken.getAccess_token(), mUserId, 0, maxId, PAGE_COUNT, 1)
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
                        if (message.getAtt_ids() != null && message.getAtt_ids().size() > 0) {
                            getMessageImageInfo(message);
                        }
                        return message;
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
                        mDirectMessageView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(final List<DirectMessage> directMessages) {
                        Collections.reverse(directMessages);
                        mDirectMessages.addAll(0, directMessages);

                        if (directMessages.size() > 0) {
                            mDirectMessageView.addMore(mDirectMessages, directMessages.size());
                        }

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
        mSendMessageObservable = RxBus.get().register(Event.SEND_MESSAGE_RESULT_EVENT);
        mSendMessageObservable.subscribe(new Action1<DirectMessage>() {
            @Override
            public void call(DirectMessage message) {
                mDirectMessageView.notifyDataChange();
                LogUtil.d(ChatPresentImp.this, "mSendMessageObservable subscribe call");
            }
        });

        Observable<UserMessageResponse> localObservable =  mLocalMessageSource.getUserMessage(mToken.getAccess_token(), mUserId, 0, 0, PAGE_COUNT, 1);
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
                            getMessageImageInfo(message);
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
                        loadNewMessage(0, true);
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
                            loadNewMessage(0, true);
                        }else {
                            loadNewMessage(getNewMessageSinceId(), false);
                        }
                    }
                });

        initLooperLoadMessage();
    }

    private void initLooperLoadMessage() {
        mLoadMessageTimer = new Timer();
        mLoadMessageTask = new TimerTask() {
            @Override
            public void run() {
                loadNewMessage(getNewMessageSinceId(), false);
            }
        };
        mLoadMessageTimer.schedule(mLoadMessageTask, MESSAGE_INTERVAL_TIME, MESSAGE_INTERVAL_TIME);
    }

    private void cancelLooperLoadMessage() {
        if (mLoadMessageTimer != null) {
            mLoadMessageTimer.cancel();
        }
        if (mLoadMessageTask != null) {
            mLoadMessageTask.cancel();
        }
    }

    private long getNewMessageSinceId() {
        for (int i = mDirectMessages.size() - 1; i >= 0; i --){
            DirectMessage message = mDirectMessages.get(i);
            if (message.getLocal_status() == DirectMessage.STATUS_SERVER
                    || message.getLocal_status() == 0) {
                return message.getId();
            }
        }
        return 0;
    }

    private void loadNewMessage(final long sinceId, final boolean isShowDialog){
        if (isShowDialog) {
            mDirectMessageView.showDialogLoading(true);
        }
        Subscription subscription = mServerMessageSource.getUserMessage(mToken.getAccess_token(), mUserId, sinceId, 0, 100, 1)
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
                        message.setCreated_at_long(DateUtil.parseCreateTime(message.getCreated_at()));
                        if (message.getAtt_ids() != null && message.getAtt_ids().size() > 0) {
                            getMessageImageInfo(message);
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
                        if (isShowDialog) {
                            mDirectMessageView.showDialogLoading(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d("loadNewMessage", e.getMessage());
                        if (isShowDialog) {
                            mDirectMessageView.showDialogLoading(false);
                        }
                    }

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        mDirectMessages.addAll(directMessages);
                        mDirectMessageView.setEntities(mDirectMessages);
                        if (isShowDialog) {
                            mDirectMessageView.toScrollToPosition(mDirectMessages.size());
                        }else {
                            mDirectMessageView.attemptSmoothScrollToBottom();
                        }

                        if (directMessages.size() > 0) {
                            MessageUtil.resetLocalUnReadMessageDisValue(mToken.getAccess_token(), UnReadMessage.TYPE_DM,
                                    directMessages.size(), mLocalMessageSource);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private void getMessageImageInfo(final DirectMessage directMessage) {
        Observable<MessageImage> localObservable = mLocalMessageSource.getMessageImageInfo(mToken.getAccess_token(),
                directMessage.getAtt_ids().get(0));
        final Observable<MessageImage> serverObservable = mServerMessageSource.getMessageImageInfo(mToken.getAccess_token(),
                directMessage.getAtt_ids().get(0))
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
                })
                .flatMap(new Func1<MessageImage, Observable<LocakImage>>() {
                    @Override
                    public Observable<LocakImage> call(MessageImage messageImage) {
                        return Observable.just(praseLocakImage(messageImage));
                    }
                }).subscribe(new Subscriber<LocakImage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(LocakImage locakImage) {
                        directMessage.setLocakImage(locakImage);
                    }
                });
    }

    private LocakImage praseLocakImage(MessageImage messageImage) {
        LocakImage locakImage =  new LocakImage();
        locakImage.setUrl(messageImage.getThumbnail_600());
        if (messageImage.getThumbnail_600().startsWith("http")) {
            String size  = Uri.parse(messageImage.getThumbnail_600()).getQueryParameter("size");
            String[] strings = size.split(",");
            locakImage.setWidth(Integer.parseInt(strings[0]));
            locakImage.setHeight(Integer.parseInt(strings[1]));
        }else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            File file = new File(URI.create(messageImage.getThumbnail_600()));
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            locakImage.setWidth(options.outWidth);
            locakImage.setHeight(options.outHeight);
        }
        return locakImage;
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
        RxBus.get().unregister(Event.SEND_MESSAGE_RESULT_EVENT, mSendMessageObservable);
        cancelLooperLoadMessage();
    }

    @Override
    public void sendMessage(DirectMessage message) {
        message.setLocal_status(DirectMessage.STATUS_SEND);
        message.setCreated_at(DateUtil.formatCreatetime(System.currentTimeMillis()));
        EventUtil.sendMessage(message);
        mDirectMessages.remove(message);
        mDirectMessages.add(message);
        mDirectMessageView.notifyDataChange();
        mDirectMessageView.attemptSmoothScrollToBottom();
        mLocalMessageSource.saveMessage(message);
    }

    private void send(DirectMessage message) {
        EventUtil.sendMessage(message);
        mDirectMessages.add(message);
        mDirectMessageView.setEntities(mDirectMessages);
        mDirectMessageView.attemptSmoothScrollToBottom();
    }

    @Override
    public void sendTextMessage(final String message) {
        mLocalUserSource.getWeiboUserInfoByUid(mToken.getAccess_token(), Long.parseLong(mToken.getUid()))
                .flatMap(new Func1<User, Observable<DirectMessage>>() {
                    @Override
                    public Observable<DirectMessage> call(User user) {
                        return Observable.just(buildTextMessage(user, mUserId, message));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DirectMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(ChatPresentImp.this, "build TEXT message error" + e.getMessage());
                    }

                    @Override
                    public void onNext(DirectMessage message) {
                        send(message);
                    }
                });
    }

    @Override
    public void sendImageMessage(final ArrayList<String> paths) {
        mLocalUserSource.getWeiboUserInfoByUid(mToken.getAccess_token(), Long.parseLong(mToken.getUid()))
                .flatMap(new Func1<User, Observable<DirectMessage>>() {
                    @Override
                    public Observable<DirectMessage> call(User user) {
                        return Observable.from(buildImagesMessage(user, mUserId, paths));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DirectMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(ChatPresentImp.this, "build image message error" + e.getMessage());
                    }

                    @Override
                    public void onNext(DirectMessage message) {
                        send(message);
                    }
                });

    }

    private DirectMessage buildTextMessage(User self, long recipientId, String text) {
        DirectMessage directMessage = buildMessage(self, recipientId);
        directMessage.setText(text);
        mLocalMessageSource.saveMessage(directMessage);
        return directMessage;
    }

    private List<DirectMessage> buildImagesMessage(User self, long recipientId, List<String>  paths){
        List<DirectMessage> messages = new ArrayList<>(paths.size());
        for (String path : paths) {
            DirectMessage message = buildImageMessage(self, recipientId, path);
            messages.add(message);
            mLocalMessageSource.saveMessage(message);
        }
        return messages;
    }

    private DirectMessage buildImageMessage(User self, long recipientId, String path) {
        DirectMessage directMessage = buildMessage(self, recipientId);
        List<Long> fids = new ArrayList<>(2);
        fids.add(directMessage.getId());
        fids.add(directMessage.getId());
        directMessage.setAtt_ids(fids);
        MessageImage messageImage = createMessageImage(directMessage.getId(), path);
        LocakImage locakImage = praseLocakImage(messageImage);
        directMessage.setLocakImage(locakImage);
        return directMessage;
    }

    private DirectMessage buildMessage(User self, long recipientId) {
        DirectMessage directMessage = new DirectMessage();
        directMessage.setId(System.currentTimeMillis());
        directMessage.setSender(self);
        directMessage.setSender_id(self.getId());
        directMessage.setRecipient_id(recipientId);
        directMessage.setLocal_status(DirectMessage.STATUS_SEND);
        directMessage.setCreated_at(DateUtil.formatCreatetime(System.currentTimeMillis()));
        return directMessage;
    }

    /**
     * 这个必须放子线程
     * @param fid
     * @param path
     */
    private MessageImage createMessageImage(long fid, String path) {
        MessageImage messageImage = new MessageImage();
        messageImage.setFid(fid);
        path = "file://" + path;
        messageImage.setThumbnail_600(path);
        mLocalMessageSource.saveMessageImage(messageImage);
        return messageImage;
    }


}

package com.caij.emore.present.imp;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.caij.emore.EMApplication;
import com.caij.emore.EventTag;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.bean.MessageAttachInfo;
import com.caij.emore.bean.event.MessageResponseEvent;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.manager.MessageManager;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.manager.UserManager;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.ChatPresent;
import com.caij.emore.remote.MessageApi;
import com.caij.emore.ui.view.DirectMessageView;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.caij.emore.utils.weibo.MessageUtil;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/10.
 */
public class ChatPresentImp extends AbsBasePresent implements ChatPresent {

    private static final int PAGE_COUNT = 20;
    private static final long MESSAGE_INTERVAL_TIME = 10 * 1000;

    private static final int MAX_NEW_MESSAGE_PAGE_COUNT = 40;

    private MessageApi mMessageApi;
    private MessageManager mMessageManager;
    private UserManager mUserManager;
    private NotifyManager mNotifyManager;

    private long mToUserId;
    private long mSelfUserId;
    private Token mToken;

    private List<DirectMessage> mDirectMessages;
    private DirectMessageView mDirectMessageView;

    private Observable<MessageResponseEvent> mSendMessageObservable;

    private Timer mLoadMessageTimer;
    private TimerTask mLoadMessageTask;

    public ChatPresentImp(Token token, long toUid, long selfUid,
                          MessageApi messageApi, MessageManager messageManager,
                          UserManager userManager, NotifyManager notifyManager,
                          DirectMessageView directMessageView) {
        super();
        mToken = token;
        mNotifyManager = notifyManager;
        mMessageApi = messageApi;
        mMessageManager = messageManager;
        mUserManager = userManager;
        mDirectMessages = new ArrayList<>();
        mDirectMessageView = directMessageView;
        mToUserId = toUid;
        mSelfUserId = selfUid;
    }

    @Override
    public void userFirstVisible() {

    }


    @Override
    public void onCreate() {
        initEvent();

        getLocalMessage();

        initLooperLoadMessage();
    }

    private void getLocalMessage() {
        Subscription subscription = getLocalMessageObservable(0)
                .subscribe(new SubscriberAdapter<List<DirectMessage>>() {
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
        addSubscription(subscription);
    }

    @Override
    public void loadMore() {
        long maxId = 0;
        if (mDirectMessages.size() > 1) {
            maxId = mDirectMessages.get(0).getId();
        }

        Subscription subscription = getLocalMessageObservable(maxId)
                .subscribe(new SubscriberAdapter<List<DirectMessage>>() {

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
                            mDirectMessageView.notifyItemRangeInserted(mDirectMessages, 0, directMessages.size());
                        }

                        if (directMessages.size() >= PAGE_COUNT - 1) {
                            mDirectMessageView.onLoadComplete(true);
                        }else {
                            mDirectMessageView.onLoadComplete(false);
                        }
                    }
                });
        addSubscription(subscription);
    }

    private Observable<List<DirectMessage>> getLocalMessageObservable(final long maxId) {
        return RxUtil.createDataObservable(new RxUtil.Provider<List<DirectMessage>>() {
            @Override
            public List<DirectMessage> getData() {
                return  mMessageManager.getUserMessage(mToUserId, mSelfUserId, 0, maxId, PAGE_COUNT, 1);
            }
        }).flatMap(new Func1<List<DirectMessage>, Observable<DirectMessage>>() {
            @Override
            public Observable<DirectMessage> call(List<DirectMessage> messages) {
                return Observable.from(messages);
            }
        })
        .filter(new Func1<DirectMessage, Boolean>() {
            @Override
            public Boolean call(DirectMessage directMessage) {
                return !mDirectMessages.contains(directMessage);
            }
        })
        .compose(MessageTransformer.create())
        .toList()
        .doOnNext(new Action1<List<DirectMessage>>() {
            @Override
            public void call(List<DirectMessage> directMessages) {
                Collections.reverse(directMessages);
            }
        })
        .compose(SchedulerTransformer.<List<DirectMessage>>create());
    }

    private void initEvent() {
        mSendMessageObservable = RxBus.getDefault().register(EventTag.EVENT_SEND_MESSAGE_RESULT);
        mSendMessageObservable.compose(new SchedulerTransformer<MessageResponseEvent>())
                .subscribe(new Action1<MessageResponseEvent>() {
                    @Override
                    public void call(MessageResponseEvent messageResponseEvent) {
                        int updatePosition = -1;
                        for (int i = mDirectMessages.size() - 1; i >= 0; i --) {
                            DirectMessage directMessage = mDirectMessages.get(i);
                            if (directMessage.getId() == messageResponseEvent.localMessageId) {
                                updatePosition = i;
                                if (messageResponseEvent.message == null) { //失败
                                    directMessage.setLocal_status(DirectMessage.STATUS_FAIL);
                                }else {
                                    directMessage.setId(messageResponseEvent.message.getId());
                                    directMessage.setLocal_status(DirectMessage.STATUS_SUCCESS);
                                }
                                break;
                            }
                        }
                        if (updatePosition > 0) {
                            mDirectMessageView.notifyItemChanged(mDirectMessages, updatePosition);
                        }
                    }
                });
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
        Subscription subscription = mMessageApi.getChatMessages(mToUserId,
                mSelfUserId, sinceId, 0, MAX_NEW_MESSAGE_PAGE_COUNT, 1)
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
                .compose(MessageTransformer.create())
                .map(new Func1<DirectMessage, DirectMessage>() {
                    @Override
                    public DirectMessage call(DirectMessage message) {
                        mMessageManager.saveMessage(message);
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
                .compose(SchedulerTransformer.<List<DirectMessage>>create())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        if (isShowDialog) {
                            mDirectMessageView.showDialogLoading(false);
                        }
                    }
                })
                .subscribe(new SubscriberAdapter<List<DirectMessage>>() {

                    @Override
                    public void onNext(List<DirectMessage> directMessages) {
                        if (directMessages.size() > 0) {
                            mDirectMessages.addAll(directMessages);
                            mDirectMessageView.setEntities(mDirectMessages);

                            MessageUtil.resetLocalUnReadMessageDisValue(UnReadMessage.TYPE_DM,
                                    directMessages.size(), mNotifyManager);
                        }

                        if (isShowDialog) {
                            mDirectMessageView.toScrollToPosition(mDirectMessages.size());
                        }else {
                            mDirectMessageView.attemptSmoothScrollToBottom();
                        }
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void sendMessage(final DirectMessage message) {
        mDirectMessages.remove(message);
        message.setLocal_status(DirectMessage.STATUS_SEND);
        message.setCreated_at(new Date(System.currentTimeMillis()));
        mDirectMessages.add(message);
        mDirectMessageView.setEntities(mDirectMessages);
        mDirectMessageView.attemptSmoothScrollToBottom();

        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                mMessageManager.saveMessage(message);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                MessageResponseEvent responseEvent = new MessageResponseEvent(EventTag.SEND_MESSAGE_EVENT, message.getId(), message);
                RxBus.getDefault().post(EventTag.SEND_MESSAGE_EVENT, responseEvent);
            }
        });

    }

    @Override
    public String getMessageImageHdUrl(DirectMessage directMessage) {
        String url = directMessage.getAttachinfo().get(0).getThumbnail();
        return appImageUrl(url, DensityUtil.getScreenWidth(EMApplication.getInstance()),
                DensityUtil.getScreenHeight(EMApplication.getInstance()));
    }

    private void send(DirectMessage message) {
        MessageResponseEvent responseEvent = new MessageResponseEvent(EventTag.SEND_MESSAGE_EVENT, message.getId(), message);
        RxBus.getDefault().post(EventTag.SEND_MESSAGE_EVENT, responseEvent);

        mDirectMessages.add(message);
        mDirectMessageView.notifyItemRangeInserted(mDirectMessages, mDirectMessages.size() - 1, 1);
        mDirectMessageView.attemptSmoothScrollToBottom();
    }

    @Override
    public void sendTextMessage(final String message) {
        RxUtil.createDataObservable(new RxUtil.Provider<DirectMessage>() {
            @Override
            public DirectMessage getData() {
                User user = mUserManager.getUserByUid(Long.parseLong(mToken.getUid()));
                return buildTextMessage(user, mToUserId, message);
            }
        }).doOnNext(new Action1<DirectMessage>() {
            @Override
            public void call(DirectMessage message) {
                mMessageManager.saveMessage(message);
                message.setTextContentSpannable(SpannableStringUtil.paraeSpannable(message));
            }
        })
        .compose(SchedulerTransformer.<DirectMessage>create())
        .subscribe(new SubscriberAdapter<DirectMessage>() {

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
        RxUtil.createDataObservable(new RxUtil.Provider<List<DirectMessage>>() {
            @Override
            public List<DirectMessage> getData() {
                User user = mUserManager.getUserByUid(Long.parseLong(mToken.getUid()));
                return buildImagesMessage(user, mToUserId, paths);
            }
        }).flatMap(new Func1<List<DirectMessage>, Observable<DirectMessage>>() {
            @Override
            public Observable<DirectMessage> call(List<DirectMessage> directMessages) {
                return Observable.from(directMessages);
            }
        })
        .doOnNext(new Action1<DirectMessage>() {
            @Override
            public void call(DirectMessage message) {
                mMessageManager.saveMessage(message);
                message.setTextContentSpannable(SpannableStringUtil.paraeSpannable(message));
            }
        })
        .compose(SchedulerTransformer.<DirectMessage>create())
        .subscribe(new SubscriberAdapter<DirectMessage>() {

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
        return directMessage;
    }

    private List<DirectMessage> buildImagesMessage(User self, long recipientId, List<String>  paths){
        List<DirectMessage> messages = new ArrayList<>(paths.size());
        for (String path : paths) {
            DirectMessage message = buildImageMessage(self, recipientId, path);
            messages.add(message);
        }
        return messages;
    }

    private DirectMessage buildImageMessage(User self, long recipientId, String path) {
        DirectMessage directMessage = buildMessage(self, recipientId);
        directMessage.setText("分享图片");
        List<Long> fids = new ArrayList<>(2);
        fids.add(directMessage.getId());
        fids.add(directMessage.getId());
        directMessage.setAtt_ids(fids);
        MessageAttachInfo messageAttachInfo  = new MessageAttachInfo();
        messageAttachInfo.setThumbnail("file://" + path);
        List<MessageAttachInfo> attachInfos = new ArrayList<>();
        attachInfos.add(messageAttachInfo);
        ImageInfo imageInfo = praseLocakImage(messageAttachInfo);
        directMessage.setAttachinfo(attachInfos);
        directMessage.setImageInfo(imageInfo);
        return directMessage;
    }

    private DirectMessage buildMessage(User self, long recipientId) {
        DirectMessage directMessage = new DirectMessage();
        directMessage.setId(System.currentTimeMillis());
        directMessage.setSender(self);
        directMessage.setSender_id(self.getId());
        directMessage.setRecipient_id(recipientId);
        directMessage.setLocal_status(DirectMessage.STATUS_SEND);
        directMessage.setCreated_at(new Date(System.currentTimeMillis()));
        return directMessage;
    }


    private static ImageInfo praseLocakImage(MessageAttachInfo messageImage) {
        String url = messageImage.getThumbnail();
        if (url.startsWith("http")) {
            String size  = Uri.parse(url).getQueryParameter("size");
            String[] strings = size.split(",");
            return toImageInfo(url, Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
        }else {
            File file = new File(URI.create(url));
            BitmapFactory.Options options = ImageUtil.getImageOptions(file);
            return toImageInfo(url, options.outWidth, options.outHeight);
        }
    }

    public static ImageInfo toImageInfo(String url, int width, int height) {
        ImageInfo imageInfo = new ImageInfo();
        if (width > height) {
            int maxWidth = (int) (DensityUtil.getScreenWidth(EMApplication.getInstance()) / 2f);
            imageInfo.setWidth(maxWidth);
            imageInfo.setHeight((int) (maxWidth * 1f / width * height));
        }else {
            int maxWidth = (int) (DensityUtil.getScreenWidth(EMApplication.getInstance()) / 3f);
            imageInfo.setWidth(maxWidth);
            imageInfo.setHeight((int) (maxWidth * 1f / width * height));
        }
        imageInfo.setUrl(appImageUrl(url, imageInfo.getWidth(), imageInfo.getHeight()));
        return imageInfo;
    }


    private static String appImageUrl(String url, int width, int height) {
        if (url.startsWith("http")) {
            Uri uri = Uri.parse(url);
            String v = uri.getScheme() + "://" + uri.getHost() + uri.getPath() + "?" + "fid=" + uri.getQueryParameter("fid") + "&width=" + width + "&high=" + height
                    + "&access_token=" + UserPrefs.get(EMApplication.getInstance()).getToken().getAccess_token();
            return v;
        }
        return url;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_SEND_MESSAGE_RESULT, mSendMessageObservable);
        cancelLooperLoadMessage();
    }

    static class MessageTransformer implements Observable.Transformer<DirectMessage, DirectMessage> {

        @Override
        public Observable<DirectMessage> call(Observable<DirectMessage> directMessageObservable) {
            return directMessageObservable.doOnNext(new Action1<DirectMessage>() {
                @Override
                public void call(DirectMessage message) {
                    message.setTextContentSpannable(SpannableStringUtil.paraeSpannable(message));

                    if (message.getAttachinfo() != null && message.getAttachinfo().size() > 0) {
                        ImageInfo imageInfo = praseLocakImage(message.getAttachinfo().get(0));
                        message.setImageInfo(imageInfo);

                    }

                    //这里是5分钟还没有发送出去 认为发送失败
                    if (message.getLocal_status() == DirectMessage.STATUS_SEND
                            && System.currentTimeMillis() - message.getCreated_at().getTime() > 5 * 60 * 1000) {
                        message.setLocal_status(DirectMessage.STATUS_FAIL);
                    }

                }
            });
        }

        public static MessageTransformer create() {
            return new MessageTransformer();
        }
    }

}

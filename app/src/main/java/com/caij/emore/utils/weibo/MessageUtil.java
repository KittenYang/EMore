package com.caij.emore.utils.weibo;

import com.caij.emore.AppApplication;
import com.caij.emore.EventTag;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.response.Response;
import com.caij.emore.dao.NotifyManager;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.remote.UnReadMessageApi;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/24.
 */
public class MessageUtil {

    public static void resetUnReadMessage(final String type, final long uid, UnReadMessageApi unReadMessageApi,
                                          final NotifyManager notifyManager) {
        unReadMessageApi.resetUnReadMessage(uid, type, 0)
                .compose(ErrorCheckerTransformer.create())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        notifyManager.resetUnReadMessage(uid, type, 0);
                    }
                })
                .flatMap(new Func1<Response, Observable<UnReadMessage>>() {
                    @Override
                    public Observable<UnReadMessage> call(Response response) {
                        return Observable.create(new Observable.OnSubscribe<UnReadMessage>() {
                            @Override
                            public void call(Subscriber<? super UnReadMessage> subscriber) {
                                subscriber.onNext(notifyManager.getUnReadMessage(uid));
                            }
                        });
                    }
                })
                .compose(SchedulerTransformer.<UnReadMessage>create())
                .subscribe(new SubscriberAdapter<UnReadMessage>() {
                    @Override
                    public void onNext(UnReadMessage unReadMessage) {
                        RxBus.getDefault().post(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE, unReadMessage);
                    }
                });
    }

    public static void resetLocalUnReadMessage(final String type, final int value, final long uid, final NotifyManager notifyManager) {
        Observable.create(new Observable.OnSubscribe<UnReadMessage>() {
            @Override
            public void call(Subscriber<? super UnReadMessage> subscriber) {
                notifyManager.resetUnReadMessage(uid, type, value);
                subscriber.onNext(notifyManager.getUnReadMessage(uid));
                subscriber.onCompleted();
            }
        }).compose(SchedulerTransformer.<UnReadMessage>create())
        .subscribe(new SubscriberAdapter<UnReadMessage>() {
            @Override
            public void onNext(UnReadMessage unReadMessage) {
                if (unReadMessage != null) {
                    RxBus.getDefault().post(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE, unReadMessage);
                }
            }
        });
    }

    public static void resetLocalUnReadMessageDisValue(final String token, final String type, final int disValue, final MessageSource localMessageSource) {
        long uid  = Long.parseLong(UserPrefs.get(AppApplication.getInstance()).getToken().getUid());
        localMessageSource.getUnReadMessage(token, uid)
                .doOnNext(new Action1<UnReadMessage>() {
                    @Override
                    public void call(UnReadMessage unReadMessage) {
                        if (type.equals(UnReadMessage.TYPE_MENTION_STATUS)) {
                            unReadMessage.setMention_status(unReadMessage.getMention_status() - disValue);
                        }else if (type.equals(UnReadMessage.TYPE_MENTION_CMT)) {
                            unReadMessage.setMention_cmt(unReadMessage.getMention_cmt() - disValue);
                        }else if (type.equals(UnReadMessage.TYPE_CMT)) {
                            unReadMessage.setCmt(unReadMessage.getCmt() - disValue);
                        }else if (type.equals(UnReadMessage.TYPE_STATUS)) {
                            unReadMessage.setStatus(unReadMessage.getStatus() - disValue);
                        }else if (type.equals(UnReadMessage.TYPE_DM)) {
                            unReadMessage.setDm_single(unReadMessage.getDm_single() - disValue);
                        }else if (type.equals(UnReadMessage.TYPE_ATTITUDE)) {
                            unReadMessage.setAttitude(unReadMessage.getAttitude() - disValue);
                        }else if (type.equals(UnReadMessage.TYPE_FOLLOWER)) {
                            unReadMessage.setFollower(unReadMessage.getFollower() - disValue);
                        }

                        localMessageSource.saveUnReadMessage(unReadMessage);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UnReadMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UnReadMessage unReadMessage) {
                        RxBus.getDefault().post(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE, unReadMessage);
                    }
                });
    }
}

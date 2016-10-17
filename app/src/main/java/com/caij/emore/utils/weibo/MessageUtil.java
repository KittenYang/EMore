package com.caij.emore.utils.weibo;

import com.caij.emore.EMoreApplication;
import com.caij.emore.EventTag;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.response.Response;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.remote.NotifyApi;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
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

    public static void resetUnReadMessage(final String type, final long uid, NotifyApi notifyApi,
                                          final NotifyManager notifyManager) {
        notifyApi.resetUnReadMessage(uid, type, 0)
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

    public static void resetLocalUnReadMessageDisValue(final String type, final int disValue, final NotifyManager notifyManager) {
        final long uid  = Long.parseLong(UserPrefs.get(EMoreApplication.getInstance()).getToken().getUid());
        RxUtil.createDataObservable(new RxUtil.Provider<UnReadMessage>() {
            @Override
            public UnReadMessage getData() {
                return notifyManager.getUnReadMessage(uid);
            }
        }).doOnNext(new Action1<UnReadMessage>() {
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

                        notifyManager.saveUnReadMessage(unReadMessage);
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

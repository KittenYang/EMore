package com.caij.emore.utils.weibo;

import android.os.AsyncTask;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;

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

    public static void resetUnReadMessage(final String token, String type,
                                          MessageSource serverMessageSource, final MessageSource localMessageSource) {
        Observable<Response> serverObservable = serverMessageSource.resetUnReadMessage(token,
                Key.WEICO_APP_ID, Key.WEICO_APP_FROM, type, 0);
        Observable<Response> localObservable = localMessageSource.resetUnReadMessage(token,
                Key.WEICO_APP_ID, Key.WEICO_APP_FROM, type, 0);
        final long uid = Long.parseLong(UserPrefs.get().getEMoreToken().getUid());
        Observable.concat(serverObservable, localObservable)
                .filter(new Func1<Response, Boolean>() {
                    @Override
                    public Boolean call(Response response) {
                        return response != null;
                    }
                })
                .flatMap(new Func1<Response, Observable<UnReadMessage>>() {
                    @Override
                    public Observable<UnReadMessage> call(Response response) {
                        return localMessageSource.getUnReadMessage(token, uid);
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
                        RxBus.get().post(Key.EVENT_UNREAD_MESSAGE_COMPLETE, unReadMessage);
                    }
                });
    }

    public static void resetLocalUnReadMessage(final String token, String type, int value, final MessageSource localMessageSource) {
        Observable<Response> localObservable = localMessageSource.resetUnReadMessage(token,
                Key.WEICO_APP_ID, Key.WEICO_APP_FROM, type, value);
        final long uid = Long.parseLong(UserPrefs.get().getEMoreToken().getUid());
        localObservable.flatMap(new Func1<Response, Observable<UnReadMessage>>() {
                    @Override
                    public Observable<UnReadMessage> call(Response response) {
                        return localMessageSource.getUnReadMessage(token, uid);
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
                        LogUtil.d("resetLocalUnReadMessage", e.getMessage());
                    }

                    @Override
                    public void onNext(UnReadMessage unReadMessage) {
                        if (unReadMessage != null) {
                            RxBus.get().post(Key.EVENT_UNREAD_MESSAGE_COMPLETE, unReadMessage);
                        }
                    }
                });
    }

    public static void resetLocalUnReadMessageDisValue(final String token, final String type, final int disValue, final MessageSource localMessageSource) {
        long uid  = Long.parseLong(UserPrefs.get().getEMoreToken().getUid());
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
                        RxBus.get().post(Key.EVENT_UNREAD_MESSAGE_COMPLETE, unReadMessage);
                    }
                });
    }
}

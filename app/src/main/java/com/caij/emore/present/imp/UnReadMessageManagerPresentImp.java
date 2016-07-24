package com.caij.emore.present.imp;

import android.os.AsyncTask;

import com.caij.emore.AppApplication;
import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.UnReadMessageManagerPresent;
import com.caij.emore.present.view.UnReadMessageManagerPresentView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxbus.RxBus;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/23.
 */
public class UnReadMessageManagerPresentImp implements UnReadMessageManagerPresent {

    private Observable<Object> mIntervalMillisUpdateObservable;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private CompositeSubscription mCompositeSubscription;
    private UnReadMessageManagerPresentView mView;

    public UnReadMessageManagerPresentImp(MessageSource serverMessageSource,
                                          MessageSource localMessageSource, UnReadMessageManagerPresentView view) {
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mView = view;
    }

    @Override
    public void onCreate() {
        mCompositeSubscription = new CompositeSubscription();
        mIntervalMillisUpdateObservable = EventUtil.registIntervalMillisUpdateEvent();
        mServerMessageSource = new ServerMessageSource();
        mIntervalMillisUpdateObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        mView.onIntervalMillisUpdate();
                    }
                });
    }

    @Override
    public void onDestroy() {
        EventUtil.unregistIntervalMillisUpdateEvent(mIntervalMillisUpdateObservable);
        mCompositeSubscription.clear();
    }

    @Override
    public void loadUnReadMessage() {
        if (!SystemUtil.isNetworkAvailable(AppApplication.getInstance())) {
            return;
        }
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        if (accessToken != null) {
            mServerMessageSource.getUnReadMessage(accessToken.getAccess_token(), Long.parseLong(accessToken.getUid()))
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
                        public void onNext(UnReadMessage unreadMessage) {
                            notifyMessage(unreadMessage);
                            RxBus.get().post(Key.EVENT_UNREAD_MESSAGE_COMPLETE, unreadMessage);
                        }
                    });
        }
    }

    private void notifyMessage(final UnReadMessage serverUnReadMessage) {
        final AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        mLocalMessageSource.getUnReadMessage(null, Long.parseLong(accessToken.getUid()))
                .doOnNext(new Action1<UnReadMessage>() {
                    @Override
                    public void call(UnReadMessage unReadMessage) {
                        serverUnReadMessage.setUid(Long.parseLong(accessToken.getUid()));
                        mLocalMessageSource.saveUnReadMessage(serverUnReadMessage);
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
                        LogUtil.d("notifyMessage", e.getMessage());
                    }

                    @Override
                    public void onNext(UnReadMessage localUnReadMessage) {
                        mView.notifyMessage(serverUnReadMessage, localUnReadMessage);
                    }
                });
    }
}

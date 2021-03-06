package com.caij.emore.present.imp;

import com.caij.emore.EMApplication;
import com.caij.emore.EventTag;
import com.caij.emore.account.Token;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.present.UnReadMessageManagerPresent;
import com.caij.emore.remote.NotifyApi;
import com.caij.emore.ui.view.UnReadMessageManagerPresentView;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/7/23.
 */
public class UnReadMessageManagerPresentImp extends AbsBasePresent implements UnReadMessageManagerPresent {

    private Observable<Object> mIntervalMillisUpdateObservable;
    private NotifyApi mNotifyApi;
    private NotifyManager mNotifyManager;
    private UnReadMessageManagerPresentView mView;
    private Token mToken;

    public UnReadMessageManagerPresentImp(Token token, NotifyApi notifyApi,
                                          NotifyManager notifyManager, UnReadMessageManagerPresentView view) {
        mToken = token;
        mNotifyApi = notifyApi;
        mNotifyManager = notifyManager;
        mView = view;
    }

    @Override
    public void onCreate() {
        mIntervalMillisUpdateObservable = RxBus.getDefault().register(EventTag.INTERVAL_MILLIS_UPDATE);
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
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.INTERVAL_MILLIS_UPDATE, mIntervalMillisUpdateObservable);
    }

    @Override
    public void loadUnReadMessage() {
        if (!SystemUtil.isNetworkAvailable(EMApplication.getInstance())) return;
        if (mToken != null) {
            Subscription subscription = mNotifyApi.getUnReadMessage(Long.parseLong(mToken.getUid()))
                    .compose(new SchedulerTransformer<UnReadMessage>())
                    .subscribe(new ResponseSubscriber<UnReadMessage>(mView) {

                        @Override
                        protected void onFail(Throwable e) {

                        }

                        @Override
                        public void onNext(UnReadMessage unreadMessage) {
                            notifyMessage(unreadMessage);
                            RxBus.getDefault().post(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE, unreadMessage);
                        }
                    });
            addSubscription(subscription);
        }
    }

    private void notifyMessage(final UnReadMessage serverUnReadMessage) {
        RxUtil.createDataObservable(new RxUtil.Provider<UnReadMessage>() {
            @Override
            public UnReadMessage getData() {
                return  mNotifyManager.getUnReadMessage(Long.parseLong(mToken.getUid()));
            }
        }).doOnNext(new Action1<UnReadMessage>() {
                    @Override
                    public void call(UnReadMessage unReadMessage) {
                        serverUnReadMessage.setUid(Long.parseLong(mToken.getUid()));
                        mNotifyManager.saveUnReadMessage(serverUnReadMessage);
                    }
                })
                .compose(new SchedulerTransformer<UnReadMessage>())
                .subscribe(new SubscriberAdapter<UnReadMessage>() {
                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d(UnReadMessageManagerPresentImp.this, e.getMessage());
                    }

                    @Override
                    public void onNext(UnReadMessage localUnReadMessage) {
                        mView.notifyMessage(serverUnReadMessage, localUnReadMessage);

                        if (localUnReadMessage == null ||
                                (serverUnReadMessage.getDm_single() - localUnReadMessage.getDm_single() > 0)) {
                            RxBus.getDefault().post(EventTag.EVENT_HAS_NEW_DM, serverUnReadMessage);
                        }
                    }
                });
    }
}

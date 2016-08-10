package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.MainPresent;
import com.caij.emore.present.view.MainView;
import com.caij.emore.source.DraftSource;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.UserSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/6/3.
 */
public class MainPresentImp implements MainPresent {

    private final CompositeSubscription mCompositeSubscription;
    private MainView mUserView;
    private UserSource mServerUserSource;
    private UserSource mLocalUserSource;
    private String mToken;
    private long mUid;
    private MessageSource mLocalMessageSource;
    private DraftSource mDraftSource;
    private Observable<UnReadMessage> mUnReadMessageObservable;
    private Observable<Draft> mDraftObservable;

    public MainPresentImp(String token, long uid, MainView userView,
                          UserSource serverUserSource, UserSource localUserSource,
                          MessageSource localMessageSource,
                          DraftSource draftSource) {
        mUserView = userView;
        mServerUserSource = serverUserSource;
        mLocalUserSource = localUserSource;
        mToken = token;
        mUid = uid;
        mLocalMessageSource = localMessageSource;
        mDraftSource = draftSource;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {
        Subscription subscription = mLocalMessageSource.getUnReadMessage(mToken, mUid)
                .compose(new SchedulerTransformer<UnReadMessage>())
                .subscribe(new Subscriber<UnReadMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.d("onError", e.getMessage());
                    }

                    @Override
                    public void onNext(UnReadMessage unReadMessage) {
                        if (unReadMessage != null) {
                            mUserView.setUnReadMessage(unReadMessage);
                        }
                    }
                });
        mCompositeSubscription.add(subscription);

        mUnReadMessageObservable = RxBus.getDefault().register(Event.EVENT_UNREAD_MESSAGE_COMPLETE);
        mUnReadMessageObservable.subscribe(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage unReadMessage) {
                if (unReadMessage != null) {
                    mUserView.setUnReadMessage(unReadMessage);
                }
            }
        });

        mDraftObservable = RxBus.getDefault().register(Event.EVENT_DRAFT_UPDATE);
        mDraftObservable.subscribe(new Action1<Draft>() {
            @Override
            public void call(Draft draft) {
                loadDrafts();
            }
        });

        loadDrafts();
    }

    private void loadDrafts() {
        mDraftSource.getDraftsCount()
                .compose(new SchedulerTransformer<Integer>())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        mUserView.setDraftCount(integer);
                    }
                });
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
        RxBus.getDefault().unregister(Event.EVENT_UNREAD_MESSAGE_COMPLETE, mUnReadMessageObservable);
        RxBus.getDefault().unregister(Event.EVENT_DRAFT_UPDATE, mDraftObservable);
    }

    @Override
    public void getWeiboUserInfoByUid() {
        Observable<User> localObservable =  mLocalUserSource.getWeiboUserInfoByUid(mToken, mUid);
        Observable<User> serverObservable =  mServerUserSource.getWeiboUserInfoByUid(mToken, mUid);
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null && System.currentTimeMillis() - user.getUpdate_time() < 60 * 60 * 1000;
                    }
                })
                .compose(new SchedulerTransformer<User>())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        mCompositeSubscription.add(subscription);
    }
}

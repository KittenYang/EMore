package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.MainPresent;
import com.caij.emore.present.view.MainView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.UserSource;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
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
    private Observable<UnReadMessage> mUnReadMessageObservable;

    public MainPresentImp(String token, long uid, MainView userView,
                          UserSource serverUserSource, UserSource localUserSource,
                          MessageSource localMessageSource) {
        mUserView = userView;
        mServerUserSource = serverUserSource;
        mLocalUserSource = localUserSource;
        mToken = token;
        mUid = uid;
        mLocalMessageSource = localMessageSource;
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {
        Subscription subscription = mLocalMessageSource.getUnReadMessage(mToken, mUid)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

        mUnReadMessageObservable = RxBus.get().register(Key.EVENT_UNREAD_MESSAGE_COMPLETE);
        mUnReadMessageObservable.subscribe(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage unReadMessage) {
                mUserView.setUnReadMessage(unReadMessage);
            }
        });
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
        RxBus.get().unregister(Key.EVENT_UNREAD_MESSAGE_COMPLETE, mUnReadMessageObservable);
    }

    @Override
    public void getWeiboUserInfoByUid() {
        Observable<User> localObservable =   mLocalUserSource.getWeiboUserInfoByUid(mToken, mUid);
        Observable<User> serverObservable =   mServerUserSource.getWeiboUserInfoByUid(mToken, mUid);
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null && System.currentTimeMillis() - user.getUpdate_time() < 60 * 60 * 1000;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

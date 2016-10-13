package com.caij.emore.present.imp;


import com.caij.emore.EventTag;
import com.caij.emore.manager.DraftManager;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.manager.UserManager;
import com.caij.emore.database.bean.Draft;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.MainPresent;
import com.caij.emore.remote.NotifyApi;
import com.caij.emore.remote.UserApi;
import com.caij.emore.ui.view.MainView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/6/3.
 */
public class MainPresentImp extends AbsBasePresent implements MainPresent {

    private static final long USER_CACHE_TIME =  5 * 60 * 60 * 1000;

    private MainView mUserView;

    private long mUid;

    private UserApi mUserApi;
    private UserManager mUserManager;
    private NotifyManager mNotifyManager;
    private DraftManager mDraftManager;

    private Observable<UnReadMessage> mUnReadMessageObservable;
    private Observable<Draft> mDraftObservable;
    private Observable<Boolean> mModeNightUpdate;

    public MainPresentImp(long uid, MainView userView, UserApi userApi, UserManager userManager, NotifyManager notifyManager,
                          DraftManager draftManager) {
        mUid = uid;
        mUserView = userView;
        mUserApi = userApi;
        mUserManager = userManager;
        mNotifyManager = notifyManager;
        mDraftManager = draftManager;
    }

    @Override
    public void onCreate() {
        mUnReadMessageObservable = RxBus.getDefault().register(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE);
        mUnReadMessageObservable.subscribe(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage unReadMessage) {
                if (unReadMessage != null) {
                    mUserView.setUnReadMessage(unReadMessage);
                }
            }
        });

        mDraftObservable = RxBus.getDefault().register(EventTag.EVENT_DRAFT_UPDATE);
        mDraftObservable.subscribe(new Action1<Draft>() {
            @Override
            public void call(Draft draft) {
                loadDrafts();
            }
        });

        mModeNightUpdate = RxBus.getDefault().register(EventTag.EVENT_MODE_NIGHT_UPDATE);
        mModeNightUpdate.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                mUserView.updateTheme();
            }
        });
    }

    @Override
    public void getUserInfoByUid() {
        Observable<User> serverObservable = mUserApi.getWeiboUserByUid(mUid)
                .doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        mUserManager.saveUser(user);
                    }
                });
        Observable<User> localObservable = RxUtil.createDataObservable(new RxUtil.Provider<User>() {
            @Override
            public User getData() {
                return mUserManager.getUserByUid(mUid);
            }
        });
        Subscription subscription = Observable.concat(localObservable, serverObservable)
                .first(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return user != null && System.currentTimeMillis() -
                                user.getUpdate_time() < USER_CACHE_TIME;
                    }
                })
                .compose(SchedulerTransformer.<User>create())
                .subscribe(new SubscriberAdapter<User>() {
                    @Override
                    public void onNext(User user) {
                        mUserView.setUser(user);
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void getNotifyInfo() {
        loadMessageCount();
        loadDrafts();
    }

    private void loadDrafts() {
        RxUtil.createDataObservable(new RxUtil.Provider<Long>() {
            @Override
            public Long getData() {
                return mDraftManager.getDraftsCount();
            }
        })
        .compose(SchedulerTransformer.<Long>create())
        .subscribe(new SubscriberAdapter<Long>() {
            @Override
            public void onNext(Long integer) {
                mUserView.setDraftCount(integer.intValue());
            }
        });
    }

    private void loadMessageCount() {
        Subscription subscription =  RxUtil.createDataObservable(new RxUtil.Provider<UnReadMessage>() {
            @Override
            public UnReadMessage getData() {
                return mNotifyManager.getUnReadMessage(mUid);
            }
        }) .compose(SchedulerTransformer.<UnReadMessage>create())
                .subscribe(new SubscriberAdapter<UnReadMessage>() {
                    @Override
                    public void onNext(UnReadMessage unReadMessage) {
                        if (unReadMessage != null) {
                            mUserView.setUnReadMessage(unReadMessage);
                        }
                    }
                });
        addSubscription(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE, mUnReadMessageObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_DRAFT_UPDATE, mDraftObservable);
        RxBus.getDefault().unregister(EventTag.EVENT_MODE_NIGHT_UPDATE, mModeNightUpdate);
    }
}

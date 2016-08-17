package com.caij.emore.present.imp;

import com.caij.emore.Event;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.ui.view.MessageUserView;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

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
public class MessageUserPresentImp implements MessageUserPresent {

    private final static int PAGE_COUNT = 20;

    private CompositeSubscription mCompositeSubscription;
    private String mToken;
    private MessageSource mServerMessageSource;
    private MessageSource mLocalMessageSource;
    private List<MessageUser.UserListBean> mUserListBeens;
    private MessageUserView mMessageUserView;
    private MessageUser mMessageUser;
    private Observable<UnReadMessage> mUnReadMessageObservable;

    public MessageUserPresentImp(String token, MessageSource serverMessageSource,
                                 MessageSource localMessageSource, MessageUserView view) {
        mServerMessageSource = serverMessageSource;
        mToken = token;
        mLocalMessageSource = localMessageSource;
        mMessageUserView = view;
        mUserListBeens = new ArrayList<>();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void refresh() {
        Subscription subscription = createMessageObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<MessageUser.UserListBean>>(mMessageUserView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mMessageUserView.onRefreshComplete();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<MessageUser.UserListBean> userListBeen) {
                        mUserListBeens.clear();
                        mUserListBeens.addAll(userListBeen);
                        mMessageUserView.setEntities(mUserListBeens);
                        mMessageUserView.onLoadComplete(userListBeen.size() >= PAGE_COUNT - 1);
                        mMessageUserView.onRefreshComplete();
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        long cursor = mMessageUser == null ? 0 : mMessageUser.getNext_cursor();
        Subscription subscription = createMessageObservable(cursor, false)
                .subscribe(new DefaultResponseSubscriber<List<MessageUser.UserListBean>>(mMessageUserView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mMessageUserView.onLoadComplete(true);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<MessageUser.UserListBean> userListBeen) {
                        mUserListBeens.addAll(userListBeen);
                        mMessageUserView.notifyItemRangeInserted(mUserListBeens,
                                mUserListBeens.size() -  userListBeen.size(), userListBeen.size());
                        mMessageUserView.onLoadComplete(userListBeen.size() >= PAGE_COUNT - 1);
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onCreate() {
        final String uid  = UserPrefs.get().getWeiCoToken().getUid();
        mLocalMessageSource.getUnReadMessage(mToken, Long.parseLong(uid))
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
                        mMessageUserView.onLoadUnReadMessageSuccess(unReadMessage);
                    }
                });

        mUnReadMessageObservable = RxBus.getDefault().register(Event.EVENT_UNREAD_MESSAGE_COMPLETE);
        mUnReadMessageObservable.subscribe(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage unReadMessage) {
                mMessageUserView.onLoadUnReadMessageSuccess(unReadMessage);
            }
        });
    }

    private Observable<List<MessageUser.UserListBean>> createMessageObservable(long next_cursor, final boolean isRefresh) {
        return mServerMessageSource.getMessageUserList(mToken, PAGE_COUNT, next_cursor)
                .compose(new ErrorCheckerTransformer<MessageUser>())
                .flatMap(new Func1<MessageUser, Observable<MessageUser.UserListBean>>() {
                    @Override
                    public Observable<MessageUser.UserListBean> call(MessageUser messageUser) {
                        mMessageUser = messageUser;
                        return Observable.from(messageUser.getUser_list());
                    }
                })
                .filter(new Func1<MessageUser.UserListBean, Boolean>() {
                    @Override
                    public Boolean call(MessageUser.UserListBean userListBean) {
                        return !mUserListBeens.contains(userListBean) || isRefresh;
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<MessageUser.UserListBean>>());
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
        RxBus.getDefault().unregister(Event.EVENT_UNREAD_MESSAGE_COMPLETE, mUnReadMessageObservable);
    }
}

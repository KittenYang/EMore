package com.caij.emore.present.imp;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.present.view.MessageUserView;
import com.caij.emore.source.DefaultResponseSubscriber;
import com.caij.emore.source.MessageSource;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    private List<MessageUser.UserListBean> userListBeens;
    private MessageUserView mMessageUserView;
    private MessageUser mMessageUser;
    private Observable<UnReadMessage> mUnReadMessageObservable;

    public MessageUserPresentImp(String token, MessageSource serverMessageSource,
                                 MessageSource localMessageSource, MessageUserView view) {
        mServerMessageSource = serverMessageSource;
        mToken = token;
        mLocalMessageSource = localMessageSource;
        mMessageUserView = view;
        userListBeens = new ArrayList<>();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void refresh() {
        Subscription subscription = mServerMessageSource.getMessageUserList(mToken, PAGE_COUNT, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<MessageUser>(mMessageUserView) {

                    @Override
                    public void onCompleted() {
                        mMessageUserView.onRefreshComplete();

                    }

                    @Override
                    public void onNext(MessageUser messageUser) {
                        mMessageUser = messageUser;
                        userListBeens.clear();
                        userListBeens.addAll(messageUser.getUser_list());
                        mMessageUserView.setEntities(userListBeens);
                        mMessageUserView.onLoadComplete(messageUser.getUser_list().size() >= PAGE_COUNT - 1);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mMessageUserView.onRefreshComplete();
                        mMessageUserView.onDefaultLoadError();
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
        Subscription subscription = mServerMessageSource.getMessageUserList(mToken, PAGE_COUNT, cursor)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultResponseSubscriber<MessageUser>(mMessageUserView) {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(MessageUser messageUser) {
                        mMessageUser = messageUser;
                        userListBeens.addAll(messageUser.getUser_list());
                        mMessageUserView.setEntities(userListBeens);
                        mMessageUserView.onLoadComplete(messageUser.getUser_list().size() >= PAGE_COUNT - 1);
                    }

                    @Override
                    protected void onFail(Throwable e) {
                        mMessageUserView.onDefaultLoadError();
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

        mUnReadMessageObservable = RxBus.get().register(Key.EVENT_UNREAD_MESSAGE_COMPLETE);
        mUnReadMessageObservable.subscribe(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage unReadMessage) {
                mMessageUserView.onLoadUnReadMessageSuccess(unReadMessage);
            }
        });
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
        RxBus.get().unregister(Key.EVENT_UNREAD_MESSAGE_COMPLETE, mUnReadMessageObservable);
    }
}

package com.caij.emore.present.imp;

import com.caij.emore.EventTag;
import com.caij.emore.api.ex.ResponseSubscriber;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.manager.NotifyManager;
import com.caij.emore.present.MessageUserPresent;
import com.caij.emore.remote.MessageApi;
import com.caij.emore.ui.view.MessageUserView;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.api.ex.ErrorCheckerTransformer;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/7/10.
 */
public class MessageUserPresentImp extends AbsBasePresent implements MessageUserPresent {

    private final static int PAGE_COUNT = 20;

    private String mToken;
    private long mUid;

    private MessageApi mMessageApi;
    private NotifyManager mNotifyManager;

    private MessageUserView mMessageUserView;

    private List<MessageUser.UserListBean> mUserListBeens;
    private MessageUser mMessageUser;

    private Observable<UnReadMessage> mUnReadMessageObservable;


    public MessageUserPresentImp(String token, long uid, MessageApi messageApi, NotifyManager notifyManager,
                                 MessageUserView view) {
        mMessageApi = messageApi;
        mToken = token;
        mUid = uid;
        mMessageUserView = view;
        mNotifyManager = notifyManager;
        mUserListBeens = new ArrayList<>();
    }

    @Override
    public void refresh() {
        Subscription subscription = createMessageObservable(0, true)
                .subscribe(new ResponseSubscriber<List<MessageUser.UserListBean>>(mMessageUserView) {
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
        addSubscription(subscription);
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {
        long cursor = mMessageUser == null ? 0 : mMessageUser.getNext_cursor();
        Subscription subscription = createMessageObservable(cursor, false)
                .subscribe(new ResponseSubscriber<List<MessageUser.UserListBean>>(mMessageUserView) {
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
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<UnReadMessage>() {
            @Override
            public UnReadMessage getData() {
                return  mNotifyManager.getUnReadMessage(mUid);
            }
        })
        .compose(SchedulerTransformer.<UnReadMessage>create())
        .subscribe(new SubscriberAdapter<UnReadMessage>() {


            @Override
            public void onNext(UnReadMessage unReadMessage) {
                mMessageUserView.onLoadUnReadMessageSuccess(unReadMessage);
            }
        });

        addSubscription(subscription);

        mUnReadMessageObservable = RxBus.getDefault().register(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE);
        mUnReadMessageObservable.subscribe(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage unReadMessage) {
                mMessageUserView.onLoadUnReadMessageSuccess(unReadMessage);
            }
        });
    }

    private Observable<List<MessageUser.UserListBean>> createMessageObservable(long next_cursor, final boolean isRefresh) {
        return mMessageApi.getConversations(PAGE_COUNT, next_cursor)
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
    public void deleteMessageConversation(final MessageUser.UserListBean userListBean) {
        mMessageApi.deleteConversation(userListBean.getUser().getId())
                .compose(new ErrorCheckerTransformer<Response>())
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        if (userListBean.getUnread_count() > 0) {
                            resetMessage(userListBean);
                        }
                    }
                })
                .compose(new SchedulerTransformer<Response>())
                .subscribe(new ResponseSubscriber<Response>(mMessageUserView) {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    protected void onFail(Throwable e) {

                    }

                    @Override
                    public void onNext(Response response) {
                        for (int i = 0; i < mUserListBeens.size(); i ++) {
                            MessageUser.UserListBean bean = mUserListBeens.get(i);
                            if (bean.getUser().getId().longValue() == userListBean.getUser().getId().longValue()) {
                                mUserListBeens.remove(bean);
                                mMessageUserView.notifyItemRemove(mUserListBeens, i);
                                break;
                            }
                        }
                    }
                });
    }

    private void resetMessage(final MessageUser.UserListBean userListBean) {
        RxUtil.createDataObservable(new RxUtil.Provider<UnReadMessage>() {
            @Override
            public UnReadMessage getData() {
                return  mNotifyManager.getUnReadMessage(mUid);
            }
        }).doOnNext(new Action1<UnReadMessage>() {
            @Override
            public void call(UnReadMessage unReadMessage) {
                unReadMessage.setDm_single(unReadMessage.getDm_single() - userListBean.getUnread_count());
                mNotifyManager.saveUnReadMessage(unReadMessage);
            }
        })
        .compose(new SchedulerTransformer<UnReadMessage>())
        .subscribe(new SubscriberAdapter<UnReadMessage>() {

            @Override
            public void onNext(UnReadMessage unReadMessage) {
                RxBus.getDefault().post(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE, unReadMessage);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.EVENT_UNREAD_MESSAGE_COMPLETE, mUnReadMessageObservable);
    }

}

package com.caij.emore.present.imp;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.FriendshipPresent;
import com.caij.emore.ui.view.FriendshipView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.UserSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;
import com.caij.emore.utils.weibo.MessageUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/3.
 */
public class FollowsPresentImp extends AbsBasePresent implements FriendshipPresent {

    private static final int PAGE_SIZE = 20;

    private String mToken;
    private long mUid;
    private UserSource mUserSource;
    private FriendshipView mFriendshipView;
    private FriendshipResponse mLastFriendshipResponse;
    private List<User> mUsers;
    MessageSource mServerMessageSource;
    MessageSource mLocalMessageSource;

    public FollowsPresentImp(String token, long uid, UserSource userSource,
                             MessageSource serverMessageSource,
                             MessageSource localMessageSource,
                             FriendshipView friendshipView) {
        mToken = token;
        mUid = uid;
        mUserSource = userSource;
        mServerMessageSource = serverMessageSource;
        mLocalMessageSource = localMessageSource;
        mFriendshipView = friendshipView;
        mUsers = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void userFirstVisible() {
        refresh();
    }

    @Override
    public void loadMore() {
        Subscription subscription = createUsersObservable(mLastFriendshipResponse.getNext_cursor(), false)
                .subscribe(new DefaultResponseSubscriber<List<User>>(mFriendshipView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mFriendshipView.onLoadComplete(true);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        mUsers.addAll(users);
                        mFriendshipView.notifyItemRangeInserted(mUsers, mUsers.size() - users.size(), users.size());
                        mFriendshipView.onLoadComplete(users.size() > PAGE_SIZE - 1);
                    }
                });
        addSubscription(subscription);
    }

    public Observable<List<User>> createUsersObservable(long next_cursor, final boolean isRefresh) {
        return mUserSource.getFollowers(mToken, mUid, PAGE_SIZE, 0, next_cursor)
                .compose(new ErrorCheckerTransformer<FriendshipResponse>())
                .flatMap(new Func1<FriendshipResponse, Observable<User>>() {
                    @Override
                    public Observable<User> call(FriendshipResponse friendshipResponse) {
                        mLastFriendshipResponse = friendshipResponse;
                        return Observable.from(friendshipResponse.getUsers());
                    }
                })
                .filter(new Func1<User, Boolean>() {
                    @Override
                    public Boolean call(User user) {
                        return !mUsers.contains(user) || isRefresh;
                    }
                })
                .toList()
                .compose(new SchedulerTransformer<List<User>>());
    }

    @Override
    public void refresh() {
        Subscription subscription = createUsersObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<User>>(mFriendshipView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mFriendshipView.onRefreshComplete();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        mUsers.clear();
                        mUsers.addAll(users);
                        mFriendshipView.setEntities(mUsers);
                        mFriendshipView.onLoadComplete(users.size() > PAGE_SIZE - 1);
                        mFriendshipView.onRefreshComplete();

                        if (mUid == Long.parseLong(UserPrefs.get().getEMoreToken().getUid())) {
                            MessageUtil.resetUnReadMessage(mToken, UnReadMessage.TYPE_FOLLOWER,
                                    mServerMessageSource, mLocalMessageSource);
                        }
                    }
                });
        addSubscription(subscription);
    }
}

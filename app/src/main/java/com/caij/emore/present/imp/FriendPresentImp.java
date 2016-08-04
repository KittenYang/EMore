package com.caij.emore.present.imp;

import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.FriendshipPresent;
import com.caij.emore.present.view.FriendshipView;
import com.caij.emore.source.UserSource;
import com.caij.emore.utils.rxjava.DefaultResponseSubscriber;
import com.caij.emore.utils.rxjava.ErrorCheckerTransformer;
import com.caij.emore.utils.rxjava.SchedulerTransformer;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/3.
 */
public class FriendPresentImp implements FriendshipPresent {

    private static final int PAGE_SIZE = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
    private String mToken;
    private long mUid;
    private UserSource mUserSource;
    private FriendshipView mFriendshipView;
    private FriendshipResponse mLastFriendshipResponse;
    private List<User> mUsers;

    public FriendPresentImp(String token, long uid, UserSource userSource, FriendshipView friendshipView) {
        mToken = token;
        mUid = uid;
        mUserSource = userSource;
        mFriendshipView = friendshipView;
        mUsers = new ArrayList<>();
        mLoginCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mLoginCompositeSubscription.clear();
    }

    @Override
    public void userFirstVisible() {
        Subscription subscription = createUsersObservable(0, true)
                .subscribe(new DefaultResponseSubscriber<List<User>>(mFriendshipView) {
                    @Override
                    protected void onFail(Throwable e) {
                        mFriendshipView.onLoadComplete(false);
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(List<User> users) {
                        mUsers.addAll(users);
                        mFriendshipView.setEntities(mUsers);
                        mFriendshipView.onLoadComplete(users.size() > PAGE_SIZE - 1);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
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
                        mFriendshipView.setEntities(mUsers);
                        mFriendshipView.onLoadComplete(users.size() > PAGE_SIZE - 1);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    private Observable<List<User>> createUsersObservable(long next_cursor, final boolean isRefresh) {
        return mUserSource.getFriends(mToken, mUid, PAGE_SIZE, 0, next_cursor)
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
}

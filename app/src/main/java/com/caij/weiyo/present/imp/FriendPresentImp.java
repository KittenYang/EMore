package com.caij.weiyo.present.imp;

import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.response.FriendshipResponse;
import com.caij.weiyo.present.FriendshipPresent;
import com.caij.weiyo.present.view.FriendshipView;
import com.caij.weiyo.source.UserSource;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
        Subscription subscription = mUserSource.getFriends(mToken, mUid, PAGE_SIZE, 0, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FriendshipResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mFriendshipView.onDefaultLoadError();
                        mFriendshipView.onLoadComplete(false);
                    }

                    @Override
                    public void onNext(FriendshipResponse friendshipResponse) {
                        mLastFriendshipResponse = friendshipResponse;
                        mUsers.addAll(friendshipResponse.getUsers());
                        mFriendshipView.setEntities(mUsers);
                        mFriendshipView.onLoadComplete(friendshipResponse.getUsers().size() > PAGE_SIZE - 1);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void loadMore() {
        Subscription subscription = mUserSource.getFriends(mToken, mUid, PAGE_SIZE, 0, mLastFriendshipResponse.getNext_cursor())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FriendshipResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mFriendshipView.onDefaultLoadError();
                        mFriendshipView.onLoadComplete(true);
                    }

                    @Override
                    public void onNext(FriendshipResponse friendshipResponse) {
                        mLastFriendshipResponse = friendshipResponse;
                        mUsers.addAll(friendshipResponse.getUsers());
                        mFriendshipView.setEntities(mUsers);
                        mFriendshipView.onLoadComplete(friendshipResponse.getUsers().size() > PAGE_SIZE - 1);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }
}

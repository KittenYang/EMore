package com.caij.emore.present.imp;

import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.FriendshipPresent;
import com.caij.emore.present.view.FriendshipView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.UserSource;
import com.caij.emore.utils.weibo.MessageUtil;

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
public class FollowsPresentImp implements FriendshipPresent {

    private static final int PAGE_SIZE = 20;

    private final CompositeSubscription mLoginCompositeSubscription;
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
        Subscription subscription = mUserSource.getFollowers(mToken, mUid, PAGE_SIZE, 0, 0)
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

                        MessageUtil.resetUnReadMessage(mToken, UnReadMessage.TYPE_FOLLOWER,
                                mServerMessageSource, mLocalMessageSource);
                    }
                });
        mLoginCompositeSubscription.add(subscription);
    }

    @Override
    public void loadMore() {
        Subscription subscription = mUserSource.getFollowers(mToken, mUid, PAGE_SIZE, 0, mLastFriendshipResponse.getNext_cursor())
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

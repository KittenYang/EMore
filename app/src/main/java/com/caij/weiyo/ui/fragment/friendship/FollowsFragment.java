package com.caij.weiyo.ui.fragment.friendship;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.weiyo.Key;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.present.FriendshipPresent;
import com.caij.weiyo.present.imp.FollowsPresentImp;
import com.caij.weiyo.present.imp.FriendPresentImp;
import com.caij.weiyo.source.server.ServerUserSource;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;

/**
 * Created by Caij on 2016/7/3.
 */
public class FollowsFragment extends FriendshipFragment {

    private FriendshipPresent mFriendshipPresent;

    public static FollowsFragment newInstance(long uid) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, uid);
        FollowsFragment fragment = new FollowsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        long uid = getArguments().getLong(Key.ID);
        mFriendshipPresent = new FollowsPresentImp(accessToken.getAccess_token(), uid, new ServerUserSource(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
        mFriendshipPresent.onFirstVisible();
    }

    @Override
    public void onLoadMore() {
        mFriendshipPresent.onLoadMore();
    }
}

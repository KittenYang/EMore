package com.caij.emore.ui.fragment.friendship;

import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.present.FriendshipPresent;
import com.caij.emore.present.imp.FollowsPresentImp;
import com.caij.emore.source.server.ServerUserSource;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;

/**
 * Created by Caij on 2016/7/3.
 */
public class FollowsFragment extends FriendshipFragment<FriendshipPresent> {

    public static FollowsFragment newInstance(long uid) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, uid);
        FollowsFragment fragment = new FollowsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FriendshipPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        long uid = getArguments().getLong(Key.ID);
        return new FollowsPresentImp(accessToken.getAccess_token(), uid, new ServerUserSource(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
    }

}
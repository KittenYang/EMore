package com.caij.emore.ui.fragment.friendship;

import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.imp.FriendPresentImp;
import com.caij.emore.source.server.ServerUserSource;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;

/**
 * Created by Caij on 2016/7/3.
 */
public class FriendFragment extends FriendshipFragment {

    public static FriendFragment newInstance(long uid) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, uid);
        FriendFragment fragment = new FriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ListPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        long uid = getArguments().getLong(Key.ID);
        return new FriendPresentImp(accessToken.getAccess_token(), uid, new ServerUserSource(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
    }

}
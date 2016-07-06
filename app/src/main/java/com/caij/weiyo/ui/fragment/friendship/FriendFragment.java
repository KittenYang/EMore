package com.caij.weiyo.ui.fragment.friendship;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.weiyo.Key;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.present.FriendshipPresent;
import com.caij.weiyo.present.ListPresent;
import com.caij.weiyo.present.imp.FriendPresentImp;
import com.caij.weiyo.source.server.ServerUserSource;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;

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

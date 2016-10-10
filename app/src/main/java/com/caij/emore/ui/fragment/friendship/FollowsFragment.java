package com.caij.emore.ui.fragment.friendship;

import android.os.Bundle;

import com.caij.emore.Key;
import com.caij.emore.account.Account;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.dao.imp.NotifyManagerImp;
import com.caij.emore.present.FriendshipPresent;
import com.caij.emore.present.imp.FollowsPresentImp;
import com.caij.emore.remote.imp.UnReadMessageApiImp;
import com.caij.emore.remote.imp.UserApiImp;

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
        Account account = UserPrefs.get(getActivity()).getAccount();
        long uid = getArguments().getLong(Key.ID);
        return new FollowsPresentImp(uid, new UserApiImp(),
                new UnReadMessageApiImp(), new NotifyManagerImp(), this);
    }

}

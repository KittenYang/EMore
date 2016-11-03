package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.manager.imp.StatusManagerImp;
import com.caij.emore.present.GroupStatusPresent;
import com.caij.emore.present.imp.GroupStatusPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.fragment.weibo.TimeLineStatusFragment;

/**
 * Created by Caij on 2016/11/2.
 */

public class GroupStatusFragment extends TimeLineStatusFragment<GroupStatusPresent> {

    public static GroupStatusFragment newInstance(long groupId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, groupId);
        GroupStatusFragment fragment = new GroupStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected GroupStatusPresent createPresent() {
        long groupId = getArguments().getLong(Key.ID);
        return new GroupStatusPresentImp(groupId, this, new StatusApiImp(), new StatusManagerImp(), new AttitudeApiImp());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresent.refresh();
            }
        });
    }


}

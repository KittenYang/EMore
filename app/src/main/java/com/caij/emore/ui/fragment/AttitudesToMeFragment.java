package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Attitude;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.imp.AttitudesToMePresentImp;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.ui.adapter.ToMeAttitudeAdapter;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.XRecyclerView;


/**
 * Created by Caij on 2016/7/4.
 */
public class AttitudesToMeFragment extends SwipeRefreshRecyclerViewFragment<Attitude, RefreshListPresent> {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected ToMeAttitudeAdapter createRecyclerViewAdapter() {
        return  new ToMeAttitudeAdapter(getActivity());
    }

    protected RefreshListPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        return new AttitudesToMePresentImp(accessToken.getAccess_token(), new ServerWeiboSource(),
                new ServerMessageSource(), new LocalMessageSource(), this);
    }


    @Override
    public void onItemClick(View view, final int position) {
        Intent intent = WeiboDetialActivity.newIntent(getActivity(),
                mRecyclerViewAdapter.getItem(position).getStatus().getId());
        startActivity(intent);
    }
}

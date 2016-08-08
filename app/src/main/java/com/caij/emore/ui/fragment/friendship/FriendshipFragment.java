package com.caij.emore.ui.fragment.friendship;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.view.FriendshipView;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.adapter.UserAdapter;
import com.caij.emore.ui.fragment.RecyclerViewFragment;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.DensityUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.XRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/7/3.
 */
public abstract class FriendshipFragment<P extends RefreshListPresent> extends SwipeRefreshRecyclerViewFragment<User, P> implements FriendshipView,RecyclerViewOnItemClickListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int divMarginleft = DensityUtil.dip2px(getActivity(), 56f + 16f);
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(divMarginleft, 0).
                size(DensityUtil.dip2px(getActivity(), 1f)).
                build());
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected BaseAdapter<User, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new UserAdapter(getActivity());
    }

    @Override
    public void onItemClick(View view, int position) {
        User user = mRecyclerViewAdapter.getItem(position);
        Intent intent = UserInfoActivity.newIntent(getActivity(), user.getScreen_name());
        startActivity(intent);
    }
}

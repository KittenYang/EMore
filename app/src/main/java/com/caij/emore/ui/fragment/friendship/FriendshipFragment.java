package com.caij.emore.ui.fragment.friendship;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.ui.adapter.delegate.UserDelegate;
import com.caij.emore.ui.view.FriendshipView;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.DensityUtil;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.RecyclerViewOnItemClickListener;
import com.caij.rvadapter.adapter.BaseAdapter;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/7/3.
 */
public abstract class FriendshipFragment<P extends RefreshListPresent> extends SwipeRefreshRecyclerViewFragment<User, P> implements FriendshipView,RecyclerViewOnItemClickListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int divMarginLeft = DensityUtil.dip2px(getActivity(), 56f + 16f);
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(divMarginLeft, 0).
                size(DensityUtil.dip2px(getActivity(), 0.66f)).
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
        MultiItemTypeAdapter<User> multiItemTypeAdapter = new MultiItemTypeAdapter<User>(getActivity());
        multiItemTypeAdapter.addItemViewDelegate(new UserDelegate(null));
        return multiItemTypeAdapter;
    }

    @Override
    public void onItemClick(View view, int position) {
        User user = mRecyclerViewAdapter.getItem(position);
        Intent intent = UserInfoActivity.newIntent(getActivity(), user.getScreen_name());
        startActivity(intent);
    }
}

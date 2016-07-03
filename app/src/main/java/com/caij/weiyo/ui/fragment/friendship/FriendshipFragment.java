package com.caij.weiyo.ui.fragment.friendship;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.view.FriendshipView;
import com.caij.weiyo.ui.activity.UserInfoActivity;
import com.caij.weiyo.ui.adapter.UserAdapter;
import com.caij.weiyo.ui.fragment.RecyclerViewFragment;
import com.caij.weiyo.utils.DensityUtil;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/7/3.
 */
public abstract class FriendshipFragment extends RecyclerViewFragment implements
        LoadMoreRecyclerView.OnLoadMoreListener, FriendshipView,RecyclerViewOnItemClickListener {

    protected UserAdapter mUserAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserAdapter = new UserAdapter(getActivity());
        mUserAdapter.setOnItemClickListener(this);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mUserAdapter);
        int divMarginleft = DensityUtil.dip2px(getActivity(), 56f + 16f);
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(divMarginleft, 0).
                size(DensityUtil.dip2px(getActivity(), 1f)).
                build());
    }

    public void setUsers(List<User> users) {
        mUserAdapter.setEntities(users);
        mUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadComplite(boolean isHaveMore) {
        if (isHaveMore) {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NORMAL);
        }else {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NO_MORE);
        }
    }

    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
    }

    @Override
    public void onItemClick(View view, int position) {
        User user = mUserAdapter.getItem(position);
        Intent intent = UserInfoActivity.newIntent(getActivity(), user);
        startActivity(intent);
    }
}

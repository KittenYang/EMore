package com.caij.weiyo.ui.fragment.friendship;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.ListPresent;
import com.caij.weiyo.present.view.FriendshipView;
import com.caij.weiyo.ui.activity.UserInfoActivity;
import com.caij.weiyo.ui.adapter.UserAdapter;
import com.caij.weiyo.ui.fragment.RecyclerViewFragment;
import com.caij.weiyo.utils.DensityUtil;
import com.caij.weiyo.view.recyclerview.BaseAdapter;
import com.caij.weiyo.view.recyclerview.BaseViewHolder;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/7/3.
 */
public abstract class FriendshipFragment<P extends ListPresent> extends RecyclerViewFragment<User, P> implements
        LoadMoreRecyclerView.OnLoadMoreListener, FriendshipView,RecyclerViewOnItemClickListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int divMarginleft = DensityUtil.dip2px(getActivity(), 56f + 16f);
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item)).
                margin(divMarginleft, 0).
                size(DensityUtil.dip2px(getActivity(), 1f)).
                build());
    }

    @Override
    protected BaseAdapter<User, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new UserAdapter(getActivity());
    }

    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
    }

    @Override
    public void onItemClick(View view, int position) {
        User user = mRecyclerViewAdapter.getItem(position);
        Intent intent = UserInfoActivity.newIntent(getActivity(), user);
        startActivity(intent);
    }
}

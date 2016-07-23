package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Attitude;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.imp.WeiboAttitudesPresentImp;
import com.caij.emore.present.view.WeiboAttitudesView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.adapter.AttitudeAdapter;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboLikerListFragment extends RecyclerViewFragment<Attitude, ListPresent> implements WeiboAttitudesView {

    public static WeiboLikerListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, weiboId);
        WeiboLikerListFragment fragment = new WeiboLikerListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadMoreLoadMoreRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());
    }

    @Override
    protected BaseAdapter<Attitude, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new AttitudeAdapter(getActivity());
    }

    @Override
    protected ListPresent createPresent() {
        AccessToken token = UserPrefs.get().getWeiCoToken();
        long weiId = getArguments().getLong(Key.ID);
        return  new WeiboAttitudesPresentImp(token.getAccess_token(), weiId,
                new ServerWeiboSource(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
    }


    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
        // TODO: 2016/6/16
    }

    @Override
    public void onItemClick(View view, int position) {
        Attitude attitude = mRecyclerViewAdapter.getItem(position);
        Intent intent = UserInfoActivity.newIntent(getActivity(), attitude.getUser().getScreen_name());
        startActivity(intent);
    }

    @Override
    public void onAttitudeSuccess(List<Attitude> attitudes) {
        mRecyclerViewAdapter.setEntities(attitudes);
        mRecyclerViewAdapter.notifyItemInserted(0);
        LinearLayoutManager manager = (LinearLayoutManager) mLoadMoreLoadMoreRecyclerView.getLayoutManager();
        if (manager.findFirstVisibleItemPosition() < 2) {
            mLoadMoreLoadMoreRecyclerView.smoothScrollToPosition(0);
        }
    }
}

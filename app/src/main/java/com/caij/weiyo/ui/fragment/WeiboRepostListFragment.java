package com.caij.weiyo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.WeiboRepostsPresent;
import com.caij.weiyo.present.imp.WeiboRepostsPresentImp;
import com.caij.weiyo.present.view.WeiboRepostsView;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.adapter.RepostAdapter;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboRepostListFragment extends RecyclerViewFragment implements WeiboRepostsView,
        LoadMoreRecyclerView.OnLoadMoreListener {

    private WeiboRepostsPresent mWeiboRepostsPresent;

    private RepostAdapter mRepostAdapter;

    public static WeiboRepostListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, weiboId);
        WeiboRepostListFragment fragment = new WeiboRepostListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AccessToken token = UserPrefs.get().getWeiCoToken();
        long weiId = getArguments().getLong(Key.ID);
        mWeiboRepostsPresent = new WeiboRepostsPresentImp(token.getAccess_token(), weiId,
                new ServerWeiboSource(), this);
        mRepostAdapter = new RepostAdapter(getActivity());
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mRepostAdapter);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());
    }

    @Override
    protected void onUserFirstVisible() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
        mWeiboRepostsPresent.onFirstVisible();
    }

    @Override
    public void onLoadMore() {
        mWeiboRepostsPresent.onLoadMore();
    }

    @Override
    public void onLoadComplite(boolean isHaveMore) {
        if (isHaveMore) {
            mLoadMoreLoadMoreRecyclerView.completeLoading();
        }else {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NO_MORE);
        }
    }

    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
        // TODO: 2016/6/16
    }

    @Override
    public void setWeibos(List<Weibo> weibos) {
        mRepostAdapter.setEntities(weibos);
        mRepostAdapter.notifyDataSetChanged();
    }

    @Override
    public Context getContent() {
        return getActivity();
    }
}

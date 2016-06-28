package com.caij.weiyo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.FriendWeiboPresent;
import com.caij.weiyo.present.FriendWeiboPresentImp;
import com.caij.weiyo.present.view.FriendWeiboView;
import com.caij.weiyo.source.local.LocalWeiboSource;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.activity.WeiboDetialActivity;
import com.caij.weiyo.ui.adapter.WeiboAdapter;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.List;

/**
 * Created by Caij on 2016/6/4.
 */
public class FriendWeiboFragment extends SwipeRefreshRecyclerViewFragment<Weibo> implements FriendWeiboView , RecyclerViewOnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener {

    FriendWeiboPresent mFriendWeiboPresent;
    WeiboAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new WeiboAdapter(getActivity(), this);
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        AccessToken token = UserPrefs.get().getWeiYoToken();
        mFriendWeiboPresent = new FriendWeiboPresentImp(token.getAccess_token(), this,
                new ServerWeiboSource(), new LocalWeiboSource());
        mFriendWeiboPresent.onCreate();
    }

    @Override
    protected void onUserFirstVisible() {

    }

    @Override
    public void onLoadMore() {
        mFriendWeiboPresent.onLoadMore();
    }

    @Override
    public void onRefresh() {
        mFriendWeiboPresent.onRefresh();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = WeiboDetialActivity.newIntent(getActivity(), mAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void setFriendWeibo(List<Weibo> weibos) {
        mAdapter.setEntities(weibos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void toRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mFriendWeiboPresent.onRefresh();
    }

    @Override
    public void onRefreshComplite() {
        mSwipeRefreshLayout.setRefreshing(false);
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

    }

    @Override
    public Context getContent() {
        return getActivity().getApplication();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFriendWeiboPresent.onDestroy();
    }
}

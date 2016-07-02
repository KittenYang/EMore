package com.caij.weiyo.ui.fragment.weibo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.TimeLinePresent;
import com.caij.weiyo.present.imp.UserWeiboPresentImp;
import com.caij.weiyo.present.view.TimeLineWeiboView;
import com.caij.weiyo.ui.activity.WeiboDetialActivity;
import com.caij.weiyo.ui.adapter.WeiboAdapter;
import com.caij.weiyo.ui.fragment.RecyclerViewFragment;
import com.caij.weiyo.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.List;

/**
 * Created by Caij on 2016/6/4.
 */
public abstract class TimeLineWeiboFragment<P extends TimeLinePresent> extends RecyclerViewFragment
        implements TimeLineWeiboView, RecyclerViewOnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener {

    P mTimeLineWeiboPresent;
    WeiboAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new WeiboAdapter(getActivity(), this);
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mAdapter);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mTimeLineWeiboPresent = createPresent();
        if (mTimeLineWeiboPresent != null) {
            mTimeLineWeiboPresent.onCreate();
        }
    }

    protected abstract P createPresent();

    @Override
    public void onLoadMore() {
        mTimeLineWeiboPresent.onLoadMore();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = WeiboDetialActivity.newIntent(getActivity(), mAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void setWeibos(List<Weibo> weibos) {
        mAdapter.setEntities(weibos);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void toRefresh() {
        mTimeLineWeiboPresent.onRefresh();
    }

    @Override
    public void onRefreshComplite() {
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
    public Context getContent() {
        return getActivity().getApplication();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimeLineWeiboPresent.onDestroy();
    }
}

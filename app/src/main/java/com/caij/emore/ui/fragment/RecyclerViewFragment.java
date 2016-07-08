package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.view.BaseListView;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Caij on 2015/9/23.
 */
public abstract class RecyclerViewFragment<E, P extends ListPresent> extends LazyFragment implements LoadMoreRecyclerView.OnLoadMoreListener, BaseListView<E>,RecyclerViewOnItemClickListener {

    @BindView(R.id.recycler_view)
    protected LoadMoreRecyclerView mLoadMoreLoadMoreRecyclerView;
    protected BaseAdapter<E, ? extends BaseViewHolder> mRecyclerViewAdapter;
    protected P mPresent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    protected int getLayoutId() {
        return R.layout.include_recycle_view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerViewAdapter = createRecyclerViewAdapter();
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(createRecyclerLayoutManager());
        mPresent = createPresent();
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        if (mRecyclerViewAdapter != null) {
            mLoadMoreLoadMoreRecyclerView.setAdapter(mRecyclerViewAdapter);
            mRecyclerViewAdapter.setOnItemClickListener(this);
        }
        if (mPresent != null) {
            mPresent.onCreate();
        }
    }

    protected abstract BaseAdapter<E, ? extends BaseViewHolder> createRecyclerViewAdapter();

    protected RecyclerView.LayoutManager createRecyclerLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected abstract P createPresent();

    @Override
    protected void onUserFirstVisible() {
        if (mPresent != null) {
            mPresent.userFirstVisible();
        }
    }

    @Override
    public void onLoadMore() {
        if (mPresent != null) {
            mPresent.loadMore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresent != null) {
            mPresent.onDestroy();
        }
    }

    @Override
    public void onLoadComplete(boolean isHaveMore) {
        if (isHaveMore) {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NORMAL);
        }else {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NO_MORE);
        }
    }

    @Override
    public void setEntities(List<E> entities) {
        mRecyclerViewAdapter.setEntities(entities);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }
}

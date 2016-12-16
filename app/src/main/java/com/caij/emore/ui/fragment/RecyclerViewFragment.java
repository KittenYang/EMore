package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.present.ListPresent;
import com.caij.emore.ui.brige.ToolbarDoubleClick;
import com.caij.emore.ui.view.ListView;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.RecyclerViewOnItemClickListener;
import com.caij.rvadapter.adapter.BaseAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Caij on 2015/9/23.
 */
public abstract class RecyclerViewFragment<E, P extends ListPresent> extends LazyFragment<P> implements
        XRecyclerView.OnLoadMoreListener, ListView<E>,RecyclerViewOnItemClickListener,
        ToolbarDoubleClick {

    @BindView(R.id.xrecycler_view)
    protected XRecyclerView xRecyclerView;

    protected BaseAdapter<E, ? extends BaseViewHolder> mRecyclerViewAdapter;
    private View mErrorView;

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
        xRecyclerView.setLayoutManager(createRecyclerLayoutManager());

        xRecyclerView.setOnLoadMoreListener(this);
        if (mRecyclerViewAdapter != null) {
            xRecyclerView.setAdapter(mRecyclerViewAdapter);
            mRecyclerViewAdapter.setOnItemClickListener(this);
        }
    }

    protected abstract BaseAdapter<E, ? extends BaseViewHolder> createRecyclerViewAdapter();

    protected RecyclerView.LayoutManager createRecyclerLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected View createEmptyView() {
        TextView emptyTextView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.view_empty, xRecyclerView, false);
        setEmptyText(emptyTextView);
        return emptyTextView;
    }

    @Override
    public void showErrorView() {
        if (mErrorView == null) {
            mErrorView = getActivity().getLayoutInflater().inflate(R.layout.view_error, xRecyclerView, false);
            mErrorView.findViewById(R.id.tv_re_load).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mErrorView.setVisibility(View.GONE);
                    onReLoadBtnClick();
                }
            });
            xRecyclerView.addView(mErrorView);
        }else {
            mErrorView.setVisibility(View.VISIBLE);
        }
        xRecyclerView.setFooterState(XRecyclerView.STATE_EMPTY);
    }

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
    public void onLoadComplete(boolean isHaveMore) {
        xRecyclerView.completeLoading(isHaveMore);
    }

    @Override
    public void setEntities(List<E> entities) {
        View emptyView = createEmptyView();
        if (xRecyclerView.getEmptyView() == null && emptyView != null) {
            xRecyclerView.setEmptyView(emptyView);
        }
        mRecyclerViewAdapter.setEntities(entities);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(List<E> entities, int index) {
        mRecyclerViewAdapter.setEntities(entities);
        mRecyclerViewAdapter.notifyItemChanged(index);
    }

    @Override
    public void notifyItemRangeInserted(List<E> entities, int position, int count) {
        mRecyclerViewAdapter.setEntities(entities);
        mRecyclerViewAdapter.notifyItemRangeInserted(position, count);
    }

    protected void onReLoadBtnClick() {

    }

    protected void setEmptyText(TextView textView) {
        textView.setText("这里什么都没有");
    }

    @Override
    public void acceptToolBarDoubleClick(View view) {
        xRecyclerView.smoothScrollToPosition(0);
    }
}

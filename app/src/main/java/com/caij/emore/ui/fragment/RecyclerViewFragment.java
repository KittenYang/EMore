package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.view.BaseListView;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.XRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Caij on 2015/9/23.
 */
public abstract class RecyclerViewFragment<E, P extends ListPresent> extends LazyFragment implements XRecyclerView.OnLoadMoreListener, BaseListView<E>,RecyclerViewOnItemClickListener {

    @BindView(R.id.recycler_view)
    protected XRecyclerView xRecyclerView;
    @BindView(R.id.rl_root)
    protected RelativeLayout mRlRoot;
    protected BaseAdapter<E, ? extends BaseViewHolder> mRecyclerViewAdapter;
    protected P mPresent;
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

        mPresent = createPresent();
        xRecyclerView.setOnLoadMoreListener(this);
        if (mRecyclerViewAdapter != null) {
            xRecyclerView.setAdapter(mRecyclerViewAdapter);
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

    protected View createEmptyView() {
        TextView emptyTextView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.view_empty, mRlRoot, false);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresent != null) {
            mPresent.onDestroy();
        }
    }

    @Override
    public void onLoadComplete(boolean isHaveMore) {
        xRecyclerView.completeLoading(isHaveMore);
    }

    @Override
    public void setEntities(List<E> entities) {
        if (xRecyclerView.getEmptyView() == null) {
            xRecyclerView.setEmptyView(createEmptyView());
        }
        mRecyclerViewAdapter.setEntities(entities);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    protected void onReLoadBtnClick() {

    }

    protected void setEmptyText(TextView textView) {
        textView.setText("这里什么都没有");
    }
}

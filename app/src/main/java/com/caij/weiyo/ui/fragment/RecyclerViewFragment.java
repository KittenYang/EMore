package com.caij.weiyo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caij.weiyo.R;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Caij on 2015/9/23.
 */
public abstract class RecyclerViewFragment extends LazyFragment {

    @BindView(R.id.recycler_view)
    public LoadMoreRecyclerView mLoadMoreLoadMoreRecyclerView;

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
}

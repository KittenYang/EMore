package com.caij.weiyo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caij.weiyo.R;
import com.caij.weiyo.present.RefreshListPresent;
import com.caij.weiyo.present.view.RefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2015/11/4.
 */
public abstract class SwipeRefreshRecyclerViewFragment<E, P extends RefreshListPresent> extends RecyclerViewFragment<E, P>
        implements SwipeRefreshLayout.OnRefreshListener, RefreshListView<E> {

    @BindView(R.id.swipe_refresh_layout)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.include_refresh_recycle_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));

    }

    @Override
    public void onRefresh() {
        if (mPresent != null) {
            mPresent.refresh();
        }
    }

    @Override
    public void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}

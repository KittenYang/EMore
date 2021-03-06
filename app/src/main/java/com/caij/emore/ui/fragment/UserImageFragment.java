package com.caij.emore.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.present.UserStatusPresent;
import com.caij.emore.present.imp.UserImagePresentImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.adapter.delegate.StatusImageDelegate;
import com.caij.emore.ui.view.TimeLineStatusImageView;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.BaseAdapter;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserImageFragment extends RecyclerViewFragment<StatusImageInfo, UserStatusPresent> implements TimeLineStatusImageView, XRecyclerView.OnLoadMoreListener {


    public static UserImageFragment newInstance(long uid) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, uid);
        UserImageFragment fragment = new UserImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    protected BaseAdapter<StatusImageInfo, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        MultiItemTypeAdapter<StatusImageInfo> multiItemTypeAdapter = new MultiItemTypeAdapter<StatusImageInfo>(getActivity());
        multiItemTypeAdapter.addItemViewDelegate(new StatusImageDelegate(null));
        return multiItemTypeAdapter;
    }

    private void initView() {
        xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.image_item_space);
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.spacing_medium));
        View headView  = new View(getActivity());
        headView.setLayoutParams(params);
        xRecyclerView.getAdapter().addHeaderView(headView);
    }

    @Override
    protected RecyclerView.LayoutManager createRecyclerLayoutManager() {
        return new GridLayoutManager(getActivity(), 3);
    }

    @Override
    protected UserStatusPresent createPresent() {
        long uid = getArguments().getLong(Key.ID);
        return new UserImagePresentImp(uid, this, new StatusApiImp());
    }

    @Override
    protected void onReLoadBtnClick() {
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
        mPresent.userFirstVisible();
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}

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
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.bean.PicUrl;
import com.caij.emore.present.UserWeiboPresent;
import com.caij.emore.present.imp.UserImagePresentImp;
import com.caij.emore.present.view.TimeLineWeiboImageView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.adapter.UserGridImageAdapter;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.XRecyclerView;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserImageFragment extends RecyclerViewFragment<PicUrl, UserWeiboPresent> implements TimeLineWeiboImageView, XRecyclerView.OnLoadMoreListener {


    public static UserImageFragment newInstance(String username) {
        Bundle args = new Bundle();
        args.putString(Key.USERNAME, username);
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
    protected BaseAdapter<PicUrl, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new UserGridImageAdapter(getActivity());
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
    protected UserWeiboPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        String username = getArguments().getString(Key.USERNAME);
        return new UserImagePresentImp(accessToken.getAccess_token(), username, this, new ServerWeiboSource());
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

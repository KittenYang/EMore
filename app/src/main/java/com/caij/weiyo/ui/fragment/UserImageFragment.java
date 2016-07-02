package com.caij.weiyo.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.present.UserWeiboPresent;
import com.caij.weiyo.present.imp.UserImagePresentImp;
import com.caij.weiyo.present.imp.UserWeiboPresentImp;
import com.caij.weiyo.present.view.TimeLineWeiboImageView;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.adapter.GridImageAdapter;
import com.caij.weiyo.ui.adapter.UserGridImageAdapter;
import com.caij.weiyo.ui.fragment.weibo.TimeLineWeiboFragment;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;

import java.util.List;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserImageFragment extends RecyclerViewFragment implements TimeLineWeiboImageView, LoadMoreRecyclerView.OnLoadMoreListener {

    private UserWeiboPresent mTimeLineWeiboPresent;
    private UserGridImageAdapter mImageAdapter;

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
        mTimeLineWeiboPresent = createPresent();
        initView();
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(gridLayoutManager);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mImageAdapter = new UserGridImageAdapter(getActivity());
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.image_item_space);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.image_item_space);
            }
        });
        mLoadMoreLoadMoreRecyclerView.setAdapter(mImageAdapter);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelOffset(R.dimen.spacing_medium));
        View headView  = new View(getActivity());
        headView.setLayoutParams(params);
        mLoadMoreLoadMoreRecyclerView.getAdapter().addHeaderView(headView);
    }

    protected UserWeiboPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        String username = getArguments().getString(Key.USERNAME);
        return new UserImagePresentImp(accessToken.getAccess_token(), username, this, new ServerWeiboSource());
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
    }

    @Override
    protected void onUserFirstVisible() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
        mTimeLineWeiboPresent.onFirstVisible();
    }

    @Override
    public void onLoadMore() {
        mTimeLineWeiboPresent.onLoadMore();
    }

    @Override
    public void setImages(List<PicUrl> picUrls) {
        mImageAdapter.setEntities(picUrls);
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshComplite() {

    }
}

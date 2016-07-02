package com.caij.weiyo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.WeiboCommentsPresent;
import com.caij.weiyo.present.imp.WeiboCommentsPresentImp;
import com.caij.weiyo.present.view.WeiboCommentsView;
import com.caij.weiyo.source.server.ServerCommentSource;
import com.caij.weiyo.ui.adapter.CommentAdapter;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboCommentListFragment extends RecyclerViewFragment implements WeiboCommentsView, LoadMoreRecyclerView.OnLoadMoreListener {

    private WeiboCommentsPresent mWeiboCommentsPresent;
    private CommentAdapter mCommentAdapter;

    public static WeiboCommentListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, weiboId);
        WeiboCommentListFragment fragment = new WeiboCommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AccessToken token = UserPrefs.get().getWeiYoToken();
        long weiId = getArguments().getLong(Key.ID);
        mWeiboCommentsPresent = new WeiboCommentsPresentImp(token.getAccess_token(), weiId,
                new ServerCommentSource(), this);
        mCommentAdapter = new CommentAdapter(getActivity());
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mCommentAdapter);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());
    }

    @Override
    protected void onUserFirstVisible() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
        mWeiboCommentsPresent.onFirstVisible();
    }

    @Override
    public void onLoadMore() {
        mWeiboCommentsPresent.onLoadMore();
    }

    @Override
    public void setComments(List<Comment> comments) {
        mCommentAdapter.setEntities(comments);
        mCommentAdapter.notifyDataSetChanged();
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
        // TODO: 2016/6/16
    }

    @Override
    public Context getContent() {
        return getActivity();
    }
}

package com.caij.weiyo.ui.fragment.mention;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.present.MentionPresent;
import com.caij.weiyo.present.PublishCommentsPresent;
import com.caij.weiyo.present.imp.PublishCommentsPresentImp;
import com.caij.weiyo.present.view.MentionView;
import com.caij.weiyo.present.view.MyPublishComentsView;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.activity.WeiboDetialActivity;
import com.caij.weiyo.ui.activity.publish.ReplyCommentActivity;
import com.caij.weiyo.ui.adapter.MyMessageCommentAdapter;
import com.caij.weiyo.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import java.util.List;

/**
 * Created by Caij on 2016/7/4.
 */
public class PublishCommentsFragment extends SwipeRefreshRecyclerViewFragment implements LoadMoreRecyclerView.OnLoadMoreListener, MentionView<Comment>,RecyclerViewOnItemClickListener, MyPublishComentsView {

    private PublishCommentsPresent mMentionPresent;
    private MyMessageCommentAdapter mAdapter;
    private Dialog mDialogLoading;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mMentionPresent = createPresent();
        mAdapter = createAdapter();
        mAdapter.setOnItemClickListener(this);
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onUserFirstVisible() {
        mMentionPresent.onUserFirstVisible();
    }

    @Override
    public void onRefresh() {
        mMentionPresent.onRefresh();
    }

    @Override
    public void onLoadMore() {
        mMentionPresent.onLoadMore();
    }

    @Override
    public void setEntities(List<Comment> entities) {
        mAdapter.setEntities(entities);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void toRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mMentionPresent.onRefresh();
    }

    @Override
    public void onRefreshComplite() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoadComplite(boolean isHaveMore) {
        if (isHaveMore) {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NORMAL);
        }else {
            mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_NO_MORE);
        }
    }

    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
    }

    protected PublishCommentsPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiYoToken();
        return new PublishCommentsPresentImp(accessToken.getAccess_token(), new ServerWeiboSource(), this);
    }

    protected MyMessageCommentAdapter createAdapter() {
        return new MyMessageCommentAdapter(getActivity());
    }

    @Override
    public void onItemClick(View view, final int position) {
        String[] items = new String[]{"删除", "查看微博"};
        DialogUtil.showItemDialog(getActivity(), null, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mMentionPresent.deleteComment(mAdapter.getItem(position), position);
                }else if (which == 1) {
                    Intent intent = WeiboDetialActivity.newIntent(getActivity(), mAdapter.getItem(position).getStatus());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onDeleteCommentSuccess(Comment comment, int position) {
        mAdapter.removeEntity(comment);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void showDialogLoading(boolean isShow) {
        if (isShow) {
            if (mDialogLoading == null) {
                mDialogLoading = DialogUtil.showProgressDialog(getActivity(), null, getString(R.string.requesting));
            }else {
                mDialogLoading.show();
            }
        }else {
            mDialogLoading.dismiss();
        }
    }
}

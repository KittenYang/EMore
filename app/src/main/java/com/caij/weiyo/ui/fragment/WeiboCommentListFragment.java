package com.caij.weiyo.ui.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.activity.publish.ReplyCommentActivity;
import com.caij.weiyo.ui.activity.publish.RepostWeiboActivity;
import com.caij.weiyo.ui.adapter.CommentAdapter;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.utils.ToastUtil;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboCommentListFragment extends RecyclerViewFragment implements WeiboCommentsView, LoadMoreRecyclerView.OnLoadMoreListener, RecyclerViewOnItemClickListener {

    private static final int REPLY_COMMENT_REQUEST_CODE = 100;
    private WeiboCommentsPresent mWeiboCommentsPresent;
    private CommentAdapter mCommentAdapter;
    private Dialog mCommentDialog;
    private ClipboardManager mClipboardManager;
    private Weibo mWeibo;

    public static WeiboCommentListFragment newInstance(Weibo weibo) {
        Bundle args = new Bundle();
        args.putSerializable(Key.OBJ, weibo);
        WeiboCommentListFragment fragment = new WeiboCommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AccessToken token = UserPrefs.get().getWeiYoToken();
        mWeibo = (Weibo) getArguments().getSerializable(Key.OBJ);
        mWeiboCommentsPresent = new WeiboCommentsPresentImp(token.getAccess_token(), mWeibo.getId(),
                new ServerWeiboSource(), this);
        mCommentAdapter = new CommentAdapter(getActivity());
        mCommentAdapter.setOnItemClickListener(this);
        mLoadMoreLoadMoreRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLoadMoreLoadMoreRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mLoadMoreLoadMoreRecyclerView.setAdapter(mCommentAdapter);
        mLoadMoreLoadMoreRecyclerView.setOnLoadMoreListener(this);
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());

        mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
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

    @Override
    public void showDialogLoading(boolean isShow) {
        if (isShow) {
            if (mCommentDialog  == null) {
                mCommentDialog = DialogUtil.showProgressDialog(getActivity(), null, getString(R.string.requesting));
            }else {
                mCommentDialog.show();
            }
        }else {
            if (mCommentDialog != null) {
                mCommentDialog.dismiss();
            }
        }
    }

    @Override
    public void onDeleteSuccess(Comment comment) {
        mCommentAdapter.removeEntity(comment);
        mCommentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        final Comment comment = mCommentAdapter.getItem(position);
        if (comment.getUser().getId() == Long.parseLong(UserPrefs.get().getWeiYoToken().getUid())) {
            String[] array = new String[]{"删除", "复制"};
            DialogUtil.showItemDialog(getActivity(), null, array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        mWeiboCommentsPresent.deleteComment(comment);
                    }else if (which == 1) {
                        // 将文本内容放到系统剪贴板里。
                        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, comment.getText()));
                        ToastUtil.show(getActivity(), "复制成功");
                    }
                }
            });
        }else {
            String[] array = new String[]{"回复", "转发", "复制"};
            DialogUtil.showItemDialog(getActivity(), null, array, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent intent = ReplyCommentActivity.newIntent(getActivity(), mWeibo.getId(), comment.getId());
                        startActivityForResult(intent, REPLY_COMMENT_REQUEST_CODE);
                    }else if (which == 1) {
                        Intent intent = RepostWeiboActivity.newIntent(getActivity(), mWeibo, comment);
                        startActivityForResult(intent, REPLY_COMMENT_REQUEST_CODE);
                    }else if (which == 2) {
                        // 将文本内容放到系统剪贴板里。
                        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, comment.getText()));
                        ToastUtil.show(getActivity(), "复制成功");
                    }
                }
            });
        }
    }
}

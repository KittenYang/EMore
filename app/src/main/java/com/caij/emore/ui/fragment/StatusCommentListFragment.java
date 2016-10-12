package com.caij.emore.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Comment;
import com.caij.emore.present.StatusCommentsPresent;
import com.caij.emore.present.imp.StatusCommentsPresentImp;
import com.caij.emore.remote.imp.CommentApiImp;
import com.caij.emore.ui.activity.publish.RelayStatusActivity;
import com.caij.emore.ui.view.StatusCommentsView;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.activity.publish.ReplyCommentActivity;
import com.caij.emore.ui.adapter.CommentAdapter;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.OnScrollListener;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class StatusCommentListFragment extends RecyclerViewFragment<Comment, StatusCommentsPresent> implements StatusCommentsView, XRecyclerView.OnLoadMoreListener, RecyclerViewOnItemClickListener {

    private static final int REPLY_COMMENT_REQUEST_CODE = 100;
    private ClipboardManager mClipboardManager;

    public static StatusCommentListFragment newInstance(long statusId) {
        Bundle args = new Bundle();
        args.putSerializable(Key.ID, statusId);
        StatusCommentListFragment fragment = new StatusCommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());
        xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (getActivity() instanceof OnScrollListener) {
                    ((OnScrollListener) getActivity()).onScrolled(recyclerView, dx, dy);
                }
            }
        });
        mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    protected BaseAdapter<Comment, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new CommentAdapter(getActivity());
    }

    @Override
    protected StatusCommentsPresent createPresent() {
        long statusId  = getArguments().getLong(Key.ID);
        return new StatusCommentsPresentImp(statusId, new CommentApiImp(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
    }

    @Override
    protected void onReLoadBtnClick() {
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
        mPresent.userFirstVisible();
    }

    @Override
    protected void setEmptyText(TextView textView) {
        textView.setText(R.string.comment_empty);
    }

    @Override
    public void onDeleteSuccess(Comment comment) {
        mRecyclerViewAdapter.removeEntity(comment);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCommentSuccess(List<Comment> comments) {
        mRecyclerViewAdapter.setEntities(comments);
        mRecyclerViewAdapter.notifyItemInserted(0);
        LinearLayoutManager manager = (LinearLayoutManager) xRecyclerView.getLayoutManager();
        if (manager.findFirstVisibleItemPosition() < 2) {
            xRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void scrollToPosition(int position) {
        xRecyclerView.getRecyclerView().scrollToPosition(position);
    }

    @Override
    public void onItemClick(View view, int position) {
        final Comment comment = mRecyclerViewAdapter.getItem(position);
        if (view.getId() == R.id.imgPhoto) {
            Intent intent = UserInfoActivity.newIntent(getActivity(), comment.getUser().getScreen_name());
            startActivity(intent);
        }else {
            if (comment.getUser().getId() == Long.parseLong(UserPrefs.get(getActivity()).getToken().getUid())) {
                String[] array = new String[]{"删除", "复制"};
                DialogUtil.showItemDialog(getActivity(), null, array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mPresent.deleteComment(comment);
                        } else if (which == 1) {
                            // 将文本内容放到系统剪贴板里。
                            mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, comment.getText()));
                            ToastUtil.show(getActivity(), "复制成功");
                        }
                    }
                });
            } else {
                String[] array = new String[]{"回复", "转发", "复制"};
                DialogUtil.showItemDialog(getActivity(), null, array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = ReplyCommentActivity.newIntent(getActivity(), comment.getStatus().getId(), comment.getId());
                            startActivityForResult(intent, REPLY_COMMENT_REQUEST_CODE);
                        } else if (which == 1) {
                            Intent intent = RelayStatusActivity.newIntent(getActivity(), comment.getStatus(), comment);
                            startActivityForResult(intent, REPLY_COMMENT_REQUEST_CODE);
                        } else if (which == 2) {
                            // 将文本内容放到系统剪贴板里。
                            mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, comment.getText()));
                            ToastUtil.show(getActivity(), "复制成功");
                        }
                    }
                });
            }
        }
    }
}

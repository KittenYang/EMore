package com.caij.emore.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Comment;
import com.caij.emore.present.WeiboCommentsPresent;
import com.caij.emore.present.imp.WeiboCommentsPresentImp;
import com.caij.emore.present.view.WeiboCommentsView;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.activity.publish.ReplyCommentActivity;
import com.caij.emore.ui.activity.publish.RepostWeiboActivity;
import com.caij.emore.ui.adapter.CommentAdapter;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.XRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboCommentListFragment extends RecyclerViewFragment<Comment, WeiboCommentsPresent> implements WeiboCommentsView, XRecyclerView.OnLoadMoreListener, RecyclerViewOnItemClickListener {

    private static final int REPLY_COMMENT_REQUEST_CODE = 100;
    private ClipboardManager mClipboardManager;

    public static WeiboCommentListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putSerializable(Key.ID, weiboId);
        WeiboCommentListFragment fragment = new WeiboCommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());
        mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    protected BaseAdapter<Comment, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new CommentAdapter(getActivity());
    }

    @Override
    protected WeiboCommentsPresent createPresent() {
        AccessToken token = UserPrefs.get().getEMoreToken();
        long weibiId  = getArguments().getLong(Key.ID);
        return new WeiboCommentsPresentImp(token.getAccess_token(), weibiId,
                new ServerWeiboSource(), new LocalWeiboSource(), this);
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
    public void onItemClick(View view, int position) {
        final Comment comment = mRecyclerViewAdapter.getItem(position);
        if (view.getId() == R.id.imgPhoto) {
            Intent intent = UserInfoActivity.newIntent(getActivity(), comment.getUser().getScreen_name());
            startActivity(intent);
        }else {
            if (comment.getUser().getId() == Long.parseLong(UserPrefs.get().getEMoreToken().getUid())) {
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
                            Intent intent = RepostWeiboActivity.newIntent(getActivity(), comment.getStatus(), comment);
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

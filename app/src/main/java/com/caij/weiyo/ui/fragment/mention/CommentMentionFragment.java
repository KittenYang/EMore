package com.caij.weiyo.ui.fragment.mention;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.present.RefreshListPresent;
import com.caij.weiyo.present.imp.CommentMentionPresentImp;
import com.caij.weiyo.present.view.RefreshListView;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.activity.WeiboDetialActivity;
import com.caij.weiyo.ui.activity.publish.ReplyCommentActivity;
import com.caij.weiyo.ui.adapter.MessageCommentAdapter;
import com.caij.weiyo.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.view.recyclerview.BaseAdapter;
import com.caij.weiyo.view.recyclerview.BaseViewHolder;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

/**
 * Created by Caij on 2016/7/4.
 */
public class CommentMentionFragment extends SwipeRefreshRecyclerViewFragment<Comment, RefreshListPresent> implements
        LoadMoreRecyclerView.OnLoadMoreListener, RefreshListView<Comment>, RecyclerViewOnItemClickListener {

    @Override
    protected BaseAdapter<Comment, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new MessageCommentAdapter(getActivity());
    }

    @Override
    public void onEmpty() {

    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    protected RefreshListPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        return new CommentMentionPresentImp(accessToken.getAccess_token(), new ServerWeiboSource(), this);
    }

    @Override
    public void onItemClick(View view, final int position) {
        String[] items = new String[]{"回复评论", "查看微博"};
        DialogUtil.showItemDialog(getActivity(), null, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Comment comment = mRecyclerViewAdapter.getItem(position);
                    Intent intent = ReplyCommentActivity.newIntent(getActivity(),
                            comment.getStatus().getId(), comment.getId());
                    startActivity(intent);
                }else if (which == 1) {
                    Intent intent = WeiboDetialActivity.newIntent(getActivity(),
                            mRecyclerViewAdapter.getItem(position).getStatus());
                    startActivity(intent);
                }
            }
        });
    }
}

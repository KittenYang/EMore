package com.caij.emore.ui.fragment.mention;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.caij.emore.bean.Comment;
import com.caij.emore.present.PublishCommentsPresent;
import com.caij.emore.present.imp.PublishCommentsPresentImp;
import com.caij.emore.remote.imp.CommentApiImp;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.ui.adapter.delegate.CommentMesssageDelegate;
import com.caij.emore.ui.adapter.delegate.MyCommentDelegate;
import com.caij.emore.ui.view.MyPublishCommentsView;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.BaseAdapter;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;

/**
 * Created by Caij on 2016/7/4.
 */
public class PublishCommentsFragment extends AcceptCommentsFragment implements MyPublishCommentsView {

    @Override
    protected BaseAdapter<Comment, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        MultiItemTypeAdapter<Comment> multiItemTypeAdapter = new MultiItemTypeAdapter<Comment>(getActivity());
        multiItemTypeAdapter.addItemViewDelegate(new MyCommentDelegate(this));
        return multiItemTypeAdapter;
    }

    @Override
    protected PublishCommentsPresent createPresent() {
        return new PublishCommentsPresentImp(new CommentApiImp(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onItemClick(View view, final int position) {
        String[] items = new String[]{"删除", "查看微博"};
        DialogUtil.showItemDialog(getActivity(), null, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    ((PublishCommentsPresent)mPresent).deleteComment(mRecyclerViewAdapter.getItem(position), position);
                }else if (which == 1) {
                    Intent intent = StatusDetailActivity.newIntent(getActivity(),
                            mRecyclerViewAdapter.getItem(position).getStatus().getId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onDeleteCommentSuccess(Comment comment, int position) {
        mRecyclerViewAdapter.removeEntity(comment);
        mRecyclerViewAdapter.notifyItemRemoved(position);
    }

}

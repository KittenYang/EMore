package com.caij.emore.ui.fragment.mention;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Comment;
import com.caij.emore.present.PublishCommentsPresent;
import com.caij.emore.present.imp.PublishCommentsPresentImp;
import com.caij.emore.present.view.MyPublishComentsView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.ui.adapter.MyMessageCommentAdapter;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;

/**
 * Created by Caij on 2016/7/4.
 */
public class PublishCommentsFragment extends SwipeRefreshRecyclerViewFragment<Comment, PublishCommentsPresent> implements LoadMoreRecyclerView.OnLoadMoreListener,  MyPublishComentsView {

    @Override
    protected BaseAdapter<Comment, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new MyMessageCommentAdapter(getActivity());
    }

    @Override
    protected PublishCommentsPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getEMoreToken();
        return new PublishCommentsPresentImp(accessToken.getAccess_token(), new ServerWeiboSource(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onItemClick(View view, final int position) {
        String[] items = new String[]{"删除", "查看微博"};
        DialogUtil.showItemDialog(getActivity(), null, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    mPresent.deleteComment(mRecyclerViewAdapter.getItem(position), position);
                }else if (which == 1) {
                    Intent intent = WeiboDetialActivity.newIntent(getActivity(),
                            mRecyclerViewAdapter.getItem(position).getStatus());
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

    @Override
    public void onEmpty() {

    }
}

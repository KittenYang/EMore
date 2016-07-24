package com.caij.emore.ui.fragment.mention;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Comment;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.imp.AcceptCommentsPresentImp;
import com.caij.emore.present.view.RefreshListView;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.ui.activity.publish.ReplyCommentActivity;
import com.caij.emore.ui.adapter.MessageCommentAdapter;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;


/**
 * Created by Caij on 2016/7/4.
 */
public class AcceptCommentsFragment extends SwipeRefreshRecyclerViewFragment<Comment, RefreshListPresent> implements
        LoadMoreRecyclerView.OnLoadMoreListener,RecyclerViewOnItemClickListener, RefreshListView<Comment> {


    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
    }

    @Override
    protected BaseAdapter createRecyclerViewAdapter() {
        return  new MessageCommentAdapter(getActivity());
    }

    protected RefreshListPresent createPresent() {
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        return new AcceptCommentsPresentImp(accessToken.getAccess_token(), new ServerWeiboSource(),
                new ServerMessageSource(), new LocalMessageSource(), this);
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
                            mRecyclerViewAdapter.getItem(position).getStatus().getId());
                    startActivity(intent);
                }
            }
        });
    }
}

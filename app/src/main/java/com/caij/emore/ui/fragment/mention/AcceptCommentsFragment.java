package com.caij.emore.ui.fragment.mention;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.caij.emore.account.Account;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Comment;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.imp.AcceptCommentsPresentImp;
import com.caij.emore.remote.imp.CommentApiImp;
import com.caij.emore.remote.imp.NotifyApiImp;
import com.caij.emore.ui.view.RefreshListView;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.ui.activity.publish.ReplyCommentActivity;
import com.caij.emore.ui.adapter.MessageCommentAdapter;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;


/**
 * Created by Caij on 2016/7/4.
 */
public class AcceptCommentsFragment extends SwipeRefreshRecyclerViewFragment<Comment, RefreshListPresent> implements
        XRecyclerView.OnLoadMoreListener,RecyclerViewOnItemClickListener, RefreshListView<Comment> {

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
    protected BaseAdapter createRecyclerViewAdapter() {
        return  new MessageCommentAdapter(getActivity());
    }

    protected RefreshListPresent createPresent() {
        Account account = UserPrefs.get(getActivity()).getAccount();
        return new AcceptCommentsPresentImp(account, new CommentApiImp(),
                new NotifyApiImp(), new NotifyManagerImp(), this);
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
                    Intent intent = StatusDetailActivity.newIntent(getActivity(),
                            mRecyclerViewAdapter.getItem(position).getStatus().getId());
                    startActivity(intent);
                }
            }
        });
    }
}

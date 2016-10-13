package com.caij.emore.ui.fragment.weibo;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.caij.emore.account.UserPrefs;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.TimeLinePresent;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.ui.adapter.StatusAdapter;
import com.caij.emore.ui.view.TimeLineStatusView;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/6/4.
 */
public abstract class TimeLineStatusFragment<P extends TimeLinePresent> extends SwipeRefreshRecyclerViewFragment<Status, P>
        implements TimeLineStatusView, RecyclerViewOnItemClickListener, XRecyclerView.OnLoadMoreListener, StatusAdapter.OnItemActionClickListener {

    @Override
    protected BaseAdapter<Status, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return new StatusAdapter(getActivity(), this, this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = StatusDetailActivity.newIntent(getActivity(), mRecyclerViewAdapter.getItem(position).getId());
        startActivity(intent);
    }

    @Override
    public void onLoadComplete(boolean isHaveMore) {
        xRecyclerView.completeLoading(isHaveMore);
    }

    private void onMenuClick(final Status weibo, final int position) {
        List<String> items = new ArrayList<>();
        if (weibo.getFavorited()) {
            items.add("取消收藏");
        }else {
            items.add("收藏");
        }
        long uid = Long.parseLong(UserPrefs.get(getActivity()).getToken().getUid());
        if (weibo.getUser().getId() == uid) {
            items.add("删除");
        }
        String[] array = new String[items.size()];
        DialogUtil.showItemDialog(getActivity(), null, items.toArray(array), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (weibo.getFavorited()) {
                            uncollectWeibo(weibo);
                        }else {
                            collectWeibo(weibo);
                        }
                        break;

                    case 1:
                        deleteWeibo(weibo, position);
                        break;
                }
            }
        });
    }

    private void deleteWeibo(Status weibo, int position) {
        mPresent.deleteStatus(weibo, position);
    }

    private void collectWeibo(Status weibo) {
        mPresent.collectStatus(weibo);
    }

    private void uncollectWeibo(Status weibo) {
        mPresent.unCollectStatus(weibo);
    }

    @Override
    public void onDeleteStatusSuccess(Status weibo, int position) {
        mRecyclerViewAdapter.removeEntity(weibo);
        mRecyclerViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onCollectSuccess(Status weibo) {
        weibo.setFavorited(true);
    }

    @Override
    public void onUnCollectSuccess(Status weibo) {
        weibo.setFavorited(false);
    }

    @Override
    public void onMenuClick(View v, int position) {
        onMenuClick(mRecyclerViewAdapter.getItem(position), position);
    }

    @Override
    public void onLikeClick(View v, int position) {
        if (WeicoAuthUtil.checkWeicoLogin(this, false)) {
            Status weibo = mRecyclerViewAdapter.getItem(position);
            if (weibo.getAttitudes_status() == 1) {
                mPresent.destroyAttitudeStatus(weibo);
            }else {
                mPresent.attitudeStatus(weibo);
            }
        }
    }

}
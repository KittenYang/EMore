package com.caij.emore.ui.fragment.weibo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.TimeLinePresent;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.ui.adapter.StatusAdapter;
import com.caij.emore.ui.view.TimeLineStatusView;
import com.caij.emore.ui.fragment.SwipeRefreshRecyclerViewFragment;
import com.caij.emore.utils.AnimUtil;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.value;

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

    private void onMenuClick(final Status status, final int position) {
        List<String> items = new ArrayList<>();
        if (status.getFavorited()) {
            items.add(getString(R.string.cancel_collection));
        }else {
            items.add(getString(R.string.collection));
        }
        long uid = Long.parseLong(UserPrefs.get(getActivity()).getToken().getUid());
        if (status.getUser().getId() == uid) {
            items.add(getString(R.string.delete));
        }
        String[] array = new String[items.size()];
        DialogUtil.showItemDialog(getActivity(), null, items.toArray(array), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (status.getFavorited()) {
                            unCollectStatus(status);
                        }else {
                            collectStatus(status);
                        }
                        break;

                    case 1:
                        deleteStatus(status, position);
                        break;
                }
            }
        });
    }

    private void deleteStatus(Status status, int position) {
        mPresent.deleteStatus(status, position);
    }

    private void collectStatus(Status status) {
        mPresent.collectStatus(status);
    }

    private void unCollectStatus(Status status) {
        mPresent.unCollectStatus(status);
    }

    @Override
    public void onDeleteStatusSuccess(Status status, int position) {
        mRecyclerViewAdapter.removeEntity(status);
        mRecyclerViewAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onCollectSuccess(Status status) {
        status.setFavorited(true);
    }

    @Override
    public void onUnCollectSuccess(Status status) {
        status.setFavorited(false);
    }

    @Override
    public void onMenuClick(View v, int position) {
        onMenuClick(mRecyclerViewAdapter.getItem(position), position);
    }

    @Override
    public void onStatusAttitudeUpdate(Status status, int index) {
        RecyclerView.ViewHolder viewHolder = findViewHolder(index);
        if (viewHolder != null && viewHolder instanceof StatusAdapter.WeiboBaseViewHolder) {
            ((StatusAdapter.WeiboBaseViewHolder) viewHolder).onStatusAttitudeUpdate(status);
        }
    }

    @Override
    public void onStatusAttitudeCountUpdate(Status status, int index) {
        RecyclerView.ViewHolder viewHolder = findViewHolder(index);
        if (viewHolder != null && viewHolder instanceof StatusAdapter.WeiboBaseViewHolder) {
            ((StatusAdapter.WeiboBaseViewHolder) viewHolder).onStatusAttitudeUpdate(status);
        }
    }

    @Override
    public void onStatusCommentCountUpdate(Status status, int index) {
        RecyclerView.ViewHolder viewHolder = findViewHolder(index);
        if (viewHolder != null && viewHolder instanceof StatusAdapter.WeiboBaseViewHolder) {
            ((StatusAdapter.WeiboBaseViewHolder) viewHolder).onStatusCommentCountUpdate(status);
        }
    }

    @Override
    public void onStatusRelayCountUpdate(Status status, int index) {
        RecyclerView.ViewHolder viewHolder = findViewHolder(index);
        if (viewHolder != null && viewHolder instanceof StatusAdapter.WeiboBaseViewHolder) {
            ((StatusAdapter.WeiboBaseViewHolder) viewHolder).onStatusRelayCountUpdate(status);
        }
    }

    private RecyclerView.ViewHolder findViewHolder(int index) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) xRecyclerView.getLayoutManager();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleFeedPosition = linearLayoutManager.findLastVisibleItemPosition();

        HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter = xRecyclerView.getAdapter();

        if (firstVisibleItemPosition <= index + headerAndFooterRecyclerViewAdapter.getHeaderViewsCount()
                && index + headerAndFooterRecyclerViewAdapter.getHeaderViewsCount() <= lastVisibleFeedPosition) {
            //得到要更新的item的view
            View view = xRecyclerView.getRecyclerView().getChildAt(index - firstVisibleItemPosition + headerAndFooterRecyclerViewAdapter.getHeaderViewsCount());

            return xRecyclerView.getRecyclerView().getChildViewHolder(view);
        }

        return null;
    }

    @Override
    public void onLikeClick(View v, int position) {
        if (WeicoAuthUtil.checkWeicoLogin(this, false)) {
            Status status = mRecyclerViewAdapter.getItem(position);
            if (status.getAttitudes_status() == 1) {
                mPresent.destroyAttitudeStatus(status);
                v.setSelected(false);
                AnimUtil.scale(v, 1f, 1.2f, 1f);
            }else {
                mPresent.attitudeStatus(status);
                v.setSelected(true);
                AnimUtil.scale(v, 1f, 1.2f, 1f);
            }
        }
    }

}

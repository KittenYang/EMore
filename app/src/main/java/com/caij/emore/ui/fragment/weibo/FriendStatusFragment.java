package com.caij.emore.ui.fragment.weibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.caij.emore.EventTag;
import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.manager.imp.StatusManagerImp;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.FriendStatusPresent;
import com.caij.emore.present.imp.FriendStatusPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.brige.ToolbarDoubleClick;
import com.caij.emore.ui.view.FriendStatusView;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.SearchRecommendActivity;
import com.caij.emore.ui.activity.publish.PublishStatusActivity;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/6/4.
 */
public class FriendStatusFragment extends TimeLineStatusFragment<FriendStatusPresent> implements RecyclerViewOnItemClickListener,
        XRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, FriendStatusView{

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected FriendStatusPresent createPresent() {
       return new FriendStatusPresentImp(UserPrefs.get(getActivity()).getAccount().getUid(), this,
                new StatusApiImp(), new StatusManagerImp(), new AttitudeApiImp(), new NotifyManagerImp());
    }

    @Override
    public void toRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresent.refresh();
            }
        });
    }

    @Override
    public void onStatusPublishSuccess(Status status) {
        mRecyclerViewAdapter.addEntity(0, status);
        mRecyclerViewAdapter.notifyItemInserted(0);
        LinearLayoutManager manager = (LinearLayoutManager) xRecyclerView.getLayoutManager();
        if (manager.findFirstVisibleItemPosition() < 2) {
            xRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onUserFirstVisible() {
    }

    @Override
    protected void setEmptyText(TextView textView) {
        textView.setText(R.string.friend_weibo_empty_hint);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = DefaultFragmentActivity.starFragmentV4(getActivity(), getString(R.string.hot_weibo),
                        HotStatusFragment.class, null);
                startActivity(intent);
            }
        });
    }

}

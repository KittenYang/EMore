package com.caij.emore.ui.fragment.weibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.present.imp.FriendWeiboPresentImp;
import com.caij.emore.present.view.FriendWeiboView;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.publish.PublishWeiboActivity;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/6/4.
 */
public class FriendWeiboFragment extends TimeLineWeiboFragment<FriendWeiboPresent> implements
        RecyclerViewOnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, FriendWeiboView {

    @BindView(R.id.swipe_refresh_layout)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.include_refresh_recycle_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));
        Observable<Activity> observable = RxBus.get().register(Key.EVENT_TOOL_BAR_DOUBLE_CLICK);
        observable.subscribe(new Action1<Activity>() {
            @Override
            public void call(Activity activity) {
                if (getActivity() == activity) {
                    mLoadMoreLoadMoreRecyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    @Override
    protected FriendWeiboPresent createPresent() {
        AccessToken token = UserPrefs.get().getEMoreToken();
       return new FriendWeiboPresentImp(token.getAccess_token(), this,
                new ServerWeiboSource(), new LocalWeiboSource());
    }


    @Override
    public void toRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mPresent.refresh();
    }

    @Override
    public void onWeiboPublishSuccess(Weibo weibo) {
        mRecyclerViewAdapter.addEntity(0, weibo);
        mRecyclerViewAdapter.notifyItemInserted(0);
        LinearLayoutManager manager = (LinearLayoutManager) mLoadMoreLoadMoreRecyclerView.getLayoutManager();
        if (manager.findFirstVisibleItemPosition() < 2) {
            mLoadMoreLoadMoreRecyclerView.smoothScrollToPosition(0);
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
    public void onEmpty() {

    }

    @Override
    public void onRefresh() {
        mPresent.refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.publish:
                Intent intent = new Intent(getActivity(), PublishWeiboActivity.class);
                startActivity(intent);
                break;

            case R.id.search:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

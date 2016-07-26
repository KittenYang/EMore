package com.caij.emore.ui.fragment.weibo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.FriendWeiboPresent;
import com.caij.emore.present.imp.FriendWeiboPresentImp;
import com.caij.emore.present.view.FriendWeiboView;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.SearchRecommendActivity;
import com.caij.emore.ui.activity.publish.PublishWeiboActivity;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.caij.emore.view.recyclerview.RecyclerViewOnItemClickListener;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/6/4.
 */
public class FriendWeiboFragment extends TimeLineWeiboFragment<FriendWeiboPresent> implements
        RecyclerViewOnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, FriendWeiboView {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Observable<Object> observable = RxBus.get().register(Key.EVENT_TOOL_BAR_DOUBLE_CLICK);
        observable.subscribe(new Action1<Object>() {
            @Override
            public void call(Object object) {
                if (object == FriendWeiboFragment.this) {
                    mLoadMoreLoadMoreRecyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    @Override
    protected FriendWeiboPresent createPresent() {
       return new FriendWeiboPresentImp( UserPrefs.get().getAccount(), this,
                new ServerWeiboSource(), new LocalWeiboSource(), new LocalMessageSource());
    }

    @Override
    public void toRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresent.refresh();
            }
        });
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
                Intent intent1 = new Intent(getActivity(), SearchRecommendActivity.class);
                startActivity(intent1);
                getActivity().overridePendingTransition(-1, -1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

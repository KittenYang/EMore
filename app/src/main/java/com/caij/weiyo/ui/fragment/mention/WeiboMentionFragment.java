package com.caij.weiyo.ui.fragment.mention;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.WeiboMentionPresent;
import com.caij.weiyo.present.imp.WeiboMentionPresentImp;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.fragment.weibo.TimeLineWeiboFragment;
import com.caij.weiyo.view.recyclerview.BaseAdapter;
import com.caij.weiyo.view.recyclerview.BaseViewHolder;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;
import com.caij.weiyo.view.recyclerview.RecyclerViewOnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/4.
 */
public class WeiboMentionFragment extends TimeLineWeiboFragment<WeiboMentionPresent> implements
        RecyclerViewOnItemClickListener, LoadMoreRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

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
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));
    }


    @Override
    protected WeiboMentionPresent createPresent() {
        AccessToken token = UserPrefs.get().getWeiCoToken();
       return new WeiboMentionPresentImp(token.getAccess_token(), new ServerWeiboSource(), this);
    }

    @Override
    public void onRefresh() {
        mPresent.refresh();
    }


    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}

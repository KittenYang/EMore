package com.caij.emore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.present.imp.WeiboRepostsPresentImp;
import com.caij.emore.present.view.WeiboRepostsView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.adapter.RepostAdapter;
import com.caij.emore.view.recyclerview.BaseAdapter;
import com.caij.emore.view.recyclerview.BaseViewHolder;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboRepostListFragment extends RecyclerViewFragment<Weibo, WeiboRepostsPresent> implements WeiboRepostsView,
        LoadMoreRecyclerView.OnLoadMoreListener {

    public static WeiboRepostListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, weiboId);
        WeiboRepostListFragment fragment = new WeiboRepostListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadMoreLoadMoreRecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
        mLoadMoreLoadMoreRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());
    }

    @Override
    protected BaseAdapter<Weibo, ? extends BaseViewHolder> createRecyclerViewAdapter() {
       return  new RepostAdapter(getActivity());
    }

    @Override
    protected WeiboRepostsPresent createPresent() {
        AccessToken token = UserPrefs.get().getWeiCoToken();
        long weiId = getArguments().getLong(Key.ID);
        return  new WeiboRepostsPresentImp(token.getAccess_token(), weiId,
                new ServerWeiboSource(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
    }


    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
        // TODO: 2016/6/16
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}

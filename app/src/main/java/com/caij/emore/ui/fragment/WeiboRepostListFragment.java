package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboRepostsPresent;
import com.caij.emore.present.imp.WeiboRepostsPresentImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.view.WeiboRepostsView;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.adapter.RepostAdapter;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.OnScrollListener;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboRepostListFragment extends RecyclerViewFragment<Weibo, WeiboRepostsPresent> implements WeiboRepostsView,
        XRecyclerView.OnLoadMoreListener {

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
        xRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                color(getResources().getColor(R.color.divider_timeline_item))
                .size(getResources().getDimensionPixelSize(R.dimen.divider)).build());

        xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (getActivity() instanceof OnScrollListener) {
                    ((OnScrollListener) getActivity()).onScrolled(recyclerView, dx, dy);
                }
            }
        });
    }

    @Override
    protected BaseAdapter<Weibo, ? extends BaseViewHolder> createRecyclerViewAdapter() {
       return  new RepostAdapter(getActivity());
    }

    @Override
    protected WeiboRepostsPresent createPresent() {
        long weiId = getArguments().getLong(Key.ID);
        return  new WeiboRepostsPresentImp(weiId,
                new StatusApiImp(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
    }

    @Override
    protected void onReLoadBtnClick() {
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
        mPresent.userFirstVisible();
    }

    @Override
    protected void setEmptyText(TextView textView) {
        textView.setText(R.string.repost_empty);
    }

    @Override
    public void onItemClick(View view, int position) {
        Weibo weibo = mRecyclerViewAdapter.getItem(position);
        if (view.getId() == R.id.imgPhoto) {
            Intent intent = UserInfoActivity.newIntent(getActivity(), weibo.getUser().getScreen_name());
            startActivity(intent);
        }else {

        }
    }

    @Override
    public void onRepostWeiboSuccess(List<Weibo> weobos) {
        mRecyclerViewAdapter.setEntities(weobos);
        mRecyclerViewAdapter.notifyItemInserted(0);
        LinearLayoutManager manager = (LinearLayoutManager) xRecyclerView.getLayoutManager();
        if (manager.findFirstVisibleItemPosition() < 2) {
            xRecyclerView.smoothScrollToPosition(0);
        }
    }
}

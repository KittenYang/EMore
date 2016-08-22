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
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Attitude;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.imp.WeiboAttitudesPresentImp;
import com.caij.emore.ui.view.WeiboAttitudesView;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.adapter.AttitudeAdapter;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.OnScrollListener;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

/**
 * Created by Caij on 2016/6/14.
 */
public class WeiboLikerListFragment extends RecyclerViewFragment<Attitude, ListPresent> implements WeiboAttitudesView {

    public static WeiboLikerListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, weiboId);
        WeiboLikerListFragment fragment = new WeiboLikerListFragment();
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
    protected BaseAdapter<Attitude, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new AttitudeAdapter(getActivity());
    }

    @Override
    protected ListPresent createPresent() {
        Token token = UserPrefs.get(getActivity()).getWeiCoToken();
        long weiId = getArguments().getLong(Key.ID);
        return  new WeiboAttitudesPresentImp(token.getAccess_token(), weiId,
                new ServerWeiboSource(), new LocalWeiboSource(), this);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
    }

    @Override
    protected void setEmptyText(TextView textView) {
        textView.setText(R.string.attitude_empty);
    }

    @Override
    protected void onReLoadBtnClick() {
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
        mPresent.userFirstVisible();
    }

    @Override
    public void onItemClick(View view, int position) {
        Attitude attitude = mRecyclerViewAdapter.getItem(position);
        Intent intent = UserInfoActivity.newIntent(getActivity(), attitude.getUser().getScreen_name());
        startActivity(intent);
    }

    @Override
    public void onAttitudeSuccess(List<Attitude> attitudes) {
        mRecyclerViewAdapter.setEntities(attitudes);
        mRecyclerViewAdapter.notifyItemInserted(0);
        LinearLayoutManager manager = (LinearLayoutManager) xRecyclerView.getLayoutManager();
        if (manager.findFirstVisibleItemPosition() < 2) {
            xRecyclerView.smoothScrollToPosition(0);
        }
    }
}

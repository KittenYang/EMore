package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.ListPresent;
import com.caij.emore.present.imp.StatusAttitudesPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.ui.view.StatusAttitudesView;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.adapter.AttitudeAdapter;
import com.caij.emore.widget.recyclerview.BaseAdapter;
import com.caij.emore.widget.recyclerview.BaseViewHolder;
import com.caij.emore.widget.recyclerview.OnScrollListener;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;


/**
 * Created by Caij on 2016/6/14.
 */
public class StatusLikerListFragment extends RecyclerViewFragment<User, ListPresent> implements StatusAttitudesView {

    public static StatusLikerListFragment newInstance(long weiboId) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, weiboId);
        StatusLikerListFragment fragment = new StatusLikerListFragment();
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
    protected BaseAdapter<User, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        return  new AttitudeAdapter(getActivity());
    }

    @Override
    protected ListPresent createPresent() {
        long statusId = getArguments().getLong(Key.ID);
        return  new StatusAttitudesPresentImp(statusId, new AttitudeApiImp(), this);
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
        User user = mRecyclerViewAdapter.getItem(position);
        Intent intent = UserInfoActivity.newIntent(getActivity(), user.getScreen_name());
        startActivity(intent);
    }

}

package com.caij.emore.ui.fragment.weibo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.manager.imp.StatusManagerImp;
import com.caij.emore.present.UserStatusPresent;
import com.caij.emore.present.imp.UserStatusesPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.widget.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.caij.emore.widget.recyclerview.XRecyclerView;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserStatusFragment extends TimeLineStatusFragment<UserStatusPresent> implements View.OnClickListener, DialogInterface.OnClickListener {

    private Dialog mFilterDialog;
    private HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter;

    public static UserStatusFragment newInstance(long uid) {
        Bundle args = new Bundle();
        args.putLong(Key.ID, uid);
        UserStatusFragment fragment = new UserStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setEnabled(false);
        headerAndFooterRecyclerViewAdapter = xRecyclerView.getAdapter();
        View headView = getActivity().getLayoutInflater().
                inflate(R.layout.header_view_profile_weibo, xRecyclerView, false);
        headerAndFooterRecyclerViewAdapter.addHeaderView(headView);
        headView.findViewById(R.id.txtName).setOnClickListener(this);
    }

    @Override
    protected UserStatusPresent createPresent() {
        long uid = getArguments().getLong(Key.ID);
        return new UserStatusesPresentImp(uid, this,
                new StatusApiImp(), new StatusManagerImp(), new AttitudeApiImp());
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
    }

    @Override
    public void onClick(View v) {
        if (mFilterDialog == null) {
            mFilterDialog = DialogUtil.showSingleSelectDialog(getActivity(), "微博类型",
                    getResources().getStringArray(R.array.user_headers), 0, this);
        }else {
            mFilterDialog.show();
        }
    }

    @Override
    protected void onReLoadBtnClick() {
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
        mPresent.userFirstVisible();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mFilterDialog.dismiss();
        mPresent.filter(which);
        xRecyclerView.setFooterState(XRecyclerView.STATE_LOADING);
    }

    @Override
    public void onItemClick(View view, int position) {
        //这里因为加了head  现在需要 -1
        super.onItemClick(view, position - headerAndFooterRecyclerViewAdapter.getHeaderViewsCount());
    }

    @Override
    public void onClick(View view, int position) {
        super.onClick(view, position - headerAndFooterRecyclerViewAdapter.getHeaderViewsCount());
    }

    @Override
    public void onRefreshComplete() {

    }
}

package com.caij.emore.ui.fragment.weibo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.present.UserWeiboPresent;
import com.caij.emore.present.imp.UserWeiboPresentImp;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.widget.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.caij.emore.widget.recyclerview.XRecyclerView;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserWeiboFragment extends TimeLineWeiboFragment<UserWeiboPresent> implements View.OnClickListener, DialogInterface.OnClickListener {

    private Dialog mFilterDialog;

    public static UserWeiboFragment newInstance(String username) {
        Bundle args = new Bundle();
        args.putString(Key.USERNAME, username);
        UserWeiboFragment fragment = new UserWeiboFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setEnabled(false);
        HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter = xRecyclerView.getAdapter();
        View headView = getActivity().getLayoutInflater().
                inflate(R.layout.header_view_profile_weibo, xRecyclerView, false);
        headerAndFooterRecyclerViewAdapter.addHeaderView(headView);
        headView.findViewById(R.id.txtName).setOnClickListener(this);
    }

    @Override
    protected UserWeiboPresent createPresent() {
        String username = getArguments().getString(Key.USERNAME);
        return new UserWeiboPresentImp(UserPrefs.get().getAccount(), username, this, new ServerWeiboSource(), new LocalWeiboSource());
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
        super.onItemClick(view, position - 1);
    }

    @Override
    public void onRefreshComplete() {

    }
}

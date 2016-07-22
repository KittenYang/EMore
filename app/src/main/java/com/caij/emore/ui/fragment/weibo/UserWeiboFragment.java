package com.caij.emore.ui.fragment.weibo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.present.UserWeiboPresent;
import com.caij.emore.present.imp.UserWeiboPresentImp;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.view.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.caij.emore.view.recyclerview.LoadMoreRecyclerView;

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
        HeaderAndFooterRecyclerViewAdapter headerAndFooterRecyclerViewAdapter = mLoadMoreLoadMoreRecyclerView.getAdapter();
        View headView = getActivity().getLayoutInflater().
                inflate(R.layout.header_view_profile_weibo, mLoadMoreLoadMoreRecyclerView, false);
        headerAndFooterRecyclerViewAdapter.addHeaderView(headView);
        headView.findViewById(R.id.txtName).setOnClickListener(this);
    }

    @Override
    protected UserWeiboPresent createPresent() {
        String username = getArguments().getString(Key.USERNAME);
        return new UserWeiboPresentImp(UserPrefs.get().getAccount(), username, this, new ServerWeiboSource(), new LocalWeiboSource());
    }

    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
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
    public void onClick(DialogInterface dialog, int which) {
        mFilterDialog.dismiss();
        mPresent.filter(which);
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
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

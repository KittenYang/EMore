package com.caij.weiyo.ui.fragment.weibo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.present.UserWeiboPresent;
import com.caij.weiyo.present.imp.UserWeiboPresentImp;
import com.caij.weiyo.source.server.ServerUserSource;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.ui.activity.LoginActivity;
import com.caij.weiyo.utils.DensityUtil;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.view.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.caij.weiyo.view.recyclerview.LoadMoreRecyclerView;

import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserWeiboFragment extends TimeLineWeiboFragment<UserWeiboPresent> implements View.OnClickListener, DialogUtil.SingleSelectListener {

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
        AccessToken accessToken = UserPrefs.get().getWeiCoToken();
        String username = getArguments().getString(Key.USERNAME);
        return new UserWeiboPresentImp(accessToken.getAccess_token(), username, this, new ServerWeiboSource());
    }

    @Override
    public void onEmpty() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_EMPTY);
    }

    @Override
    protected void onUserFirstVisible() {
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
        mTimeLineWeiboPresent.onFirstVisible();
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
    public void onSelect(int position) {
        mFilterDialog.dismiss();
        mTimeLineWeiboPresent.filter(position);
        mLoadMoreLoadMoreRecyclerView.setFooterState(LoadMoreRecyclerView.STATE_LOADING);
    }
}

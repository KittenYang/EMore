package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.account.Account;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.present.RefreshListPresent;
import com.caij.emore.present.imp.AttitudesToMePresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.NotifyApiImp;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.ui.activity.publish.ReplyCommentActivity;
import com.caij.emore.ui.adapter.delegate.ToMeAttitudeDelegate;
import com.caij.emore.widget.recyclerview.OnItemPartViewClickListener;
import com.caij.rvadapter.BaseViewHolder;
import com.caij.rvadapter.adapter.BaseAdapter;
import com.caij.rvadapter.adapter.MultiItemTypeAdapter;


/**
 * Created by Caij on 2016/7/4.
 */
public class AttitudesToMeFragment extends SwipeRefreshRecyclerViewFragment<Attitude, RefreshListPresent> implements OnItemPartViewClickListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected BaseAdapter<Attitude, ? extends BaseViewHolder> createRecyclerViewAdapter() {
        MultiItemTypeAdapter<Attitude> multiItemTypeAdapter = new MultiItemTypeAdapter<Attitude>(getActivity());
        multiItemTypeAdapter.addItemViewDelegate(new ToMeAttitudeDelegate(this));
        return multiItemTypeAdapter;
    }

    protected RefreshListPresent createPresent() {
        Account account = UserPrefs.get(getActivity()).getAccount();
        return new AttitudesToMePresentImp(account, new AttitudeApiImp(),
                new NotifyApiImp(), new NotifyManagerImp(), this);
    }


    @Override
    public void onItemClick(View view, final int position) {
        Intent intent = StatusDetailActivity.newIntent(getActivity(),
                mRecyclerViewAdapter.getItem(position).getStatus().getId());
        startActivity(intent);
    }

    public void onClick(View view, int position) {
        Attitude attitude = mRecyclerViewAdapter.getItem(position);
        switch (view.getId()) {
            case R.id.item_bottom:
                Intent intent = StatusDetailActivity.newIntent(getActivity(), attitude.getStatus().getId());
                startActivity(intent);
                break;

            case R.id.sdv_avatar:
                intent = UserInfoActivity.newIntent(getActivity(), attitude.getUser().getScreen_name());
                startActivity(intent);
                break;
        }
    }
}

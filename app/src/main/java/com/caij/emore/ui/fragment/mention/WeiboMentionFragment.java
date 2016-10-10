package com.caij.emore.ui.fragment.mention;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.caij.emore.account.UserPrefs;
import com.caij.emore.dao.imp.NotifyManagerImp;
import com.caij.emore.dao.imp.StatusManagerImp;
import com.caij.emore.present.WeiboMentionPresent;
import com.caij.emore.present.imp.WeiboMentionPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.remote.imp.UnReadMessageApiImp;
import com.caij.emore.ui.fragment.weibo.TimeLineWeiboFragment;
import com.caij.emore.widget.recyclerview.XRecyclerView;
import com.caij.emore.widget.recyclerview.RecyclerViewOnItemClickListener;

/**
 * Created by Caij on 2016/6/4.
 */
public class WeiboMentionFragment extends TimeLineWeiboFragment<WeiboMentionPresent> implements
        RecyclerViewOnItemClickListener, XRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected WeiboMentionPresent createPresent() {
       return new WeiboMentionPresentImp(UserPrefs.get(getActivity()).getAccount().getUid(), this,
               new StatusApiImp(), new StatusManagerImp(), new AttitudeApiImp(),
               new UnReadMessageApiImp(), new NotifyManagerImp());
    }

    @Override
    protected void onUserFirstVisible() {
        super.onUserFirstVisible();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onRefreshComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}

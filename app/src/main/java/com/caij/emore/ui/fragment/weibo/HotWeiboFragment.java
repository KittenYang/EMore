package com.caij.emore.ui.fragment.weibo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.HotWeiboPresent;
import com.caij.emore.present.imp.HotWeiboPresentImp;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;

/**
 * Created by Caij on 2016/7/25.
 */
public class HotWeiboFragment extends TimeLineWeiboFragment<HotWeiboPresent> {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresent.refresh();
            }
        });
    }

    @Override
    protected HotWeiboPresent createPresent() {
        return new HotWeiboPresentImp(UserPrefs.get(getActivity()).getAccount(), this,
                new ServerWeiboSource(), new LocalWeiboSource());
    }

}

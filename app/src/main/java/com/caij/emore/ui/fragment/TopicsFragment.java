package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.UserPrefs;
import com.caij.emore.present.imp.TopicPresentImp;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.fragment.weibo.TimeLineWeiboFragment;

/**
 * Created by Caij on 2016/7/25.
 */
public class TopicsFragment extends TimeLineWeiboFragment<TopicPresentImp> {

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
    protected TopicPresentImp createPresent() {
        String key  = getArguments().getString(Key.ID);
        return new TopicPresentImp(UserPrefs.get().getAccount(), key,
                new ServerWeiboSource(), new LocalWeiboSource(), this);
    }

}

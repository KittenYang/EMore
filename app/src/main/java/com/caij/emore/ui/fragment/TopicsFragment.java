package com.caij.emore.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.manager.imp.StatusManagerImp;
import com.caij.emore.present.imp.TopicPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.fragment.weibo.TimeLineStatusFragment;

/**
 * Created by Caij on 2016/7/25.
 */
public class TopicsFragment extends TimeLineStatusFragment<TopicPresentImp> {

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
        return new TopicPresentImp(key, this, new StatusApiImp(),
                new StatusManagerImp(), new AttitudeApiImp());
    }

}

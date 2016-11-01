package com.caij.emore.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.manager.imp.StatusManagerImp;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.StatusAndUserSearchPresent;
import com.caij.emore.present.imp.StatusAndUserSearchPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.remote.imp.UserApiImp;
import com.caij.emore.ui.fragment.weibo.TimeLineStatusFragment;
import com.caij.emore.ui.view.StatusAndUserSearchView;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.utils.ImageLoader;

import java.util.List;

/**
 * Created by Caij on 2016/7/26.
 */
public class StatusAndUserSearchFragment extends TimeLineStatusFragment<StatusAndUserSearchPresent> implements StatusAndUserSearchView {

    private LinearLayout mUserlinearLayout;
    private ImageLoader.ImageConfig mUserImageConfig;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mPresent.refresh();
            }
        });
    }

    private View createHeadUserView() {
        View headUserView = getActivity().getLayoutInflater().inflate(R.layout.item_search_head, xRecyclerView, false);
        mUserlinearLayout = (LinearLayout) headUserView.findViewById(R.id.ll_user);
        mUserImageConfig = new ImageLoader.ImageConfigBuild().setCircle(true)
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP)
                .build();
        return headUserView;
    }

    @Override
    protected StatusAndUserSearchPresent createPresent() {
        String key = getArguments().getString(Key.ID);
        return new StatusAndUserSearchPresentImp(key, this,
                new StatusApiImp(), new StatusManagerImp(), new AttitudeApiImp(), new UserApiImp());
    }

    @Override
    public void setUsers(List<User> users) {
        int length = Math.min(6, users.size());
        if (length > 0) {
            xRecyclerView.getAdapter().addHeaderView(createHeadUserView());
            for (int i = 0; i < length; i ++) {
                mUserlinearLayout.addView(createUserView(users.get(i)));
            }
        }
    }

    private View createUserView(final User user) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_search_user, mUserlinearLayout, false);
        TextView name = (TextView) view.findViewById(R.id.tv_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_avatar);
        name.setText(user.getScreen_name());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = UserInfoActivity.newIntent(getActivity(), user.getScreen_name());
                startActivity(intent);
            }
        });
        ImageLoader.loadUrl(getActivity(), imageView, user.getAvatar_large(), R.drawable.circle_image_placeholder, mUserImageConfig);
        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position - 1);
    }
}

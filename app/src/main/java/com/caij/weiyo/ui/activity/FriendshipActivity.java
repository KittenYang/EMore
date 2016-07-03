package com.caij.weiyo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.weiyo.ui.fragment.BaseFragment;
import com.caij.weiyo.ui.fragment.friendship.FollowsFragment;
import com.caij.weiyo.ui.fragment.friendship.FriendFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/3.
 */
public class FriendshipActivity extends BaseToolBarActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    public static Intent newIntent(Context context, long uid) {
        Intent intent = new Intent(context, FriendshipActivity.class);
        intent.putExtra(Key.ID, uid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        long uid = getIntent().getLongExtra(Key.ID, -1);
        setTitle("好友");
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(FriendFragment.newInstance(uid));
        fragments.add(FollowsFragment.newInstance(uid));

        List<String> titles = new ArrayList<>();
        titles.add("关注");
        titles.add("粉丝");
        FriendshipPageAdapter friendshipPageAdapter = new FriendshipPageAdapter(
                getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(friendshipPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public int getAttachLayoutId() {
        return 0;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_friendship;
    }

    private class FriendshipPageAdapter extends WeiboFragmentPagerAdapter {

        public FriendshipPageAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> titles) {
            super(fm, fragments, titles);
        }
    }
}

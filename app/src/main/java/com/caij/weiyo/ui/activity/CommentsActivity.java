package com.caij.weiyo.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.caij.weiyo.R;
import com.caij.weiyo.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.weiyo.ui.fragment.BaseFragment;
import com.caij.weiyo.ui.fragment.mention.AcceptCommentsFragment;
import com.caij.weiyo.ui.fragment.mention.CommentMentionFragment;
import com.caij.weiyo.ui.fragment.mention.PublishCommentsFragment;
import com.caij.weiyo.ui.fragment.mention.WeiboMentionFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/7/3.
 */
public class CommentsActivity extends BaseToolBarActivity {

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle("评论");
        List<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new AcceptCommentsFragment());
        fragments.add(new PublishCommentsFragment());

        List<String> titles = new ArrayList<>();
        titles.add("收到的评论");
        titles.add("发出的评论");
        FriendshipPageAdapter friendshipPageAdapter = new FriendshipPageAdapter(getSupportFragmentManager(), fragments, titles);
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

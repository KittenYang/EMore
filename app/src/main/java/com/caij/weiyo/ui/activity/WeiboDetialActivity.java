package com.caij.weiyo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.weiyo.ui.fragment.BaseFragment;
import com.caij.weiyo.ui.fragment.WeiboCommentListFragment;
import com.caij.weiyo.ui.fragment.WeiboLikerListFragment;
import com.caij.weiyo.ui.fragment.WeiboRepostListFragment;
import com.caij.weiyo.utils.SpannableStringUtil;
import com.caij.weiyo.view.weibo.WeiboDetailItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/12.
 */
public class WeiboDetialActivity extends BaseToolBarActivity {

    @BindView(R.id.weibo_item_view)
    WeiboDetailItemView weiboItemView;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    public static Intent newIntent(Context context, Weibo weibo) {
        Intent intent = new Intent(context, WeiboDetialActivity.class);
        intent.putExtra(Key.OBJ, weibo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Weibo weibo = (Weibo) getIntent().getSerializableExtra(Key.OBJ);
        ButterKnife.bind(this);
        //这里需要重新解析文本spand 因为通过序列化SpannableString不会序列化
        SpannableStringUtil.paraeSpannable(weibo, getApplicationContext());
        weiboItemView.setWeibo(weibo);
        List<BaseFragment> fragments = new ArrayList<>(3);
        Bundle bundle = new Bundle();
        bundle.putLong(Key.ID, weibo.getId());
        WeiboCommentListFragment weiboCommentListFragment = new WeiboCommentListFragment();
        WeiboRepostListFragment weiboRepostListFragment =  new WeiboRepostListFragment();
        WeiboLikerListFragment weiboLikerListFragment = new WeiboLikerListFragment();

        weiboCommentListFragment.setArguments(bundle);
        weiboRepostListFragment.setArguments(bundle);
        weiboLikerListFragment.setArguments(bundle);

        fragments.add(weiboRepostListFragment);
        fragments.add(weiboCommentListFragment);
        fragments.add(weiboLikerListFragment);
        List<String> titles = new ArrayList<>(3);
        titles.add("转发");
        titles.add("评论");
        titles.add("赞");
        WeiboFragmentPagerAdapter adapter = new WeiboFragmentPagerAdapter(getSupportFragmentManager(),
                fragments, titles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                getResources().getColor(R.color.text_80));
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_weibo_detail;
    }

}

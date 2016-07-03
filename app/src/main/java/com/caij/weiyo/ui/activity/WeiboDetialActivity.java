package com.caij.weiyo.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.ui.activity.publish.CommentWeiboActivity;
import com.caij.weiyo.ui.activity.publish.RepostWeiboActivity;
import com.caij.weiyo.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.weiyo.ui.fragment.BaseFragment;
import com.caij.weiyo.ui.fragment.WeiboCommentListFragment;
import com.caij.weiyo.ui.fragment.WeiboLikerListFragment;
import com.caij.weiyo.ui.fragment.WeiboRepostListFragment;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.view.weibo.WeiboDetailItemView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.action_star)
    FloatingActionButton actionStar;
    @BindView(R.id.action_repost)
    FloatingActionButton actionRepost;
    @BindView(R.id.action_comment)
    FloatingActionButton actionComment;
    @BindView(R.id.action_menu)
    FloatingActionsMenu actionMenu;
    @BindView(R.id.fl_lay)
    FrameLayout flLay;

    private Weibo mWeibo;

    public static Intent newIntent(Context context, Weibo weibo) {
        Intent intent = new Intent(context, WeiboDetialActivity.class);
        intent.putExtra(Key.OBJ, weibo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.weibo_detail_title));
        mWeibo = (Weibo) getIntent().getSerializableExtra(Key.OBJ);
        ButterKnife.bind(this);
        weiboItemView.setWeibo(mWeibo);

        actionMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.
                OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                flLay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                flLay.setVisibility(View.GONE);
            }
        });

        AccessToken weicoToken = UserPrefs.get().getWeiCoToken();
        if (weicoToken == null || weicoToken.isExpired()) {
            DialogUtil.showHintDialog(this, getString(R.string.hint), getString(R.string.aouth_high_hint), getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Account account = UserPrefs.get().getAccount();
                    Intent intent = LoginActivity.newWeiCoLoginIntent(WeiboDetialActivity.this,
                            account.getUsername(), account.getPwd());
                    startActivityForResult(intent, Key.AUTH);
                }
            }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            doNext();
        }
    }

    private void doNext() {
        List<BaseFragment> fragments = new ArrayList<>(3);
        fragments.add(WeiboRepostListFragment.newInstance(mWeibo.getId()));
        fragments.add(WeiboCommentListFragment.newInstance(mWeibo));
        fragments.add(WeiboLikerListFragment.newInstance(mWeibo.getId()));
        List<String> titles = new ArrayList<>(3);
        titles.add("转发 " + mWeibo.getReposts_count());
        titles.add("评论 " + mWeibo.getComments_count());
        titles.add("赞 " + mWeibo.getAttitudes_count());
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

    @OnClick({R.id.action_star, R.id.action_repost, R.id.action_comment, R.id.fl_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_star:
                break;
            case R.id.action_repost: {
                Intent intent = RepostWeiboActivity.newIntent(this, mWeibo);
                startActivity(intent);
                break;
            }
            case R.id.action_comment: {
                Intent intent = CommentWeiboActivity.newIntent(this, mWeibo.getId());
                startActivity(intent);
                break;
            }

            case R.id.fl_lay:
                actionMenu.collapse();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Key.AUTH) {
                doNext();
            }
        }
    }
}

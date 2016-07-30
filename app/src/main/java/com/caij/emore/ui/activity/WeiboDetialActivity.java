package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Account;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboDetailPresent;
import com.caij.emore.present.imp.WeiboDetailPresentImp;
import com.caij.emore.present.view.WeiboDetailView;
import com.caij.emore.source.local.LocalWeiboSource;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.activity.publish.CommentWeiboActivity;
import com.caij.emore.ui.activity.publish.RepostWeiboActivity;
import com.caij.emore.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.WeiboCommentListFragment;
import com.caij.emore.ui.fragment.WeiboLikerListFragment;
import com.caij.emore.ui.fragment.WeiboRepostListFragment;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.view.weibo.WeiboDetailItemView;
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
public class WeiboDetialActivity extends BaseToolBarActivity implements WeiboDetailView, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

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
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private long mWeiboId;
    private Weibo mWeibo;
    private WeiboDetailPresent mWeiboDetailPresent;
    private List<String> mTabTitles;

    public static Intent newIntent(Context context, long weiboId) {
        Intent intent = new Intent(context, WeiboDetialActivity.class);
        intent.putExtra(Key.ID, weiboId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.weibo_detail_title));
        mWeiboId = getIntent().getLongExtra(Key.ID, -1);
        ButterKnife.bind(this);

        initView();

        mWeiboDetailPresent = new WeiboDetailPresentImp(UserPrefs.get().getAccount(), mWeiboId,
                this, new ServerWeiboSource(), new LocalWeiboSource());
        mWeiboDetailPresent.onCreate();

        if (WeicoAuthUtil.checkWeicoLogin(this, true)) {
            doNext();
        }
    }

    private void initView(){
        mAttachContainer.setVisibility(View.GONE);

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

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    private void doNext() {
        List<BaseFragment> fragments = new ArrayList<>(3);
        fragments.add(WeiboCommentListFragment.newInstance(mWeiboId));
        fragments.add(WeiboRepostListFragment.newInstance(mWeiboId));
        fragments.add(WeiboLikerListFragment.newInstance(mWeiboId));
        mTabTitles = new ArrayList<>(3);
        WeiboFragmentPagerAdapter adapter = new WeiboFragmentPagerAdapter(getSupportFragmentManager(),
                fragments, null);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                getResources().getColor(R.color.text_80));

        mWeiboDetailPresent.loadWeiboDetail();
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_weibo_detail;
    }

    @OnClick({R.id.action_star, R.id.action_repost, R.id.action_comment, R.id.fl_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_star:
                mWeiboDetailPresent.attitudesWeibo(mWeibo);
                actionMenu.collapse();
                break;
            case R.id.action_repost: {
                if (mWeibo != null) {
                    Intent intent = RepostWeiboActivity.newIntent(this, mWeibo);
                    startActivity(intent);
                }
                actionMenu.collapse();
                break;
            }
            case R.id.action_comment: {
                Intent intent = CommentWeiboActivity.newIntent(this, mWeiboId);
                startActivity(intent);
                actionMenu.collapse();
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
        if (requestCode == Key.AUTH) {
            if (resultCode == RESULT_OK) {
                doNext();
            }else {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (actionMenu.isExpanded()) {
            actionMenu.collapse();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void setWeibo(Weibo weibo) {
        mAttachContainer.setVisibility(View.VISIBLE);

        weiboItemView.setWeibo(weibo);
        mWeibo = weibo;
        mTabTitles.clear();

        mTabTitles.add("评论 " + mWeibo.getComments_count());
        mTabTitles.add("转发 " + mWeibo.getReposts_count());
        mTabTitles.add("赞 " + mWeibo.getAttitudes_count());
        for (int i = 0; i < mTabTitles.size(); i ++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(mTabTitles.get(i));
            }
        }
    }

    @Override
    public void onRefreshComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDeleteWeiboSuccess(Weibo weibo, int position) {
        showToast(R.string.delete_success);
        finish();
    }

    @Override
    public void onCollectSuccess(Weibo weibo) {

    }

    @Override
    public void onUncollectSuccess(Weibo weibo) {

    }

    @Override
    public void onAttitudesSuccess(Weibo weibo) {
        mWeibo.setAttitudes(true);
        RxBus.get().post(Key.EVENT_WEIBO_UPDATE, mWeibo);
    }

    @Override
    public void onDestoryAttitudesSuccess(Weibo weibo) {
        mWeibo.setAttitudes(false);
        RxBus.get().post(Key.EVENT_WEIBO_UPDATE, mWeibo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWeiboDetailPresent != null) {
            mWeiboDetailPresent.onDestroy();
        }
    }

    @Override
    public void onRefresh() {
        mWeiboDetailPresent.refreshWeiboDetail();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipeRefreshLayout.setEnabled(verticalOffset == 0);
    }
}

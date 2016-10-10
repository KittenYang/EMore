package com.caij.emore.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.dao.imp.StatusManagerImp;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.WeiboDetailPresent;
import com.caij.emore.present.imp.WeiboDetailPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.adapter.WeiboAdapter;
import com.caij.emore.ui.view.WeiboDetailView;
import com.caij.emore.ui.activity.publish.CommentWeiboActivity;
import com.caij.emore.ui.activity.publish.RepostWeiboActivity;
import com.caij.emore.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.WeiboCommentListFragment;
import com.caij.emore.ui.fragment.WeiboLikerListFragment;
import com.caij.emore.ui.fragment.WeiboRepostListFragment;
import com.caij.emore.utils.CountUtil;
import com.caij.emore.utils.DrawableUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.widget.recyclerview.OnScrollListener;
import com.caij.emore.widget.weibo.detail.RepostWeiboImageItemView;
import com.caij.emore.widget.weibo.detail.WeiboImageItemView;
import com.caij.emore.widget.weibo.list.RepostWeiboListArticleItemView;
import com.caij.emore.widget.weibo.list.RepostWeiboListVideoItemView;
import com.caij.emore.widget.weibo.list.WeiboListArticleItemView;
import com.caij.emore.widget.weibo.list.WeiboListItemView;
import com.caij.emore.widget.weibo.list.WeiboListVideoItemView;
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
public class WeiboDetialActivity extends BaseToolBarActivity<WeiboDetailPresent> implements WeiboDetailView,
        SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, OnScrollListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.fl_weibo_item_view)
    FrameLayout flWeiboItemView;
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

    private WeiboListItemView mWeiboItemView;

    private long mWeiboId;
    private Weibo mWeibo;
    private List<String> mTabTitles;
    private boolean isActionMenuVisible = true;

    public static Intent newIntent(Context context, long weiboId) {
        Intent intent = new Intent(context, WeiboDetialActivity.class);
        intent.putExtra(Key.ID, weiboId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.weibo_detail_title));
        ButterKnife.bind(this);

        initView();

        if (WeicoAuthUtil.checkWeicoLogin(this, true)) {
            doNext();
        }
    }

    @Override
    protected WeiboDetailPresent createPresent() {
        mWeiboId = getIntent().getLongExtra(Key.ID, -1);
        return new WeiboDetailPresentImp(mWeiboId,
                this, new StatusApiImp(), new StatusManagerImp(), new AttitudeApiImp());
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

        viewPager.addOnPageChangeListener(this);

        actionStar.setIconDrawable(DrawableUtil.createSelectThemeDrawable(this, R.mipmap.ic_star, R.color.white, R.color.red_message_bg));
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

        mPresent.loadWeiboDetail();
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_weibo_detail;
    }

    @OnClick({R.id.action_star, R.id.action_repost, R.id.action_comment, R.id.fl_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_star:
                if (mWeibo.getAttitudes_status() == 1) {
                    mPresent.destroyAttitudesWeibo(mWeibo);
                }else {
                    mPresent.attitudesWeibo(mWeibo);
                }
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

        if (mWeiboItemView == null) {
            int type = WeiboAdapter.getWeiboType(weibo);
            switch (type) {
                case WeiboAdapter.TYPE_NORMAL_IMAGE:
                    mWeiboItemView = new WeiboImageItemView(this);
                    break;

                case WeiboAdapter.TYPE_REPOST_IMAGE:
                    mWeiboItemView = new RepostWeiboImageItemView(this);
                    break;

                case WeiboAdapter.TYPE_NORMAL_VIDEO:
                    mWeiboItemView = new WeiboListVideoItemView(this);
                    break;

                case WeiboAdapter.TYPE_REPOST_VIDEO:
                    mWeiboItemView = new RepostWeiboListVideoItemView(this);
                    break;

                case WeiboAdapter.TYPE_NORMAL_ARTICLE:
                    mWeiboItemView = new WeiboListArticleItemView(this);
                    break;

                case WeiboAdapter.TYPE_REPOST_ARTICLE:
                    mWeiboItemView = new RepostWeiboListArticleItemView(this);
                    break;
            }
            mWeiboItemView.setDetail(true);
            flWeiboItemView.addView(mWeiboItemView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        mWeibo = weibo;

        mWeiboItemView.setWeibo(weibo);

        setTabText(weibo);
        setAttitudeStatus(weibo);
    }

    private void setTabText(Weibo weibo) {
        mTabTitles.clear();
        mTabTitles.add(getString(R.string.comment) + " " + weibo.getComments_count());
        mTabTitles.add(getString(R.string.repost) + " " + weibo.getReposts_count());
        mTabTitles.add(getString(R.string.attitude) + " " + weibo.getAttitudes_count());
        for (int i = 0; i < mTabTitles.size(); i ++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(mTabTitles.get(i));
            }
        }
    }

    private void setAttitudeStatus(Weibo weibo) {
        actionStar.setSelected(weibo.getAttitudes_status() == 1);
    }

    @Override
    public void onRefreshComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onWeiboUpdate(Weibo weibo) {
        mWeibo = weibo;

        setTabText(weibo);
        setAttitudeStatus(weibo);
    }

    @Override
    public void onStatusAttitudeCountUpdate(int count) {
        mWeibo.setAttitudes_count(count);
        tabLayout.getTabAt(2).setText(getString(R.string.attitude) + " " + CountUtil.getCounter(this, count));
    }

    @Override
    public void onStatusAttitudeUpdate(boolean isAttitude) {
        mWeibo.setAttitudes_status(isAttitude ? 1 : 0);
        actionStar.setSelected(isAttitude);
    }

    @Override
    public void onDeleteWeiboSuccess(Weibo weibo, int position) {
        showHint(R.string.delete_success);
        finish();
    }

    @Override
    public void onCollectSuccess(Weibo weibo) {

    }

    @Override
    public void onUncollectSuccess(Weibo weibo) {

    }

    @Override
    public void onRefresh() {
        mPresent.refreshWeiboDetail();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipeRefreshLayout.setEnabled(verticalOffset == 0);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        boolean isSignificantDelta = Math.abs(dy) > getResources().getDimensionPixelOffset(R.dimen.fab_scrollthreshold);
        if (isSignificantDelta) {
            if (dy > 0) {
                onScrollUp();
            } else {
                onScrollDown();
            }
        }
    }

    private void onScrollDown() {
       showActionMenu();
    }

    private void onScrollUp() {
        hideActionMenu();
    }

    private void showActionMenu() {
        if (isActionMenuVisible) {
            return;
        }
        isActionMenuVisible = true;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) actionMenu.getLayoutParams();
        int translationY = layoutParams.bottomMargin + actionMenu.getHeight();
        LogUtil.d("this", "translationY %s", translationY);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(actionMenu, "translationY", translationY, 0);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

    private void hideActionMenu() {
        if (!isActionMenuVisible) {
            return;
        }
        isActionMenuVisible = false;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) actionMenu.getLayoutParams();
        int translationY = layoutParams.bottomMargin + actionMenu.getHeight();
        LogUtil.d("this", "translationY %s", translationY);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(actionMenu, "translationY", 0, translationY);
        objectAnimator.setDuration(300);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.start();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        showActionMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

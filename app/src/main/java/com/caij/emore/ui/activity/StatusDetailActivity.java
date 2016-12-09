package com.caij.emore.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import com.caij.emore.manager.imp.StatusManagerImp;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.StatusDetailPresent;
import com.caij.emore.present.imp.StatusDetailPresentImp;
import com.caij.emore.remote.imp.AttitudeApiImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.activity.publish.RelayStatusActivity;
import com.caij.emore.ui.adapter.StatusAdapter;
import com.caij.emore.ui.view.StatusDetailView;
import com.caij.emore.ui.activity.publish.CommentStatusActivity;
import com.caij.emore.ui.adapter.StatusFragmentPagerAdapter;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.StatusCommentListFragment;
import com.caij.emore.ui.fragment.StatusLikerListFragment;
import com.caij.emore.ui.fragment.StatusRelayListFragment;
import com.caij.emore.utils.CountUtil;
import com.caij.emore.utils.DrawableUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;
import com.caij.emore.widget.recyclerview.OnScrollListener;
import com.caij.emore.widget.weibo.detail.RepostStatusImageItemView;
import com.caij.emore.widget.weibo.detail.StatusImageItemView;
import com.caij.emore.widget.weibo.list.RepostStatusListArticleItemView;
import com.caij.emore.widget.weibo.list.RepostStatusListVideoItemView;
import com.caij.emore.widget.weibo.list.StatusListArticleItemView;
import com.caij.emore.widget.weibo.list.StatusListItemView;
import com.caij.emore.widget.weibo.list.StatusListVideoItemView;
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
public class StatusDetailActivity extends BaseToolBarActivity<StatusDetailPresent> implements StatusDetailView,
        SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener, OnScrollListener, ViewPager.OnPageChangeListener {

    public static final int ENTER_TYPE_COM = 1;
    public static final int ENTER_TYPE_COMMENT = 2;

    @BindView(R.id.cl)
    CoordinatorLayout coordinatorLayout;
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

    private StatusListItemView mWeiboItemView;

    private long mStatusId;
    private Status mStatus;

    private boolean isActionMenuVisible = true;

    public static Intent newIntent(Context context, long statusId) {
        return newIntent(context, statusId, ENTER_TYPE_COM);
    }

    public static Intent newIntent(Context context, long statusId, int type) {
        Intent intent = new Intent(context, StatusDetailActivity.class);
        intent.putExtra(Key.ID, statusId);
        intent.putExtra(Key.TYPE, type);
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
    protected StatusDetailPresent createPresent() {
        mStatusId = getIntent().getLongExtra(Key.ID, -1);
        return new StatusDetailPresentImp(mStatusId,
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
        fragments.add(StatusCommentListFragment.newInstance(mStatusId));
        fragments.add(StatusRelayListFragment.newInstance(mStatusId));
        fragments.add(StatusLikerListFragment.newInstance(mStatusId));
        StatusFragmentPagerAdapter adapter = new StatusFragmentPagerAdapter(getSupportFragmentManager(),
                fragments, null);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                getResources().getColor(R.color.text_80));

        mPresent.loadStatusDetail();
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_weibo_detail;
    }

    @OnClick({R.id.action_star, R.id.action_repost, R.id.action_comment, R.id.fl_lay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_star:
                if (mStatus.getAttitudes_status() == 1) {
                    mPresent.destroyAttitudeStatus(mStatus);
                }else {
                    mPresent.attitudeStatus(mStatus);
                }
                actionMenu.collapse();
                break;
            case R.id.action_repost: {
                if (mStatus != null) {
                    Intent intent = RelayStatusActivity.newIntent(this, mStatus);
                    startActivity(intent);
                }
                actionMenu.collapse();
                break;
            }
            case R.id.action_comment: {
                Intent intent = CommentStatusActivity.newIntent(this, mStatusId);
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
    public void setStatus(Status status) {
        mAttachContainer.setVisibility(View.VISIBLE);

        if (mWeiboItemView == null) {
            int type = StatusAdapter.getStatusType(status);
            switch (type) {
                case StatusAdapter.TYPE_NORMAL_IMAGE:
                    mWeiboItemView = new StatusImageItemView(this);
                    break;

                case StatusAdapter.TYPE_REPOST_IMAGE:
                    mWeiboItemView = new RepostStatusImageItemView(this);
                    break;

                case StatusAdapter.TYPE_NORMAL_VIDEO:
                    mWeiboItemView = new StatusListVideoItemView(this);
                    break;

                case StatusAdapter.TYPE_REPOST_VIDEO:
                    mWeiboItemView = new RepostStatusListVideoItemView(this);
                    break;

                case StatusAdapter.TYPE_NORMAL_ARTICLE:
                    mWeiboItemView = new StatusListArticleItemView(this);
                    break;

                case StatusAdapter.TYPE_REPOST_ARTICLE:
                    mWeiboItemView = new RepostStatusListArticleItemView(this);
                    break;
            }
            mWeiboItemView.setDetail(true);
            flWeiboItemView.addView(mWeiboItemView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        mStatus = status;

        mWeiboItemView.setStatus(status);

        setTabText(status);
        setAttitudeStatus(status);
    }

    private void setTabText(Status status) {
        for (int i = 0; i < tabLayout.getTabCount(); i ++) {
            setTabText(i, status);
        }
    }

    private void setTabText(int index, Status status) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        String text;
        if (index == 0) {
            text = getCount(R.string.comment, status.getComments_count());
        }else if (index == 1) {
            text = getCount(R.string.repost, status.getReposts_count());
        }else {
            text = getCount(R.string.attitude, status.getAttitudes_count());
        }
        assert tab != null;
        tab.setText(text);
    }

    private String getCount(int strResId, int count) {
        return getString(strResId) + " " + CountUtil.getCounter(this, count);
    }

    private void setAttitudeStatus(Status status) {
        actionStar.setSelected(status.getAttitudes_status() == 1);
    }

    @Override
    public void onRefreshComplete() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStatusAttitudeCountUpdate(int count) {
        mStatus.setAttitudes_count(count);
        setTabText(2, mStatus);
    }

    @Override
    public void onStatusAttitudeUpdate(boolean isAttitude) {
        mStatus.setAttitudes_status(isAttitude ? 1 : 0);
        actionStar.setSelected(isAttitude);
    }

    @Override
    public void onStatusCommentCountUpdate(int count) {
        mStatus.setComments_count(count);
        setTabText(0, mStatus);
    }

    @Override
    public void onStatusRelayCountUpdate(int count) {
        mStatus.setReposts_count(count);
        setTabText(1, mStatus);
    }

    @Override
    public void onDeleteStatusSuccess(Status status, int position) {
        showHint(R.string.delete_success);
        finish();
    }

    @Override
    public void onCollectSuccess(Status status) {

    }

    @Override
    public void onUnCollectSuccess(Status status) {

    }

    @Override
    public void onRefresh() {
        mPresent.refreshStatusDetail();
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
        if (isActionMenuVisible) return;

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
        if (!isActionMenuVisible) return;

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

package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.UserInfoDetailPresent;
import com.caij.emore.present.imp.UserInfoDetailPresentImp;
import com.caij.emore.ui.view.DetailUserView;
import com.caij.emore.source.local.LocalUserSource;
import com.caij.emore.source.server.ServerUserSource;
import com.caij.emore.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.emore.ui.fragment.BaseFragment;
import com.caij.emore.ui.fragment.UserImageFragment;
import com.caij.emore.ui.fragment.UserInfoFragment;
import com.caij.emore.ui.fragment.weibo.UserWeiboFragment;
import com.caij.emore.utils.CountUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.weibo.WeiboUtil;
import com.caij.emore.utils.weibo.WeicoAuthUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/8.
 */
public class UserInfoActivity extends BaseActivity<UserInfoDetailPresent> implements
        DetailUserView, AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.imgCover)
    ImageView imgCover;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtName)
    TextView txtName;
    @BindView(R.id.imgVerified)
    ImageView imgVerified;
    @BindView(R.id.imgGender)
    ImageView imgGender;
    @BindView(R.id.txtFriendsCounter)
    TextView txtFriendsCounter;
    @BindView(R.id.txtFollowersCounter)
    TextView txtFollowersCounter;
    @BindView(R.id.txtDesc)
    TextView txtDesc;
    @BindView(R.id.collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.divider_01)
    TextView divider01;
    @BindView(R.id.layDetail)
    View layDetail;
    @BindView(R.id.layName)
    RelativeLayout layName;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.cl_root)
    View viewRoot;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tv_action_bar_name)
    TextView tvTitleUserName;

    private User mUser;
    private MenuItem menuItem;
    private ArrayList<String> mTabTitles;
    private String mUserName;

    public static Intent newIntent(Context context, String name) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(Key.USERNAME, name);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pager);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mUserName = intent.getStringExtra(Key.USERNAME);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        tvTitleUserName.setText(mUserName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layDetail.getLayoutParams();
            params.bottomMargin = SystemUtil.getStatusBarHeight(this) + params.bottomMargin;
            layDetail.setLayoutParams(params);
        }

        viewRoot.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.gplus_color_1),
                getResources().getColor(R.color.gplus_color_2),
                getResources().getColor(R.color.gplus_color_3),
                getResources().getColor(R.color.gplus_color_4));

        mTabTitles = new ArrayList<>(3);
        mTabTitles.add(getString(R.string.user_info_page));
        mTabTitles.add(getString(R.string.weibo));
        mTabTitles.add(getString(R.string.photo));

        if (WeicoAuthUtil.checkWeicoLogin(this, true)) {
            doNext();
        }
    }

    @Override
    protected UserInfoDetailPresent createPresent() {
        Token token = UserPrefs.get(this).getWeiCoToken();
        String username = getIntent().getStringExtra(Key.USERNAME);
        return new UserInfoDetailPresentImp(token.getAccess_token(), username, this,
                new ServerUserSource(), new LocalUserSource());
    }

    private void doNext() {
        mPresent.getWeiboUserInfoByName();
    }

    private void initContent(User user) {
        mTabTitles.clear();
        mTabTitles.add(getString(R.string.user_info_page));
        mTabTitles.add(getString(R.string.weibo) + " (" + CountUtil.getCounter(this, user.getStatuses_count()) + ")");
        mTabTitles.add(getString(R.string.photo));
        if (viewPager.getAdapter() == null) {
            List<BaseFragment> fragments = new ArrayList<>(3);
            fragments.add(UserInfoFragment.newInstance(user));
            fragments.add(UserWeiboFragment.newInstance(user.getScreen_name()));
            fragments.add(UserImageFragment.newInstance(user.getScreen_name()));
            WeiboFragmentPagerAdapter adapter = new WeiboFragmentPagerAdapter(getSupportFragmentManager(),
                    fragments, mTabTitles);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(1);
            viewPager.setOffscreenPageLimit(fragments.size());
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                    getResources().getColor(R.color.text_80));
        }else {
            for (int i = 0; i < mTabTitles.size(); i ++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setText(mTabTitles.get(i));
                }
            }
        }
    }

    private void fillDate(User user) {
        viewRoot.setVisibility(View.VISIBLE);
        ImageLoader.load(this, imgCover, user.getCover_image_phone(), R.mipmap.default_user_info_bg);
        txtName.setText(user.getName());
        txtDesc.setText(user.getDescription());
        txtFriendsCounter.setText(CountUtil.getCounter(this, user.getFriends_count()) + getString(R.string.follow));
        txtFollowersCounter.setText(CountUtil.getCounter(this, user.getFollowers_count()) + getString(R.string.fans));
        ImageLoader.ImageConfigBuild build = new ImageLoader.ImageConfigBuild().setCircle(true).
                setScaleType(ImageLoader.ScaleType.CENTER_CROP);
        ImageLoader.loadUrl(this, ivAvatar, user.getAvatar_large(), R.drawable.circle_image_placeholder, build.build());

        WeiboUtil.setImageVerified(imgVerified, user);
        WeiboUtil.setGender(user, imgGender);

        initContent(user);

        if (menuItem != null) {
            updateMenu(user, menuItem);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appbar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appbar.removeOnOffsetChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_pager, menu);
        menuItem = menu.findItem(R.id.follow);
        if (mUser != null) {
            updateMenu(mUser, menuItem);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.follow:
                if (mUser != null) {
                    if (mUser.getFollowing()) {
                        mPresent.unFollow();
                    } else {
                        mPresent.follow();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUser(User user) {
        this.mUser = user;
        fillDate(user);
    }

    private void updateMenu(User user, MenuItem menuItem) {
        if (user != null && menuItem != null) {
            menuItem.setTitle(user.getFollowing() ? getString(R.string.unfollow) : getString(R.string.follow));
        }
    }


    @OnClick({R.id.txtFriendsCounter, R.id.txtFollowersCounter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtFriendsCounter:
            case R.id.txtFollowersCounter:
                Intent intent = FriendshipActivity.newIntent(this, mUser.getId());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onFollowSuccess() {
        mUser.setFollowing(true);
        updateMenu(mUser, menuItem);
    }

    @Override
    public void onUnfollowSuccess() {
        mUser.setFollowing(false);
        updateMenu(mUser, menuItem);
    }

    @Override
    public void onRefreshComplete() {
        swipeRefreshLayout.setRefreshing(false);
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipeRefreshLayout.setEnabled(verticalOffset == 0);
        int maxScroll = appBarLayout.getTotalScrollRange();
        if (Math.abs(verticalOffset) == maxScroll) {
            tvTitleUserName.setVisibility(View.VISIBLE);
        }else {
            tvTitleUserName.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        mPresent.onRefresh();
    }
}

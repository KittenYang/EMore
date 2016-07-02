package com.caij.weiyo.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.UserInfoDetailPresent;
import com.caij.weiyo.present.imp.UserInfoDetailPresentImp;
import com.caij.weiyo.present.view.DetailUserView;
import com.caij.weiyo.source.local.LocalUserSource;
import com.caij.weiyo.source.server.ServerFollowSource;
import com.caij.weiyo.source.server.ServerUserSource;
import com.caij.weiyo.ui.adapter.WeiboFragmentPagerAdapter;
import com.caij.weiyo.ui.fragment.BaseFragment;
import com.caij.weiyo.ui.fragment.UserImageFragment;
import com.caij.weiyo.ui.fragment.UserInfoFragment;
import com.caij.weiyo.ui.fragment.weibo.UserWeiboFragment;
import com.caij.weiyo.utils.CountUtil;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.utils.SpannableStringUtil;
import com.caij.weiyo.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/8.
 */
public class UserInfoActivity extends BaseActivity implements DetailUserView {

    UserInfoDetailPresent mUserInfoDetailPresent;

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

    private User mUser;
    private MenuItem menuItem;
    private Dialog mFollowDialog;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(Key.OBJ, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pager);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layDetail.getLayoutParams();
            params.bottomMargin = SystemUtil.getStatusBarHeight(this) + params.bottomMargin;
            layDetail.setLayoutParams(params);
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle("");


        AccessToken weicoToken = UserPrefs.get().getWeiCoToken();
        if (weicoToken == null || weicoToken.isExpired()) {
            DialogUtil.showHintDialog(this, getString(R.string.hint), getString(R.string.aouth_high_hint), getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Account account = UserPrefs.get().getAccount();
                    Intent intent = LoginActivity.newWeiCoLoginIntent(UserInfoActivity.this,
                            account.getUsername(), account.getPwd());
                    startActivityForResult(intent, Key.AUTH);
                }
            }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }else {
            doNext();
        }
    }

    private void doNext() {
        Intent intent = getIntent();
        AccessToken token = UserPrefs.get().getWeiCoToken();
        if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction()) && getIntent().getData() != null) {
            String userName = getIntent().getData().toString();
            if (userName.startsWith(SpannableStringUtil.USER_INFO_SCHEME)) {
                userName = userName.replace(SpannableStringUtil.USER_INFO_SCHEME, "");
            }
            if (userName.startsWith("@")) {
                userName = userName.replace("@", "");
            }
            mUserInfoDetailPresent = new UserInfoDetailPresentImp(token.getAccess_token(), userName, this,
                    new ServerUserSource(), new LocalUserSource(), new ServerFollowSource());
            mUserInfoDetailPresent.getWeiboUserInfoByName();
        } else {
            User user = (User) intent.getSerializableExtra(Key.OBJ);
            mUserInfoDetailPresent = new UserInfoDetailPresentImp(token.getAccess_token(), user.getScreen_name(), this,
                    new ServerUserSource(), new LocalUserSource(), new ServerFollowSource());
            mUser = user;
            fillDate(user);
        }
    }

    private void initContent(User user) {
        if (viewPager.getAdapter() == null) {
            List<String> titles = new ArrayList<>(3);
            titles.add(getString(R.string.user_info_page));
            titles.add(getString(R.string.weibo) + " (" + CountUtil.getCounter(this, user.getStatuses_count()) + ")");
            titles.add(getString(R.string.photo));
            List<BaseFragment> fragments = new ArrayList<>(3);
            fragments.add(UserInfoFragment.newInstance(user));
            fragments.add(UserWeiboFragment.newInstance(user.getScreen_name()));
            fragments.add(UserImageFragment.newInstance(user.getScreen_name()));
            WeiboFragmentPagerAdapter adapter = new WeiboFragmentPagerAdapter(getSupportFragmentManager(),
                    fragments, titles);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(fragments.size());
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                    getResources().getColor(R.color.text_80));

            viewPager.setCurrentItem(1);
        }
    }

    private void fillDate(User user) {
        ImageLoader.load(this, imgCover, user.getCover_image_phone(), R.mipmap.default_user_info_bg);
        txtName.setText(user.getName());
        txtDesc.setText(user.getDescription());
        txtFriendsCounter.setText(CountUtil.getCounter(this, user.getFriends_count()) + getString(R.string.follow));
        txtFollowersCounter.setText(CountUtil.getCounter(this, user.getFollowers_count()) + getString(R.string.fans));
        ImageLoader.ImageConfigBuild build = new ImageLoader.ImageConfigBuild().setCircle(true).
                setScaleType(ImageLoader.ScaleType.CENTER_CROP);
        ImageLoader.load(this, ivAvatar, user.getAvatar_large(), R.mipmap.ic_default_circle_head_image, build.build());

        initContent(user);

        if (menuItem != null) {
            updateMenu(user, menuItem);
        }
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
                    if (mUser.isFollowing()) {
                        mUserInfoDetailPresent.unFollow();
                    }else {
                        mUserInfoDetailPresent.follow();
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
            menuItem.setTitle(user.isFollowing() ? getString(R.string.unfollow) : getString(R.string.follow));
        }
    }

    @Override
    public void showGetUserLoading(boolean isShow) {

    }

    @Override
    public void showFollowLoading(boolean b) {
        if (b) {
            if (mFollowDialog == null) {
                mFollowDialog = DialogUtil.showProgressDialog(this, null, getString(R.string.requesting));
            }else {
                mFollowDialog.show();
            }
        }else {
            mFollowDialog.dismiss();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Key.AUTH) {
                doNext();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserInfoDetailPresent.onDestroy();
    }

}

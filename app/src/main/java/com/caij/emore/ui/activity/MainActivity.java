package com.caij.emore.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.MainPresent;
import com.caij.emore.present.imp.MainPresentImp;
import com.caij.emore.ui.view.MainView;
import com.caij.emore.source.local.LocalDraftSource;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.local.LocalUserSource;
import com.caij.emore.source.server.ServerUserSource;
import com.caij.emore.ui.fragment.DraftFragment;
import com.caij.emore.ui.fragment.MessageUserFragment;
import com.caij.emore.ui.fragment.weibo.FriendWeiboFragment;
import com.caij.emore.ui.fragment.weibo.HotWeiboFragment;
import com.caij.emore.utils.DrawableUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.widget.main.ActionBarDrawerToggle;
import com.caij.emore.widget.DoubleClickToolBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainPresent> implements MainView, View.OnClickListener {

    private static final String FRIEND_WEIBO_FRAGMENT_TAG = "friend_weibo_fragment_tag";
    private static final String MESSAGE_FRAGMENT_TAG = "message_fragment_tag";

    @BindView(R.id.img_navigation_avatar)
    ImageView mImgNavigationAvatar;
    @BindView(R.id.tv_navigation_username)
    TextView mTvNavigationUsername;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    DoubleClickToolBar mToolbar;
    @BindView(R.id.rl_nav_head)
    RelativeLayout mRlNavHead;
    @BindView(R.id.material_drawer_account_header_background)
    ImageView ivHeaderBackground;
    @BindView(R.id.tv_weibo)
    TextView tvWeibo;
    @BindView(R.id.tv_unread_weibo_count)
    TextView tvUnreadWeiboCount;
    @BindView(R.id.rl_item_weibo)
    RelativeLayout rlItemWeibo;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_unread_message_count)
    TextView tvUnreadMessageCount;
    @BindView(R.id.rl_item_message)
    RelativeLayout rlItemMessage;
    @BindView(R.id.tv_draft_count)
    TextView tvDraftCount;

    ActionBarDrawerToggle mActionBarDrawerToggle;

    private Fragment mFriendWeiboFragment;
    private Fragment mMessageFragment;

    private Fragment mVisibleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mToolbar.setOnDoubleClickListener(this);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        Drawable iconWeiboDrawable = createNavMenuItemDrawable(R.mipmap.ic_weibo);
        tvWeibo.setCompoundDrawables(iconWeiboDrawable, null, null, null);

        Drawable iconMessageDrawable = createNavMenuItemDrawable(R.mipmap.ic_message);
        tvMessage.setCompoundDrawables(iconMessageDrawable, null, null, null);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            mFriendWeiboFragment = new FriendWeiboFragment();
            mMessageFragment = new MessageUserFragment();
            transaction.add(R.id.attach_container,
                    mFriendWeiboFragment, FRIEND_WEIBO_FRAGMENT_TAG).commit();

            mVisibleFragment = mFriendWeiboFragment;
        } else {
            mFriendWeiboFragment = getSupportFragmentManager().findFragmentByTag(FRIEND_WEIBO_FRAGMENT_TAG);
            if (mFriendWeiboFragment == null) {
                mFriendWeiboFragment = new FriendWeiboFragment();
            }
            mMessageFragment = getSupportFragmentManager().findFragmentByTag(MESSAGE_FRAGMENT_TAG);
            if (mMessageFragment == null) {
                mMessageFragment = new MessageUserFragment();
            }

            String key  = savedInstanceState.getString(Key.ID);
            if (FRIEND_WEIBO_FRAGMENT_TAG.equals(key)) {
                mVisibleFragment = mFriendWeiboFragment;
            }else if (MESSAGE_FRAGMENT_TAG.equals(key)) {
                mVisibleFragment = mMessageFragment;
            }
        }

        if (mVisibleFragment == mFriendWeiboFragment) {
            rlItemWeibo.setSelected(true);
            rlItemMessage.setSelected(false);
            tvWeibo.setSelected(true);
            tvMessage.setSelected(false);
        }else if (mVisibleFragment == mMessageFragment) {
            rlItemMessage.setSelected(true);
            rlItemWeibo.setSelected(false);
            tvMessage.setSelected(true);
            tvWeibo.setSelected(false);
        }

        mPresent.getWeiboUserInfoByUid();
    }

    @Override
    protected MainPresent createPresent() {
        Token token = UserPrefs.get(this).getEMoreToken();
        MainPresent simpleUserPresent = new MainPresentImp(token.getAccess_token(), Long.parseLong(token.getUid()),
                this, new ServerUserSource(), new LocalUserSource(), new LocalMessageSource(), new LocalDraftSource());
        return simpleUserPresent;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mVisibleFragment == mFriendWeiboFragment) {
            outState.putString(Key.ID, FRIEND_WEIBO_FRAGMENT_TAG);
        }else if (mVisibleFragment == mMessageFragment) {
            outState.putString(Key.ID, MESSAGE_FRAGMENT_TAG);
        }
        super.onSaveInstanceState(outState);
    }


    private Drawable createNavMenuItemDrawable(int drawableId) {
        return DrawableUtil.createSelectThemeDrawable(this, drawableId, R.color.icon_normal_color, R.color.colorPrimary);
    }

    public void switchContent(Fragment from, Fragment to, int id, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from).add(id, to, tag).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }

    private void changeToolBarFlag(boolean isScroll) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        if (isScroll) {
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            params.setScrollFlags(0);
        }
        mToolbar.setLayoutParams(params);
    }

    @Override
    public void setUser(User user) {
        if (user != null) {
            mTvNavigationUsername.setText(user.getName());
            ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild().setCircle(true).build();
            ImageLoader.loadUrl(this, mImgNavigationAvatar, user.getAvatar_large(), R.drawable.circle_image_placeholder, config);
            ImageLoader.load(this, ivHeaderBackground, user.getCover_image_phone(), R.mipmap.bg_nav_head);
            mImgNavigationAvatar.setTag(user);
        }
    }

    @Override
    public void setUnReadMessage(UnReadMessage unReadMessage) {
        int status = unReadMessage.getStatus();

        if (status > 0) {
            tvUnreadWeiboCount.setVisibility(View.VISIBLE);
            tvUnreadWeiboCount.setText(String.valueOf(status));
        }else {
            tvUnreadWeiboCount.setVisibility(View.GONE);
        }

        int unReadMessageCount = unReadMessage.getCmt() + unReadMessage.getDm_single()
                + unReadMessage.getMention_status() + unReadMessage.getMention_cmt();

        if (unReadMessageCount >  0) {
            tvUnreadMessageCount.setVisibility(View.VISIBLE);
            tvUnreadMessageCount.setText(String.valueOf(unReadMessageCount));
        }else {
            tvUnreadMessageCount.setVisibility(View.GONE);
        }

        if (unReadMessage.getStatus() + unReadMessageCount > 0) {
            mActionBarDrawerToggle.setReadVisible(true);
        }else {
            mActionBarDrawerToggle.setReadVisible(false);
        }
    }

    @Override
    public void setDraftCount(Integer integer) {
        if(integer == null || integer <= 0) {
            tvDraftCount.setVisibility(View.GONE);
        }else {
            tvDraftCount.setVisibility(View.VISIBLE);
            tvDraftCount.setText(String.valueOf(integer));
        }
    }

    @OnClick({R.id.img_navigation_avatar, R.id.tv_setting, R.id.rl_draft,R.id.rl_item_weibo, R.id.rl_item_message, R.id.tv_hot_weibo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setting: {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;
            }
            case R.id.img_navigation_avatar: {
                User user = (User) view.getTag();
                if (user != null) {
                    Intent intent = UserInfoActivity.newIntent(this, user.getScreen_name());
                    startActivity(intent);
                }
                break;
            }
            case R.id.toolbar:
                RxBus.getDefault().post(Event.EVENT_TOOL_BAR_DOUBLE_CLICK, mVisibleFragment);
                break;

            case R.id.rl_draft: {
                Intent intent = DefaultFragmentActivity.starFragmentV4(this, getString(R.string.draft_box), DraftFragment.class, null);
                startActivity(intent);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;
            }

            case R.id.rl_item_weibo:
                if (!rlItemWeibo.isSelected()) {
                    switchContent(mVisibleFragment, mFriendWeiboFragment, R.id.attach_container, FRIEND_WEIBO_FRAGMENT_TAG);
                    mVisibleFragment = mFriendWeiboFragment;
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    changeToolBarFlag(true);

                    rlItemWeibo.setSelected(true);
                    rlItemMessage.setSelected(false);
                    tvWeibo.setSelected(true);
                    tvMessage.setSelected(false);
                }
                break;

            case R.id.rl_item_message:
                if (!rlItemMessage.isSelected()) {
                    switchContent(mVisibleFragment, mMessageFragment, R.id.attach_container, MESSAGE_FRAGMENT_TAG);
                    mVisibleFragment = mMessageFragment;
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    changeToolBarFlag(false);

                    rlItemMessage.setSelected(true);
                    rlItemWeibo.setSelected(false);
                    tvMessage.setSelected(true);
                    tvWeibo.setSelected(false);
                }
                break;

            case R.id.tv_hot_weibo: {
                Intent intent = DefaultFragmentActivity.starFragmentV4(this, getString(R.string.hot_weibo),
                        HotWeiboFragment.class, null);
                startActivity(intent);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;
            }
        }
    }


}

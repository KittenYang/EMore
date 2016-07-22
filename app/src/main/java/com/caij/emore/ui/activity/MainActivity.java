package com.caij.emore.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.User;
import com.caij.emore.present.SimpleUserPresent;
import com.caij.emore.present.imp.UserPresentImp;
import com.caij.emore.present.view.SimpleUserView;
import com.caij.emore.source.local.LocalUserSource;
import com.caij.emore.source.server.ServerUserSource;
import com.caij.emore.ui.fragment.DraftFragment;
import com.caij.emore.ui.fragment.MessageUserFragment;
import com.caij.emore.ui.fragment.weibo.FriendWeiboFragment;
import com.caij.emore.utils.DrawableUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.view.DoubleClickToolBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements SimpleUserView, View.OnClickListener {

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
    @BindView(R.id.rb_weibo)
    RadioButton mRbWeibo;
    @BindView(R.id.rb_message)
    RadioButton mRbMessage;

    private Fragment mFriendWeiboFragment;
    private Fragment mMessageFragment;

    private Fragment mVisibleFragment;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mToolbar.setOnDoubleClickListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);

        AccessToken token = UserPrefs.get().getEMoreToken();
        SimpleUserPresent simpleUserPresent = new UserPresentImp(token.getAccess_token(), Long.parseLong(token.getUid()),
                this, new ServerUserSource(), new LocalUserSource());
        simpleUserPresent.getWeiboUserInfoByUid();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // fitsSystemWindows
            int statusBarHeight = SystemUtil.getStatusBarHeight(this);
            ViewGroup.LayoutParams headParams = mRlNavHead.getLayoutParams();
            headParams.height += statusBarHeight;
            mRlNavHead.setLayoutParams(headParams);
            RelativeLayout.LayoutParams avatarParams = (RelativeLayout.LayoutParams) mImgNavigationAvatar.getLayoutParams();
            avatarParams.height += statusBarHeight;
            mImgNavigationAvatar.setLayoutParams(avatarParams);
        }

        Drawable iconWeiboDrawable = createNavMenuItemDrawable(R.mipmap.ic_weibo);
        mRbWeibo.setCompoundDrawables(iconWeiboDrawable, null, null, null);

        Drawable iconMessageDrawable = createNavMenuItemDrawable(R.mipmap.ic_message);
        mRbMessage.setCompoundDrawables(iconMessageDrawable, null, null, null);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            mFriendWeiboFragment = new FriendWeiboFragment();
            mMessageFragment = new MessageUserFragment();
            transaction.add(R.id.attach_container,
                    mFriendWeiboFragment, FRIEND_WEIBO_FRAGMENT_TAG).commit();
        } else {
            mFriendWeiboFragment = getSupportFragmentManager().findFragmentByTag(FRIEND_WEIBO_FRAGMENT_TAG);
            if (mFriendWeiboFragment == null) {
                mFriendWeiboFragment = new FriendWeiboFragment();
            }
            mMessageFragment = getSupportFragmentManager().findFragmentByTag(MESSAGE_FRAGMENT_TAG);
            if (mMessageFragment == null) {
                mMessageFragment = new MessageUserFragment();
            }
        }

        mVisibleFragment = mFriendWeiboFragment;

    }

    private Drawable createNavMenuItemDrawable(int drawableId) {
        return DrawableUtil.createCheckThemeDrawable(this, drawableId, R.color.icon_normal_color, R.color.colorPrimary);
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
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        }else {
            params.setScrollFlags(0);
        }
        mToolbar.setLayoutParams(params);
    }

    @Override
    public void setUser(User user) {
        if (user != null) {
            mTvNavigationUsername.setText(user.getName());
            ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild().setCircle(true).build();
            ImageLoader.load(this, mImgNavigationAvatar, user.getAvatar_large(), R.drawable.circle_image_placeholder, config);
            mImgNavigationAvatar.setTag(user);
        }
    }


    @OnCheckedChanged(R.id.rb_weibo)
    public void onWeiboCheck(RadioButton view, boolean isCheck) {
        if (isCheck) {
            switchContent(mVisibleFragment, mFriendWeiboFragment, R.id.attach_container, FRIEND_WEIBO_FRAGMENT_TAG);
            mVisibleFragment = mFriendWeiboFragment;
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            changeToolBarFlag(true);
        }
    }

    @OnCheckedChanged(R.id.rb_message)
    public void onMessageCheck(RadioButton view, boolean isCheck) {
        if (isCheck) {
            switchContent(mVisibleFragment, mMessageFragment, R.id.attach_container, MESSAGE_FRAGMENT_TAG);
            mVisibleFragment = mMessageFragment;
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            changeToolBarFlag(false);
        }
    }

    @OnClick({R.id.img_navigation_avatar, R.id.tv_setting, R.id.tv_draft})
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
                RxBus.get().post(Key.EVENT_TOOL_BAR_DOUBLE_CLICK, mVisibleFragment);
                break;

            case R.id.tv_draft:
                Intent intent = DefaultFragmentActivity.starFragmentV4(this, DraftFragment.class, null);
                startActivity(intent);
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;
        }
    }

}

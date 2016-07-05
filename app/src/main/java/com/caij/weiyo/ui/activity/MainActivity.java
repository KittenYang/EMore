package com.caij.weiyo.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.SimpleUserPresent;
import com.caij.weiyo.present.imp.UserPresentImp;
import com.caij.weiyo.present.view.DetailUserView;
import com.caij.weiyo.present.view.SimpleUserView;
import com.caij.weiyo.source.local.LocalUserSource;
import com.caij.weiyo.source.server.ServerUserSource;
import com.caij.weiyo.ui.activity.publish.PublishWeiboActivity;
import com.caij.weiyo.ui.fragment.MessageFragment;
import com.caij.weiyo.ui.fragment.weibo.FriendWeiboFragment;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.utils.SystemUtil;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MainActivity extends BaseActivity implements SimpleUserView {

    private static final String FRIEND_WEIBO_FRAGMENT_TAG = "friend_weibo_fragment_tag";
    private static final String MESSAGE_FRAGMENT_TAG = "message_fragment_tag";

    @BindView(R.id.img_navigation_avatar)
    ImageView mImgNavigationAvatar;
    @BindView(R.id.tv_navigation_username)
    TextView mTvNavigationUsername;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);

        AccessToken token = UserPrefs.get().getWeiYoToken();
        SimpleUserPresent simpleUserPresent = new UserPresentImp(token.getAccess_token(),Long.parseLong(token.getUid()),
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
            mFriendWeiboFragment  = new FriendWeiboFragment();
            mMessageFragment = new MessageFragment();
            transaction.add(R.id.attach_container,
                    mFriendWeiboFragment, FRIEND_WEIBO_FRAGMENT_TAG).commit();
        }else {
            mFriendWeiboFragment = getSupportFragmentManager().findFragmentByTag(FRIEND_WEIBO_FRAGMENT_TAG);
            mMessageFragment = getSupportFragmentManager().findFragmentByTag(MESSAGE_FRAGMENT_TAG);
        }

        mVisibleFragment = mFriendWeiboFragment;
    }

    private Drawable createNavMenuItemDrawable(int drawableId) {
        int defColor = getResources().getColor(R.color.icon_normal_color);
        int selectedColor = getResources().getColor(R.color.colorPrimary);
        ColorStateList mIconTints = new ColorStateList(
                new int[][]{{android.R.attr.state_checked},
                        {}},
                new int[]{selectedColor, defColor});
        Drawable drawableIcon = DrawableCompat.wrap(ContextCompat.getDrawable(this, drawableId));
        DrawableCompat.setTintList(drawableIcon, mIconTints);
        drawableIcon.setBounds(0, 0 , drawableIcon.getIntrinsicWidth(), drawableIcon.getIntrinsicHeight());
        return drawableIcon;
    }

    public void switchContent(Fragment from, Fragment to, int id, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from).add(id, to, tag).commit();
        } else {
            transaction.hide(from).show(to).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.publish:
                Intent intent = new Intent(this, PublishWeiboActivity.class);
                startActivity(intent);
                break;

            case R.id.search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUser(User user) {
        if (user != null) {
            mTvNavigationUsername.setText(user.getName());
            ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild().setCircle(true).build();
            ImageLoader.load(this, mImgNavigationAvatar,  user.getAvatar_large(), R.mipmap.ic_default_circle_head_image, config);
        }
    }

    @Override
    public void showGetUserLoading(boolean isShow) {

    }

    @OnCheckedChanged(R.id.rb_weibo)
    public void onWeiboCheck(RadioButton view, boolean isCheck) {
        if (isCheck) {
            switchContent(mVisibleFragment, mFriendWeiboFragment, R.id.attach_container, FRIEND_WEIBO_FRAGMENT_TAG);
            mVisibleFragment = mFriendWeiboFragment;
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @OnCheckedChanged(R.id.rb_message)
    public void onMessageCheck(RadioButton view, boolean isCheck) {
        if (isCheck) {
            switchContent(mVisibleFragment, mMessageFragment, R.id.attach_container, MESSAGE_FRAGMENT_TAG);
            mVisibleFragment = mMessageFragment;
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }
}

package com.caij.weiyo.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.UserPresent;
import com.caij.weiyo.present.imp.UserPresentImp;
import com.caij.weiyo.present.view.UserView;
import com.caij.weiyo.source.local.LocalUserSource;
import com.caij.weiyo.source.server.ServerUserSource;
import com.caij.weiyo.ui.fragment.FriendWeiboFragment;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.utils.SystemUtil;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MainActivity extends BaseActivity implements UserView {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);

        AccessToken token = UserPrefs.get().getToken();
        UserPresent userPresent = new UserPresentImp(token.getAccess_token(),
                this, new ServerUserSource(), new LocalUserSource());
        userPresent.getWeiboUserInfoByUid(Long.parseLong(token.getUid()));

        if (Build.VERSION.SDK_INT >= 19) { // fitsSystemWindows
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


        getFragmentManager().beginTransaction().add(R.id.attach_container,
                new FriendWeiboFragment()).commit();
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

    @Override
    public void setUser(User user) {
        if (user != null) {
            mTvNavigationUsername.setText(user.getName());
            ImageLoader.load(this, mImgNavigationAvatar,
                    user.getAvatar_large(), true, R.mipmap.ic_default_circle_head_image);
        }
    }

    @OnCheckedChanged(R.id.rb_weibo)
    public void onWeiboCheck(RadioButton view, boolean isCheck) {
        if (isCheck) {

        }
    }

    @OnCheckedChanged(R.id.rb_message)
    public void onMessageCheck(RadioButton view, boolean isCheck) {
        if (isCheck) {

        }
    }
}

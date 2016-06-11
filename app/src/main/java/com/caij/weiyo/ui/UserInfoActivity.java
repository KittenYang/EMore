package com.caij.weiyo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.present.UserPresent;
import com.caij.weiyo.present.imp.UserPresentImp;
import com.caij.weiyo.present.view.UserView;
import com.caij.weiyo.source.local.LocalUserSource;
import com.caij.weiyo.source.server.ServerUserSource;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.SpannableStringUtil;
import com.caij.weiyo.utils.glide.CropCircleTransformation;
import com.caij.weiyo.view.ProfileCollapsingToolbarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/8.
 */
public class UserInfoActivity extends BaseActivity implements UserView {

    @BindView(R.id.imgCover)
    ImageView mImgCover;
    @BindView(R.id.txtName)
    TextView mTvName;
    @BindView(R.id.imgVerified)
    ImageView mImgVerified;
    @BindView(R.id.imgGender)
    ImageView imgGender;
    @BindView(R.id.layName)
    LinearLayout layName;
    @BindView(R.id.txtFriendsCounter)
    TextView txtFriendsCounter;
    @BindView(R.id.txtFollowersCounter)
    TextView txtFollowersCounter;
    @BindView(R.id.txtDesc)
    TextView txtDesc;
    @BindView(R.id.layRealDetail)
    RelativeLayout layRealDetail;
    @BindView(R.id.layDetail)
    RelativeLayout layDetail;
    @BindView(R.id.collapsingToolbar)
    ProfileCollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;

    UserPresent mUserPresent;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(Key.OBJ, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_profile_pager);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle("");
        AccessToken token = UserPrefs.get().getToken();
        mUserPresent = new UserPresentImp(token.getAccess_token(), this,
                new ServerUserSource(), new LocalUserSource());
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction()) && getIntent().getData() != null) {
            String userName = getIntent().getData().toString();
            if (userName.startsWith(SpannableStringUtil.USER_INFO_SCHEME)) {
                userName = userName.replace(SpannableStringUtil.USER_INFO_SCHEME, "");
            }
            mUserPresent.getWeiboUserInfoByName(userName);
        }else {
            User user = (User) getIntent().getSerializableExtra(Key.OBJ);
            fillDate(user);
            mUserPresent.getWeiboUserInfoByName(user.getName());
        }

    }

    private void fillDate(User user) {
        mTvName.setText(user.getName());
        ImageLoader.load(this, mImgCover, ImageLoader.ScaleType.CENTER_CROP, user.getCover_image_phone(),
                R.mipmap.default_user_info_bg);
//        ImageLoader.load(this, imgAvatar, user.getAvatar_large(), true, R.drawable.weibo_image_placeholder);
        // TODO is bad
        Glide.with(this).load(user.getAvatar_large()).bitmapTransform(new CropCircleTransformation(this))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        LogUtil.d(this, isFromMemoryCache + "");
                        if (resource instanceof GlideBitmapDrawable) {
                            collapsingToolbar.setAvatarBitmap(((GlideBitmapDrawable) resource).getBitmap());
                        }else if (resource instanceof GifDrawable) {
                            collapsingToolbar.setAvatarBitmap(((GifDrawable) resource).getFirstFrame());
                        }

                        return false;
                    }
                })
                .placeholder(R.mipmap.ic_default_circle_head_image).into(imgAvatar);
        txtDesc.setText(user.getDescription());
        txtFollowersCounter.setText(user.getFollowers_count() + "粉丝");
        txtFriendsCounter.setText(user.getFriends_count() + "关注");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_pager, menu);
        return true;
    }

    @Override
    public void setUser(User user) {
        fillDate(user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserPresent.onDestroy();
    }

}

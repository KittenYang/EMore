package com.caij.weiyo.view.weibo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.ui.UserInfoActivity;
import com.caij.weiyo.utils.DateUtil;
import com.caij.weiyo.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/5.
 */
public abstract class WeiboItemView extends FrameLayout {

    @BindView(R.id.sdv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.img_verified)
    ImageView imgVerified;
    @BindView(R.id.tv_desc)
    TextView tvDesc;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_re_content)
    TextView tvReContent;


    public WeiboItemView(Context context) {
        super(context);
        init(context, null, -1, -1);
    }

    public WeiboItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1, -1);
    }

    public WeiboItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, -1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeiboItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        inflate(context, getLayoutId(), this);
        ButterKnife.bind(this);
    }

    protected abstract int getLayoutId();


    public void setWeibo(Weibo weibo) {
        tvName.setText(weibo.getUser().getName());
        ImageLoader.ImageConfig config = new ImageLoader.ImageConfigBuild().setCircle(true).build();
        ImageLoader.load(getContext(), ivAvatar,
                weibo.getUser().getAvatar_large(), R.mipmap.ic_default_circle_head_image, config);

        String createAt = "";
        if (!TextUtils.isEmpty(weibo.getCreated_at()))
            createAt = DateUtil.convDate(getContext(), weibo.getCreated_at());
        String from = "";
        if (!TextUtils.isEmpty(weibo.getSource()))
            from = String.format("%s", Html.fromHtml(weibo.getSource()));
        String desc = String.format("%s %s", createAt, from);
        tvDesc.setText(desc);

        tvContent.setText(weibo.getContentSpannableString());

        tvName.setTag(weibo.getUser());
        ivAvatar.setTag(weibo.getUser());
    }

    @OnClick(R.id.tv_name)
    public void onNameClick(View view) {
        User user = (User) view.getTag();
        Intent intent = UserInfoActivity.newIntent(getContext(), user);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.sdv_avatar)
    public void onAvatarClick(View view) {
        User user = (User) view.getTag();
        Intent intent = UserInfoActivity.newIntent(getContext(), user);
        getContext().startActivity(intent);
    }


}

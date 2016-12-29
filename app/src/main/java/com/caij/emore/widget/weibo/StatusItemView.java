package com.caij.emore.widget.weibo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Status;
import com.caij.emore.image.ImageLoad;
import com.caij.emore.image.ImageLoadFactory;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.weibo.WeiboUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/5.
 */
public abstract class StatusItemView extends RelativeLayout {

    @BindView(R.id.sdv_avatar)
    protected ImageView ivAvatar;
    @BindView(R.id.tv_name)
    protected TextView tvName;
    @BindView(R.id.img_verified)
    protected ImageView imgVerified;
    @BindView(R.id.tv_source)
    protected TextView tvDesc;
    @BindView(R.id.tv_content)
    protected TextView tvContent;

    public StatusItemView(Context context) {
        super(context);
        init(context, null, -1, -1);
    }

    public StatusItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1, -1);
    }

    public StatusItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, -1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        inflate(context, getLayoutId(), this);
        ButterKnife.bind(this);
    }

    protected abstract int getLayoutId();

    public void setStatus(Status weibo) {
        if (TextUtils.isEmpty(weibo.getUser().getRemark())) {
            tvName.setText(weibo.getUser().getName());
        }else {
            tvName.setText(weibo.getUser().getRemark());
        }
        ImageLoadFactory.getImageLoad().loadImageCircle(getContext(), ivAvatar,
                weibo.getUser().getAvatar_large(), R.drawable.circle_image_placeholder);

        String createAt = DateUtil.convWeiboDate(getContext(), weibo.getCreated_at().getTime());
        String from = "";
        if (!TextUtils.isEmpty(weibo.getSource()))
            from = String.format("%s", Html.fromHtml(weibo.getSource()));
        String desc = String.format("%s %s", createAt, from);
        tvDesc.setText(desc);

        tvContent.setText(weibo.getContentSpannableString());

        tvContent.setTag(weibo);
        tvName.setTag(weibo.getUser());
        ivAvatar.setTag(weibo.getUser());

        WeiboUtil.setImageVerified(imgVerified, weibo.getUser());
    }

    @OnClick(R.id.tv_name)
    public void onNameClick(View view) {
        User user = (User) view.getTag();
        Intent intent = UserInfoActivity.newIntent(getContext(), user.getScreen_name());
        getContext().startActivity(intent);
    }

    @OnClick(R.id.sdv_avatar)
    public void onAvatarClick(View view) {
        User user = (User) view.getTag();
        Intent intent = UserInfoActivity.newIntent(getContext(), user.getScreen_name());
        getContext().startActivity(intent);
    }


}

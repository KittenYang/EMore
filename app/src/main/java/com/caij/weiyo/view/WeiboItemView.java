package com.caij.weiyo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.utils.DateUtil;
import com.caij.weiyo.utils.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Caij on 2016/6/5.
 */
public class WeiboItemView extends FrameLayout implements View.OnClickListener {

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
    @BindView(R.id.line_re_divider)
    View lineReDivider;
    @BindView(R.id.tv_re_content)
    TextView tvReContent;
    @BindView(R.id.rl_re_content)
    RelativeLayout rlReContent;
    @BindView(R.id.tv_like)
    TextView tvLike;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_repost_count)
    TextView tvRepostCount;
    @BindView(R.id.btn_menus)
    ImageView btnMenus;
    @BindView(R.id.pics_view)
    WeiboItemPicsView picsView;

    public WeiboItemView(Context context) {
        super(context);
        init(context);
    }

    public WeiboItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeiboItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeiboItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_weibo, this);
        ButterKnife.bind(this);
    }


    public void setWeibo(Weibo weibo) {
        tvName.setText(weibo.getUser().getName());
        ImageLoader.load(getContext(), ivAvatar,
                weibo.getUser().getAvatar_large(), true, R.mipmap.ic_default_circle_head_image);
        String createAt = "";
        if (!TextUtils.isEmpty(weibo.getCreated_at()))
            createAt = DateUtil.convDate(getContext(), weibo.getCreated_at());
        String from = "";
        if (!TextUtils.isEmpty(weibo.getSource()))
            from = String.format("%s", Html.fromHtml(weibo.getSource()));
        String desc = String.format("%s %s", createAt, from);
        tvDesc.setText(desc);

        tvRepostCount.setText(DateUtil.getCounter(getContext(), weibo.getReposts_count()));

        tvRepostCount.setTag(weibo);
        tvRepostCount.setOnClickListener(this);

        if (weibo.getVisible() == null || "0".equals(weibo.getVisible().getType())) //非正常可见微博禁止转发
            tvRepostCount.setVisibility(View.VISIBLE);
        else
            tvRepostCount.setVisibility(View.GONE);

        tvCommentCount.setText(DateUtil.getCounter(getContext(), weibo.getComments_count()));


        tvCommentCount.setTag(weibo);
        tvCommentCount.setOnClickListener(this);

        // 文本
//		txtContent.setText(data.getText());
        tvContent.setText(weibo.getContentSpannableString());

        // reContent
        Weibo reContent = weibo.getRetweeted_status();
        if (reContent == null) {
            rlReContent.setVisibility(View.GONE);
        } else {
            rlReContent.setVisibility(View.VISIBLE);
            rlReContent.setTag(reContent);

            tvReContent.setText(reContent.getContentSpannableString());
        }

        // pictures
        Weibo s = weibo.getRetweeted_status() != null ? weibo.getRetweeted_status() : weibo;
        picsView.setPics(s.getPic_urls());

        // group visiable
    }

    @Override
    public void onClick(View v) {

    }
}

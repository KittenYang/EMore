package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;
import com.caij.emore.ui.activity.publish.CommentStatusActivity;
import com.caij.emore.utils.CountUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.weibo.WeiboItemView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/16.
 */
public abstract class WeiboListItemView extends WeiboItemView {

    @BindView(R.id.tv_like)
    TextView tvLike;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_repost_count)
    TextView tvRepostCount;
    @BindView(R.id.btn_menus)
    ImageView btnMenus;
    @BindView(R.id.rl_weibo_bottom)
    RelativeLayout rlWeiboBottom;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_title)
    RelativeLayout llTitle;
    @BindView(R.id.iv_title_icon)
    ImageView ivTitleIcon;

    private OnClickListener onMenuClickListener;
    private OnClickListener onLikeClickListener;

    private boolean isDetail;

    public WeiboListItemView(Context context) {
        super(context);
    }

    public WeiboListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiboListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeiboListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setWeibo(Status weibo) {
        super.setWeibo(weibo);

        if(isDetail) {
            rlWeiboBottom.setVisibility(GONE);
            btnMenus.setVisibility(GONE);
        }else {
            rlWeiboBottom.setVisibility(VISIBLE);
            btnMenus.setVisibility(VISIBLE);
        }

        if (weibo.getTitle() != null && !isDetail) {
            llTitle.setVisibility(VISIBLE);
            tvTitle.setText(weibo.getTitle().getText());
            ImageLoader.load(getContext(), ivTitleIcon, weibo.getTitle().getIcon_url(), R.drawable.circle_image_placeholder);
        }else {
            llTitle.setVisibility(GONE);
        }

        tvRepostCount.setText(CountUtil.getCounter(getContext(), weibo.getReposts_count()));

        if (weibo.getVisible() == null || "0".equals(weibo.getVisible().getType())) //非正常可见微博禁止转发
            tvRepostCount.setVisibility(View.VISIBLE);
        else
            tvRepostCount.setVisibility(View.GONE);

        tvCommentCount.setText(CountUtil.getCounter(getContext(), weibo.getComments_count()));

        tvLike.setText(String.valueOf(weibo.getAttitudes_count()));
        tvLike.setSelected(weibo.getAttitudes_status() == 1);

        tvLike.setTag(weibo);
        tvCommentCount.setTag(weibo);
        tvRepostCount.setTag(weibo);
        btnMenus.setTag(weibo);

    }


    @OnClick({R.id.tv_like, R.id.tv_comment_count, R.id.tv_repost_count, R.id.btn_menus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_like:
                if (onLikeClickListener != null) {
                    onLikeClickListener.onClick(view);
                }
                break;
            case R.id.tv_comment_count: {
                Status weibo = (Status) view.getTag();
                Intent intent = CommentStatusActivity.newIntent(getContext(), weibo.getId());
                getContext().startActivity(intent);
                break;
            }

            case R.id.btn_menus:
                if (onMenuClickListener != null) {
                    onMenuClickListener.onClick(view);
                }
                break;

        }
    }

    public void setOnMenuClickListener(OnClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    public void setLikeClickListener(OnClickListener onLikeClickListener) {
        this.onLikeClickListener = onLikeClickListener;
    }

    public void setDetail(boolean isDetail) {
        this.isDetail = isDetail;
    }

}

package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Status;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.ui.activity.publish.RelayStatusActivity;
import com.caij.emore.utils.CountUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.weibo.StatusItemView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/16.
 */
public abstract class StatusListItemView extends StatusItemView {

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

    public StatusListItemView(Context context) {
        super(context);
    }

    public StatusListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);

        if(isDetail) {
            rlWeiboBottom.setVisibility(GONE);
            btnMenus.setVisibility(GONE);
        }else {
            rlWeiboBottom.setVisibility(VISIBLE);
            btnMenus.setVisibility(VISIBLE);
        }

        if (status.getTitle() != null && !isDetail) {
            llTitle.setVisibility(VISIBLE);
            tvTitle.setText(TextUtils.isEmpty(status.getTitle().getText()) ? "推荐" : status.getTitle().getText());
            ImageLoader.load(getContext(), ivTitleIcon, status.getTitle().getIcon_url(), R.drawable.circle_image_placeholder);
        }else {
            llTitle.setVisibility(GONE);
        }

        tvRepostCount.setText(CountUtil.getCounter(getContext(), status.getReposts_count()));

        if (status.getVisible() == null || "0".equals(status.getVisible().getType())) //非正常可见微博禁止转发
            tvRepostCount.setVisibility(View.VISIBLE);
        else
            tvRepostCount.setVisibility(View.GONE);

        tvCommentCount.setText(CountUtil.getCounter(getContext(), status.getComments_count()));

        tvLike.setText(CountUtil.getCounter(getContext(), status.getAttitudes_count()));
        tvLike.setSelected(status.getAttitudes_status() == 1);

        tvLike.setTag(status);
        tvCommentCount.setTag(status);
        tvRepostCount.setTag(status);
        btnMenus.setTag(status);

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
                Status status = (Status) view.getTag();
                Intent intent = StatusDetailActivity.newIntent(getContext(), status.getId());
                getContext().startActivity(intent);
                break;
            }

            case R.id.btn_menus:
                if (onMenuClickListener != null) {
                    onMenuClickListener.onClick(view);
                }
                break;

            case R.id.tv_repost_count:
                Status status = (Status) view.getTag();
                Intent intent = RelayStatusActivity.newIntent(getContext(), status);
                getContext().startActivity(intent);
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
        requestLayout();
    }


    public void setLikeSelected(Status status) {
        if (tvLike.getTag() == status) {
            tvLike.setSelected(status.getAttitudes_status() == 1);
        }
    }

    public void setLikeCount(Status status) {
        if (tvLike.getTag() == status) {
            tvLike.setText(CountUtil.getCounter(getContext(), status.getAttitudes_count()));
        }
    }

    public void setCommentCount(Status status) {
        if (tvCommentCount.getTag() == status) {
            tvCommentCount.setText(CountUtil.getCounter(getContext(), status.getComments_count()));
        }
    }

    public void setRelayCount(Status status) {
        if (tvRepostCount.getTag() == status) {
            tvRepostCount.setText(CountUtil.getCounter(getContext(), status.getReposts_count()));
        }
    }
}

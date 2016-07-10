package com.caij.emore.view.weibo;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.Weibo;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.ui.activity.publish.CommentWeiboActivity;
import com.caij.emore.ui.activity.publish.RepostWeiboActivity;
import com.caij.emore.utils.CountUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/16.
 */
public class WeiboListItemView extends WeiboItemView {

    @BindView(R.id.tv_like)
    TextView tvLike;
    @BindView(R.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R.id.tv_repost_count)
    TextView tvRepostCount;
    @BindView(R.id.btn_menus)
    ImageView btnMenus;

    @BindView(R.id.re_pics_view)
    WeiboItemPicsView rePicsView;
    @BindView(R.id.ll_re)
    LinearLayout llRe;
    @BindView(R.id.pics_view)
    WeiboItemPicsView picsView;
    private OnClickListener onMenuClickListener;
    private OnClickListener onLikeClickListener;

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

    protected int getLayoutId() {
        return R.layout.view_weibo;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);

        tvRepostCount.setText(CountUtil.getCounter(getContext(), weibo.getReposts_count()));

        if (weibo.getVisible() == null || "0".equals(weibo.getVisible().getType())) //非正常可见微博禁止转发
            tvRepostCount.setVisibility(View.VISIBLE);
        else
            tvRepostCount.setVisibility(View.GONE);

        tvCommentCount.setText(CountUtil.getCounter(getContext(), weibo.getComments_count()));

        tvContent.setText(weibo.getContentSpannableString());

        tvLike.setText(String.valueOf(weibo.getAttitudes_count()));
        tvLike.setSelected(weibo.isAttitudes());

        // reContent
        Weibo reWeibo = weibo.getRetweeted_status();
        if (reWeibo == null) {
            llRe.setVisibility(GONE);
            picsView.setVisibility(VISIBLE);

            picsView.setPics(weibo.getPic_urls());
        } else {
            llRe.setVisibility(View.VISIBLE);
            picsView.setVisibility(GONE);

            llRe.setTag(reWeibo);

            rePicsView.setPics(reWeibo.getPic_urls());
            tvReContent.setText(reWeibo.getContentSpannableString());
        }

        tvLike.setTag(weibo);
        tvCommentCount.setTag(weibo);
        tvRepostCount.setTag(weibo);
        btnMenus.setTag(weibo);
    }

    @OnClick(R.id.ll_re)
    public void onReLineaLayoutClick(View view) {
        Weibo weibo = (Weibo) view.getTag();
        Intent intent = WeiboDetialActivity.newIntent(getContext(), weibo);
        getContext().startActivity(intent);
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
                Weibo weibo = (Weibo) view.getTag();
                Intent intent = CommentWeiboActivity.newIntent(getContext(), weibo.getId());
                getContext().startActivity(intent);
                break;
            }
            case R.id.tv_repost_count: {
                Weibo weibo = (Weibo) view.getTag();
                Intent intent = RepostWeiboActivity.newIntent(getContext(), weibo);
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
}

package com.caij.weiyo.view.weibo;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.ui.activity.WeiboDetialActivity;
import com.caij.weiyo.utils.DateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/16.
 */
public class WeiboListItemView extends WeiboItemView{

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

        tvRepostCount.setText(DateUtil.getCounter(getContext(), weibo.getReposts_count()));

        if (weibo.getVisible() == null || "0".equals(weibo.getVisible().getType())) //非正常可见微博禁止转发
            tvRepostCount.setVisibility(View.VISIBLE);
        else
            tvRepostCount.setVisibility(View.GONE);

        tvCommentCount.setText(DateUtil.getCounter(getContext(), weibo.getComments_count()));

        tvContent.setText(weibo.getContentSpannableString());

        tvLike.setText(String.valueOf(weibo.getAttitudes_count()));

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
    }

    @OnClick(R.id.ll_re)
    public void onReLineaLayoutClick(View view) {
        Weibo weibo = (Weibo) view.getTag();
        Intent intent = WeiboDetialActivity.newIntent(getContext(), weibo);
        getContext().startActivity(intent);
    }


    @OnClick(R.id.tv_like)
    public void onLikeClick(View view) {
        Weibo weibo = (Weibo) view.getTag();
        AccessToken token = UserPrefs.get().getToken();
    }
}

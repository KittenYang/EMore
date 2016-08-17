package com.caij.emore.widget.weibo.list;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.widget.weibo.ImageInterface;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/16.
 */
public class RepostWeiboListItemView extends WeiboListItemView {

    @BindView(R.id.ll_re)
    LinearLayout llRe;
    @BindView(R.id.tv_re_content)
    TextView tvReContent;

    public RepostWeiboListItemView(Context context) {
        super(context);
    }

    public RepostWeiboListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepostWeiboListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RepostWeiboListItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected int getLayoutId() {
        return R.layout.view_repost_weibo;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        // reContent
        Weibo reWeibo = weibo.getRetweeted_status();
        llRe.setTag(reWeibo);

        tvReContent.setText(reWeibo.getContentSpannableString());
        tvReContent.setTag(reWeibo);
    }

    @Override
    protected void setImages(Weibo weibo, ImageInterface picsView) {
        picsView.setPics(weibo.getRetweeted_status().getPic_urls());
    }

    @OnClick(R.id.ll_re)
    public void onReLineaLayoutClick(View view) {
        Weibo weibo = (Weibo) view.getTag();
        if (weibo.getUser() != null) {
            Intent intent = WeiboDetialActivity.newIntent(getContext(), weibo.getId());
            getContext().startActivity(intent);
        }else {
            ToastUtil.show(getContext(), getContext().getString(R.string.weibo_deleted));
        }
    }

}

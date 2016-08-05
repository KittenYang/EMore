package com.caij.emore.view.weibo.detail;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.view.weibo.WeiboItemView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/13.
 */
public class WeiboDetailItemView extends WeiboItemView {

    @BindView(R.id.fl_pics_view)
    FrameLayout flPicsView;
    @BindView(R.id.fl_re_pics_view)
    FrameLayout flRePicsView;
    @BindView(R.id.ll_re)
    LinearLayout llRe;
    @BindView(R.id.tv_re_content)
    TextView tvReContent;

    public WeiboDetailItemView(Context context) {
        super(context);
    }

    public WeiboDetailItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiboDetailItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeiboDetailItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_weibo_detail;
    }

    @Override
    public void setWeibo(Weibo weibo) {
        super.setWeibo(weibo);
        Weibo reWeibo = weibo.getRetweeted_status();
        if (reWeibo == null) {
            llRe.setVisibility(GONE);
            flPicsView.setVisibility(VISIBLE);

            setImages(weibo, flPicsView);
        } else {
            llRe.setVisibility(View.VISIBLE);
            flPicsView.setVisibility(GONE);

            llRe.setTag(reWeibo);

            setImages(reWeibo, flRePicsView);
            tvReContent.setText(reWeibo.getContentSpannableString());

            tvReContent.setTag(reWeibo);
        }
    }

    private void setImages(Weibo weibo, ViewGroup viewGroup) {
        if (weibo.getPic_urls() == null || weibo.getPic_urls().size() == 0) {
            viewGroup.setVisibility(GONE);
        }else if (weibo.getPic_urls() != null && weibo.getPic_urls().size() == 1 && weibo.getPic_urls().get(0).isBigImageAndHeightBtWidth()) {
            viewGroup.setVisibility(VISIBLE);
            WeiboDetailItemImageViewGroupOf1BigImage imagesView = new WeiboDetailItemImageViewGroupOf1BigImage(getContext());
            viewGroup.addView(imagesView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            imagesView.setPics(weibo.getPic_urls());
        } else {
            viewGroup.setVisibility(VISIBLE);
            WeiboDetailItemImageViewGroup imagesView = new WeiboDetailItemImageViewGroup(getContext());
            viewGroup.addView(imagesView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            imagesView.setPics(weibo.getPic_urls());
        }
    }

    @OnClick(R.id.ll_re)
    public void onRePostLineaLayoutClick(View view) {
        Weibo weibo = (Weibo) view.getTag();
        if (weibo.getUser() != null) {
            Intent intent = WeiboDetialActivity.newIntent(getContext(), weibo.getId());
            getContext().startActivity(intent);
        }else {
            ToastUtil.show(getContext(), getContext().getString(R.string.weibo_deleted));
        }
    }

}

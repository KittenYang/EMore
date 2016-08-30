package com.caij.emore.widget.weibo.detail;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caij.emore.R;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.widget.weibo.WeiboItemView;

import java.util.LinkedHashMap;
import java.util.List;

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
        List<String> picIds = weibo.getPic_ids();
        LinkedHashMap<String, ImageInfo> imageInfoLinkedHashMap =  weibo.getPic_infos();
        if (weibo.getPic_ids() == null || weibo.getPic_ids().size() == 0) {
            viewGroup.setVisibility(GONE);
        }else if (picIds != null && picIds.size() == 1
                && ImageUtil.isLongImage(imageInfoLinkedHashMap.get(picIds.get(0)).getBmiddle().getWidth(),
                imageInfoLinkedHashMap.get(picIds.get(0)).getBmiddle().getHeight())) {
            viewGroup.setVisibility(VISIBLE);
            WeiboDetailItemImageViewGroupOf1BigImage imagesView = new WeiboDetailItemImageViewGroupOf1BigImage(getContext());
            viewGroup.addView(imagesView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            imagesView.setPics(weibo.getPic_ids(), weibo.getPic_infos());
        } else {
            viewGroup.setVisibility(VISIBLE);
            WeiboDetailItemImageViewGroup imagesView = new WeiboDetailItemImageViewGroup(getContext());
            viewGroup.addView(imagesView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            imagesView.setPics(weibo.getPic_ids(), weibo.getPic_infos());
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

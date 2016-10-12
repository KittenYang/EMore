package com.caij.emore.widget.weibo.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.caij.emore.R;
import com.caij.emore.bean.WeiboImageInfo;
import com.caij.emore.database.bean.Status;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.widget.weibo.ImageInterface;
import com.caij.emore.widget.weibo.list.RepostWeiboListItemView;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Caij on 2016/6/16.
 */
public class RepostWeiboImageItemView extends RepostWeiboListItemView {

    @BindView(R.id.fl_pics_view)
    FrameLayout flPicsView;

    public RepostWeiboImageItemView(Context context) {
        super(context);
    }

    public RepostWeiboImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepostWeiboImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RepostWeiboImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected int getLayoutId() {
        return R.layout.view_weibo_repost_text_and_image;
    }

    @Override
    public void setWeibo(Status weibo) {
        super.setWeibo(weibo);
        ImageInterface picsView = null;
        Status reWeibo = weibo.getRetweeted_status();
        List<String> picIds = reWeibo.getPic_ids();
        LinkedHashMap<String, WeiboImageInfo> imageInfoLinkedHashMap = reWeibo.getPic_infos();
        if (picIds == null || picIds.size() == 0) {
            flPicsView.setVisibility(GONE);
        }else if (picIds.size() == 1
                && ImageUtil.isLongImage(imageInfoLinkedHashMap.get(picIds.get(0)).getBmiddle().getWidth(),
                imageInfoLinkedHashMap.get(picIds.get(0)).getBmiddle().getHeight())) {
            picsView = new WeiboDetailItemImageViewGroupOf1BigImage(getContext());
            flPicsView.addView((View) picsView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            picsView = new WeiboDetailItemImageViewGroup(getContext());
            flPicsView.addView((View) picsView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        if (picsView != null) {
            setImages(weibo, picsView);
        }
    }

    protected void setImages(Status weibo, ImageInterface picsView) {
        picsView.setPics(weibo.getRetweeted_status().getPic_ids(), weibo.getRetweeted_status().getPic_infos());
    }

}

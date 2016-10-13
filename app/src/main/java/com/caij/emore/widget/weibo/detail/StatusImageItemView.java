package com.caij.emore.widget.weibo.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.caij.emore.R;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.database.bean.Status;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.widget.weibo.ImageInterface;
import com.caij.emore.widget.weibo.list.StatusListItemView;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Caij on 2016/9/1.
 */
public class StatusImageItemView extends StatusListItemView {


    @BindView(R.id.fl_pics_view)
    FrameLayout flPicsView;

    public StatusImageItemView(Context context) {
        super(context);
    }

    public StatusImageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusImageItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusImageItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
        List<String> picIds = status.getPic_ids();
        LinkedHashMap<String, StatusImageInfo> imageInfoLinkedHashMap = status.getPic_infos();
        ImageInterface picsView = null;
        if (picIds == null || picIds.size() == 0) {
            flPicsView.setVisibility(GONE);
        }else if (picIds.size() == 1
                && ImageUtil.isLongImage(imageInfoLinkedHashMap.get(picIds.get(0)).getBmiddle().getWidth(),
                imageInfoLinkedHashMap.get(picIds.get(0)).getBmiddle().getHeight())) {
            picsView = new StatusDetailItemImageViewGroupOf1BigImage(getContext());
            flPicsView.addView((View) picsView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            picsView = new StatusDetailItemImageViewGroup(getContext());
            flPicsView.addView((View) picsView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        if (picsView != null) {
            setImages(status, picsView);
        }
    }

    protected void setImages(Status weibo, ImageInterface picsView) {
        picsView.setPics(weibo.getPic_ids(), weibo.getPic_infos());
    }

    protected int getLayoutId() {
        return R.layout.view_weibo_text_and_image;
    }
}

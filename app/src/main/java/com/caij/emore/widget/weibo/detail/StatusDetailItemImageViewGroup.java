package com.caij.emore.widget.weibo.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.widget.weibo.list.StatusItemImageViewGroup;

/**
 * Created by Caij on 2016/6/13.
 */
public class StatusDetailItemImageViewGroup extends StatusItemImageViewGroup {

    public StatusDetailItemImageViewGroup(Context context) {
        super(context);
    }

    public StatusDetailItemImageViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusDetailItemImageViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusDetailItemImageViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int measureChildOnOneImage(int availableWidth) {
        //如果是详情就撑满屏幕
        int imageHeight;
        StatusImageInfo picUrl = mImageInfoLinkedHashMap.get(mPicIds.get(0));
        View child = getChildAt(0);
        if (picUrl.getBmiddle().getHeight() > 0 && picUrl.getBmiddle().getWidth() > 0) {
            imageHeight = (int) (availableWidth * picUrl.getBmiddle().getHeight() * 1.0f
                    / picUrl.getBmiddle().getWidth());
            child.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY));
        }else {
            imageHeight = availableWidth;
            child.measure(MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY));
        }
        return imageHeight;
    }


    @Override
    protected ImageLoader.ImageConfig processImageConfig(ImageLoader.ImageConfig  imageConfig) {
        //详情页是一张图片的时候不存到内存中
        if (mPicIds == null || mPicIds.size() == 1) {
            ImageLoader.ImageConfigBuild imageConfigBuild = ImageLoader.ImageConfigBuild.clone(imageConfig);
            imageConfigBuild.setSupportGif(true);
            imageConfigBuild.setCacheMemory(false);
            if (mImageInfoLinkedHashMap.get(mPicIds.get(0)).getBmiddle().getType().contains("gif")) {
                imageConfigBuild.setDiskCacheStrategy(ImageLoader.CacheConfig.SOURCE);
            }
            return imageConfigBuild.build();
        }else {
            return super.processImageConfig(imageConfig);
        }
    }
}

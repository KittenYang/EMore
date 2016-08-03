package com.caij.emore.view.weibo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.caij.emore.database.bean.PicUrl;
import com.caij.emore.utils.ImageLoader;

/**
 * Created by Caij on 2016/6/13.
 */
public class WeiboDetailItemPicsView extends WeiboItemPicsView{

    public WeiboDetailItemPicsView(Context context) {
        super(context);
    }

    public WeiboDetailItemPicsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeiboDetailItemPicsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeiboDetailItemPicsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int measureChildOnOneImage(int availableWidth) {
        //如果是详情就撑满屏幕
        int imageHeight;
        PicUrl picUrl = mPicUrls.get(0);
        View child = getChildAt(0);
        if (picUrl.getHeight() > 0 && picUrl.getWidth() > 0) {
            imageHeight = (int) (availableWidth * picUrl.getHeight() * 1.0f / picUrl.getWidth());
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
    protected void processImageConfigBuild(ImageLoader.ImageConfigBuild build) {
        super.processImageConfigBuild(build);
        //详情页是一张图片的时候不存到内存中
        if (mPicUrls == null || mPicUrls.size() == 1) {
            build.setCacheMemory(false);
            if (mPicUrls.get(0).getThumbnail_pic().contains("gif")) {
                build.setSupportGif(true);
                build.setDiskCacheStrategy(ImageLoader.CacheConfig.SOURCE);
            }
        }
    }
}

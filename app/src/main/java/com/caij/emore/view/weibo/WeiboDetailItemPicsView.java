package com.caij.emore.view.weibo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.caij.emore.bean.PicUrl;
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
    protected int measureChildOnOneImage(PicUrl picUrl, int availableWidth, View child) {
        //如果是详情就撑满屏幕
        int height = 0;
        int imageWidth = availableWidth;
        int imageHeight;
        if (picUrl.getHeight() > 0 && picUrl.getWidth() > 0) {
            imageHeight = (int) (imageWidth * picUrl.getHeight() * 1.0f / picUrl.getWidth());
            child.measure(MeasureSpec.makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY));
        }else {
            imageHeight = imageWidth;
            child.measure(MeasureSpec.makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY));
        }
        height = imageHeight;
        return height;
    }


    @Override
    protected void processImageConfigBuild(ImageLoader.ImageConfigBuild build) {
        super.processImageConfigBuild(build);
        //详情页是一张图片的时候不存到内存中
        if (mPicUrls == null || mPicUrls.size() == 1) {
            build.setCacheMemory(false);
            build.setSupportGif(true);
        }
    }
}

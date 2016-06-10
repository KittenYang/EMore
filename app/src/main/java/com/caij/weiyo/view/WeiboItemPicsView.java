package com.caij.weiyo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.utils.LogUtil;

import java.util.List;

/**
 * Created by Caij on 2016/6/6.
 */
public class WeiboItemPicsView extends ViewGroup implements View.OnClickListener {

    public static final float MAX_RADIO = 13 * 1.0f / 13;

    private int mSpaceWidth;
    private List<PicUrl> mPicUrls;
    private Handler mMainHandler;
    private ImageClickListener mImageClickListener;

    public WeiboItemPicsView(Context context) {
        super(context);
        init(context);
    }

    public WeiboItemPicsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeiboItemPicsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WeiboItemPicsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        for (int i = 0; i < 9; i ++) {
            addView(createImageView(LayoutParams.MATCH_PARENT));
        }
        mSpaceWidth = getResources().getDimensionPixelSize(R.dimen.weibo_image_space);
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        if (mPicUrls != null && mPicUrls.size() != 0) {
            int defaultImageWidth = (width - 2 * mSpaceWidth) / 3;
            for (int i = 0; i < getChildCount(); i ++) {
                View child = getChildAt(i);
                if (i < mPicUrls.size()) {
                    if (mPicUrls.size() == 1) {
                        PicUrl picUrl = mPicUrls.get(0);
                        int imageWidth;
                        int imageHeight;
                        if (picUrl.getHeight() > 0 && picUrl.getWidth() > 0) {
                            if (picUrl.getWidth() * 1.0f / picUrl.getHeight() < MAX_RADIO) { //宽比高小很多  竖着的图
                                imageWidth  = (int) (width * 1.0f / 2);
                                imageHeight = (int) (imageWidth * 1.34f);
                            }else if (picUrl.getHeight() * 1.0f / picUrl.getWidth() < MAX_RADIO) {//宽比高大很多  横着的图
                                imageWidth  = (int) (width * 1.0f / 3 * 2);
                                imageHeight = (int) (imageWidth / 1.34f);
                            }else { //接近正方形
                                imageWidth  = (int) (width * 1.0f / 3 * 2);
                                imageHeight = imageWidth;
                            }
                        }else { //没有宽度信息就是默认正方形
                            imageWidth  = (int) (width * 1.0f / 3 * 2);
                            imageHeight = imageWidth;
                        }
                        child.measure(MeasureSpec.makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY));
                        height = imageHeight;
                    } else if (mPicUrls.size() == 4){
                        height = defaultImageWidth * 2 + mSpaceWidth;
                        child.measure(MeasureSpec.makeMeasureSpec(defaultImageWidth, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(defaultImageWidth, MeasureSpec.EXACTLY));
                    }else {
                        height = defaultImageWidth * (mPicUrls.size() / 4 + 1) + mPicUrls.size() / 4 * mSpaceWidth;
                        child.measure(MeasureSpec.makeMeasureSpec(defaultImageWidth, MeasureSpec.EXACTLY),
                                MeasureSpec.makeMeasureSpec(defaultImageWidth, MeasureSpec.EXACTLY));
                    }
                }else {
                    //不需要测量
//                    child.measure(-1, -1);
                }
            }
        }
//        LogUtil.d(this, "onMeasure width = %s  height = %s", width, height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mPicUrls == null || mPicUrls.size() == 0)
            return;
        for (int i = 0; i < getChildCount(); i++) {
            ItemImageView childView = (ItemImageView) getChildAt(i);
            if (i < mPicUrls.size()) {
                if (mPicUrls.size() == 1) {
                    childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
                }else if (mPicUrls.size() == 4) {
                    int imageWidth = childView.getMeasuredWidth();
                    int line = i / 2; // 0 1  2
                    int column = i % 2;// 0 1  2
                    int left  = column * imageWidth + column * mSpaceWidth;
                    int top = line * imageWidth + line * mSpaceWidth;
                    int right  = column * imageWidth + column * mSpaceWidth + imageWidth;
                    int button = line * imageWidth + line * mSpaceWidth + imageWidth;
                    childView.layout(left, top, right, button);
//                    LogUtil.d(this, "item image index = %s left = %s top = %s right = %s button = %s",
//                            i, left, top, right, button);
                }else {
                    int imageWidth = childView.getMeasuredWidth();
                    int line = i / 3; // 0 1  2
                    int column = i % 3;// 0 1  2
                    int left  = column * imageWidth + column * mSpaceWidth;
                    int top = line * imageWidth + line * mSpaceWidth;
                    int right  = column * imageWidth + column * mSpaceWidth + imageWidth;
                    int button = line * imageWidth + line * mSpaceWidth + imageWidth;
                    childView.layout(left, top, right, button);
//                    LogUtil.d(this, "item image index = %s left = %s top = %s right = %s button = %s",
//                            i, left, top, right, button);
                }
            }else {
                //不需要定位
//                childView.layout(-1, -1, -1, -1);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) == child) {
                if (mPicUrls != null && i < mPicUrls.size()) {
                    return super.drawChild(canvas, child, drawingTime);
                }else {
                    return false;
                }
            }
        }
        return false;
    }

    private static boolean isImageWidthAndHeightWillSame(PicUrl picUrl1, PicUrl picUrl2) {
        return (picUrl1.getWidth() * 1.0f / picUrl1.getHeight() < MAX_RADIO && picUrl2.getWidth() * 1.0f / picUrl2.getHeight() < MAX_RADIO)
                ||(picUrl1.getHeight() * 1.0f / picUrl1.getWidth() < MAX_RADIO && picUrl2.getHeight() * 1.0f / picUrl2.getWidth() < MAX_RADIO)
                ||(picUrl1.getHeight() * 1.0f / picUrl1.getWidth() == MAX_RADIO && picUrl2.getHeight() * 1.0f / picUrl2.getWidth() == MAX_RADIO)
                || ((picUrl1.getWidth() <= 0 || picUrl1.getHeight() <=0) && (picUrl2.getWidth() <= 0 || picUrl2.getHeight() <=0));
    }

    public void setPics(List<PicUrl> picUrls) {
        if (null == picUrls || picUrls.size() == 0) {
            setVisibility(GONE);
        }else {
            setVisibility(VISIBLE);
            boolean isNeedRequestLayout = false;
            if (mPicUrls != null && mPicUrls.size() == picUrls.size()) { //这个时候图片长度一样
                if (mPicUrls.size() == 1 && !isImageWidthAndHeightWillSame(mPicUrls.get(0), picUrls.get(0))) { //如果都等于1 要判断图片比例的问题
                    isNeedRequestLayout = true;
                }
            }else { //布局不一样才requestLayout
                isNeedRequestLayout = true;
            }

            this.mPicUrls = picUrls;
            if (isNeedRequestLayout) {
                requestLayout();
            }

            //因为请求重新绘制requestLayout是通过主线程handler发送消息， 这个再通过handler发送消息展示图片就会在绘制以后
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    disPlayPics(mPicUrls);
                }
            });
        }
    }

    private void disPlayPics(List<PicUrl> picUrls) {
        for (int i = 0; i < getChildCount(); i++) {
            ItemImageView imgView = (ItemImageView) getChildAt(i);
            if (i < picUrls.size()) {
                PicUrl picUrl = picUrls.get(i);
                String url = picUrl.getThumbnail_pic().replace("thumbnail", "bmiddle");
                imgView.setUrl(picUrl);
                imgView.setTag(url);
                imgView.setOnClickListener(this);
                if (imgView.isLongImage() || imgView.isGif()) {
                    //高宽的3倍 设置长图
                    ImageLoader.load(getContext(), imgView, ImageLoader.ScaleType.TOP,
                            url, false, ImageLoader.Priority.LOW, R.drawable.weibo_image_placeholder);
                } else {
                    ImageLoader.load(getContext(), imgView, ImageLoader.ScaleType.CENTER_CROP,
                            url, false, R.drawable.weibo_image_placeholder);
                }
            }
        }
    }

    private ItemImageView createImageView(int imageWidth) {
        ItemImageView imageView = new ItemImageView(getContext());
        LayoutParams params = new LayoutParams(imageWidth, imageWidth);
        imageView.setLayoutParams(params);
        return imageView;
    }

    public void setImageClickListener(ImageClickListener imageClickListener) {
        mImageClickListener = imageClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mImageClickListener != null) {
            mImageClickListener.onClick((ItemImageView)v, (String) v.getTag());
        }
        LogUtil.d(this, v + "onclick");
    }

    public static interface ImageClickListener {
        public void onClick(ItemImageView view, String url);
    }
}

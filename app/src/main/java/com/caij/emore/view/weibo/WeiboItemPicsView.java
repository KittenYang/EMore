package com.caij.emore.view.weibo;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.database.bean.PicUrl;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.NavigationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/6/6.
 */
public class WeiboItemPicsView extends ViewGroup implements View.OnClickListener, ImageInterface, Runnable {

    public static final float MAX_RADIO = 13 * 1.0f / 13;

    public ImageLoader.ImageConfig mNormalImageConfig;
    public ImageLoader.ImageConfig mLongAndGifImageConfig;

    protected int mSpaceWidth;
    protected List<PicUrl> mPicUrls;

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
        ImageLoader.ImageConfigBuild normalImageConfigBuild = new ImageLoader.ImageConfigBuild()
                .setPriority(ImageLoader.Priority.LOW)
                .setScaleType(ImageLoader.ScaleType.TOP);
        mNormalImageConfig = normalImageConfigBuild.build();

        ImageLoader.ImageConfigBuild longAndGifConfigBuild = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP);
        mLongAndGifImageConfig = longAndGifConfigBuild.build();

        addItemViews();
        mSpaceWidth = getResources().getDimensionPixelSize(R.dimen.weibo_image_space);
    }

    protected void addItemViews() {
        for (int i = 0; i < 9; i ++) {
            addView(createImageView(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int height = 0;
        if (mPicUrls != null && mPicUrls.size() != 0) {
            if (mPicUrls.size() == 1) {
                height = measureChildOnOneImage(availableWidth);
            }else {
                height = measureChildOnMultipleImage(availableWidth);
            }
        }
//        LogUtil.d(this, "onMeasure width = %s  height = %s", width, height);
        height = height + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    protected int measureChildOnOneImage(int availableWidth) {
        View child = getChildAt(0);
        int imageHeight = 0;
        if (child != null && child.getVisibility() != GONE) {
            int imageWidth;
            PicUrl picUrl = mPicUrls.get(0);
            if (picUrl.getHeight() > 0 && picUrl.getWidth() > 0) {
                if (picUrl.getWidth() * 1.0f / picUrl.getHeight() < MAX_RADIO) { //宽比高小很多  竖着的图
                    imageWidth = (int) (availableWidth * 1.0f / 2);
                    imageHeight = (int) (imageWidth * 1.34f);
                } else if (picUrl.getHeight() * 1.0f / picUrl.getWidth() < MAX_RADIO) {//宽比高大很多  横着的图
                    imageWidth = (int) (availableWidth * 1.0f / 3 * 2);
                    imageHeight = (int) (imageWidth / 1.34f);
                } else { //接近正方形
                    imageWidth = (int) (availableWidth * 1.0f / 3 * 2);
                    imageHeight = imageWidth;
                }
            } else { //没有宽度信息就是默认正方形
                imageWidth = (int) (availableWidth * 1.0f / 3 * 2);
                imageHeight = imageWidth;
            }
            child.measure(MeasureSpec.makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(imageHeight, MeasureSpec.EXACTLY));
        }
        return imageHeight;
    }

    protected int measureChildOnMultipleImage(int availableWidth) {
        int defaultImageWidth = (availableWidth - 2 * mSpaceWidth) / 3;
        int imageLine = mPicUrls.size() == 4 ? 2 : mPicUrls.size() / 4 + 1;
        int height = defaultImageWidth * imageLine + (imageLine - 1) * mSpaceWidth;
        for (int i = 0; i < getChildCount(); i ++) {
            View child = getChildAt(i);

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            child.measure(MeasureSpec.makeMeasureSpec(defaultImageWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(defaultImageWidth, MeasureSpec.EXACTLY));
        }

        return height;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mPicUrls == null || mPicUrls.size() == 0)
            return;
        for (int i = 0; i < getChildCount(); i++) {
            ItemImageView childView = (ItemImageView) getChildAt(i);
            int startX = getPaddingLeft();
            int startY = getPaddingTop();
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            if (i < mPicUrls.size()) {
                if (mPicUrls.size() == 1) {
                    childView.layout(startX, startY, startX + childView.getMeasuredWidth(), startY + childView.getMeasuredHeight());
                }else if (mPicUrls.size() == 4) {
                    int imageWidth = childView.getMeasuredWidth();
                    int line = i / 2; // 0 1  2
                    int column = i % 2;// 0 1  2
                    int left  = startX + column * imageWidth + column * mSpaceWidth;
                    int top = startY + line * imageWidth + line * mSpaceWidth;
                    int right  = left + imageWidth;
                    int button = top + imageWidth;
                    childView.layout(left, top, right, button);
//                    LogUtil.d(this, "item image index = %s left = %s top = %s right = %s button = %s",
//                            i, left, top, right, button);
                }else {
                    int imageWidth = childView.getMeasuredWidth();
                    int line = i / 3; // 0 1  2
                    int column = i % 3;// 0 1  2
                    int left  = startX + column * imageWidth + column * mSpaceWidth;
                    int top = startY + line * imageWidth + line * mSpaceWidth;
                    int right  = left + imageWidth;
                    int button = top + imageWidth;
                    childView.layout(left, top, right, button);
//                    LogUtil.d(this, "item image index = %s left = %s top = %s right = %s button = %s",
//                            i, left, top, right, button);
                }
            }
        }
    }

    public static boolean isImageWidthAndHeightSame(PicUrl picUrl1, PicUrl picUrl2) {
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

            for (int i = 0; i < getChildCount(); i++) {
                if (i < picUrls.size()) {
                    getChildAt(i).setVisibility(VISIBLE);
                }else {
                    getChildAt(i).setVisibility(GONE);
                }
            }

            //当只有一张的时候复用  图片宽度是有变化 需要判断
            boolean isNeedRequestLayout = false;
            if (mPicUrls != null
                    && mPicUrls.size() == picUrls.size()
                    && mPicUrls.size() == 1
                    && !isImageWidthAndHeightSame(mPicUrls.get(0), picUrls.get(0))) { //这个时候图片长度一样
                    isNeedRequestLayout = true;
            }

            if (isNeedRequestLayout && !isLayoutRequested()) {
                requestLayout();
            }

            this.mPicUrls = picUrls;

            //因为请求重新绘制requestLayout是通过主线程handler发送消息， 这个再通过handler发送消息展示图片就会在绘制以后
            post(this);
        }
    }

    @Override
    public void run() {
        disPlayPics(mPicUrls);
    }

    protected void disPlayPics(List<PicUrl> picUrls) {
        for (int i = 0; i < getChildCount(); i++) {
            ItemImageView imgView = (ItemImageView) getChildAt(i);
            if (i < picUrls.size()) {
                PicUrl picUrl = picUrls.get(i);
                String url = picUrl.getBmiddle_pic();
                imgView.setUrl(picUrl);
                imgView.setTag(url);
                imgView.setOnClickListener(this);

                if (imgView.isLongImage() || imgView.isGif()) {
                    ImageLoader.ImageConfig imageConfig  = processImageConfig(mLongAndGifImageConfig);
                    ImageLoader.loadUrl(getContext(), imgView, url, R.drawable.weibo_image_placeholder, imageConfig);
                } else {
                    ImageLoader.ImageConfig imageConfig = processImageConfig(mNormalImageConfig);
                    ImageLoader.loadUrl(getContext(), imgView, url, R.drawable.weibo_image_placeholder, imageConfig);
                }
            }else {
                //不可见的view 加载null 是为了在item复用imageview没有复用时关闭之前的请求
                ImageLoader.loadUrl(getContext(), imgView, null, R.drawable.weibo_image_placeholder, mNormalImageConfig);
            }
        }
    }

    protected ImageLoader.ImageConfig processImageConfig(ImageLoader.ImageConfig imageConfig) {
        return imageConfig;
    }

    private ItemImageView createImageView(int imageWidth, int imageHeight) {
        ItemImageView imageView = new ItemImageView(getContext());
        LayoutParams params = new LayoutParams(imageWidth, imageHeight);
        imageView.setLayoutParams(params);
        return imageView;
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> paths = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < mPicUrls.size(); i ++) {
            PicUrl picUrl = mPicUrls.get(i);
            View child = getChildAt(i);
            paths.add(picUrl.getBmiddle_pic());
            if (child == v) {
                position = i;
            }
        }
        NavigationUtil.startImagePreActivity(getContext(), v, paths, position);

    }

}

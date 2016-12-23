package com.caij.emore.widget.weibo.list;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.caij.emore.R;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.bean.StatusImageInfo;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.widget.weibo.ImageInterface;
import com.caij.emore.widget.weibo.ItemImageView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Caij on 2016/6/6.
 */
public class StatusItemImageViewGroup extends ViewGroup implements View.OnClickListener, ImageInterface, Runnable {

    public static final float MAX_RADIO = 13 * 1.0f / 13;

    public ImageLoader.ImageConfig mNormalImageConfig;
    public ImageLoader.ImageConfig mLongAndGifImageConfig;

    protected int mSpaceWidth;
    protected LinkedHashMap<String, StatusImageInfo> mImageInfoLinkedHashMap;
    protected List<String> mPicIds;

    private Handler mHandler;

    public StatusItemImageViewGroup(Context context) {
        super(context);
        init(context);
    }

    public StatusItemImageViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StatusItemImageViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StatusItemImageViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        ImageLoader.ImageConfigBuild normalImageConfigBuild = new ImageLoader.ImageConfigBuild()
                .setPriority(ImageLoader.Priority.LOW)
                .setScaleType(ImageLoader.ScaleType.TOP);
        mLongAndGifImageConfig = normalImageConfigBuild.build();

        ImageLoader.ImageConfigBuild longAndGifConfigBuild = new ImageLoader.ImageConfigBuild()
                .setScaleType(ImageLoader.ScaleType.CENTER_CROP);
        mNormalImageConfig = longAndGifConfigBuild.build();

        addItemViews();
        mSpaceWidth = getResources().getDimensionPixelSize(R.dimen.weibo_image_space);

        mHandler = new Handler();
    }

    protected void addItemViews() {
        for (int i = 0; i < 9; i ++) {
            ItemImageView imageView = createImageView(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setOnClickListener(this);
            addView(imageView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int height = 0;
        if (mImageInfoLinkedHashMap != null && mImageInfoLinkedHashMap.size() != 0) {
            if (mImageInfoLinkedHashMap.size() == 1) {
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
            StatusImageInfo imageInfo = mImageInfoLinkedHashMap.get(mPicIds.get(0));
            if (imageInfo.getBmiddle().getHeight() > 0 && imageInfo.getBmiddle().getWidth() > 0) {
                if (imageInfo.getBmiddle().getWidth() * 1.0f / imageInfo.getBmiddle().getHeight() < MAX_RADIO) { //宽比高小很多  竖着的图
                    imageWidth = (int) (availableWidth * 1.0f / 2);
                    imageHeight = (int) (imageWidth * 1.34f);
                } else if (imageInfo.getBmiddle().getHeight() * 1.0f / imageInfo.getBmiddle().getWidth() < MAX_RADIO) {//宽比高大很多  横着的图
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
        int imageLine = mImageInfoLinkedHashMap.size() == 4 ? 2 : mImageInfoLinkedHashMap.size() / 4 + 1;
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
        if (mImageInfoLinkedHashMap == null || mImageInfoLinkedHashMap.size() == 0)
            return;
        for (int i = 0; i < getChildCount(); i++) {
            ItemImageView childView = (ItemImageView) getChildAt(i);
            int startX = getPaddingLeft();
            int startY = getPaddingTop();
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            if (i < mImageInfoLinkedHashMap.size()) {
                if (mImageInfoLinkedHashMap.size() == 1) {
                    childView.layout(startX, startY, startX + childView.getMeasuredWidth(), startY + childView.getMeasuredHeight());
                }else if (mImageInfoLinkedHashMap.size() == 4) {
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

    public static boolean isImageWidthAndHeightSame(StatusImageInfo picUrl1, StatusImageInfo picUrl2) {
        return (picUrl1.getBmiddle().getWidth() * 1.0f / picUrl1.getBmiddle().getHeight() < MAX_RADIO && picUrl2.getBmiddle().getWidth() * 1.0f / picUrl2.getBmiddle().getHeight() < MAX_RADIO)
                ||(picUrl1.getBmiddle().getHeight() * 1.0f / picUrl1.getBmiddle().getWidth() < MAX_RADIO && picUrl2.getBmiddle().getHeight() * 1.0f / picUrl2.getBmiddle().getWidth() < MAX_RADIO)
                || ((picUrl1.getBmiddle().getWidth() <= 0 || picUrl1.getBmiddle().getHeight() <=0) && (picUrl2.getBmiddle().getWidth() <= 0 || picUrl2.getBmiddle().getHeight() <=0));
    }

    @Override
    public void run() {
        disPlayPics(mPicIds, mImageInfoLinkedHashMap);
    }

    protected void disPlayPics(List<String> mPicIds, LinkedHashMap<String, StatusImageInfo> linkedHashMap) {
        for (int i = 0; i < getChildCount(); i++) {
            ItemImageView imgView = (ItemImageView) getChildAt(i);
            if (i < mPicIds.size()) {
                StatusImageInfo imageInfo = linkedHashMap.get(mPicIds.get(i));
                String url = imageInfo.getBmiddle().getUrl();
                imgView.setUrl(imageInfo);

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
        ArrayList<ImageInfo> paths = new ArrayList<>();
        ArrayList<ImageInfo> hdPaths = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < mPicIds.size(); i ++) {
            StatusImageInfo picUrl = mImageInfoLinkedHashMap.get(mPicIds.get(i));
            View child = getChildAt(i);
            paths.add(new ImageInfo(picUrl.getBmiddle().getUrl(), picUrl.getBmiddle().getWidth(),
                    picUrl.getBmiddle().getHeight(), getImageType(picUrl.getBmiddle().getType())));

            StatusImageInfo.Image orignImage = picUrl.getOriginal();
            if (orignImage != null) {
                hdPaths.add(new ImageInfo(orignImage.getUrl(), orignImage.getWidth(),
                        orignImage.getHeight(), getImageType(orignImage.getType())));
            }else {
                hdPaths.add(null);
            }

            if (child == v) {
                position = i;
            }
        }
        NavigationUtil.startImagePreActivity(getContext(), v, paths, hdPaths, position);
    }

    protected ImageUtil.ImageType getImageType(String type) {
        if (ImageUtil.ImageType.GIF.getValue().toLowerCase().contains(type)
                || ImageUtil.ImageType.GIF.getValue().toUpperCase().contains(type)) {
            return ImageUtil.ImageType.GIF;
        }else {
            return ImageUtil.ImageType.PNG;
        }
    }

    @Override
    public void setPics(List<String> pic_ids, LinkedHashMap<String, StatusImageInfo> imageInfoLinkedHashMap) {
        if (null == imageInfoLinkedHashMap || imageInfoLinkedHashMap.size() == 0) {
            setVisibility(GONE);
        }else {
            setVisibility(VISIBLE);

            for (int i = 0; i < getChildCount(); i++) {
                if (i < imageInfoLinkedHashMap.size()) {
                    getChildAt(i).setVisibility(VISIBLE);
                }else {
                    getChildAt(i).setVisibility(GONE);
                }
            }

            //当只有一张的时候复用  图片宽度是有变化 需要判断
            boolean isNeedRequestLayout = false;
            if (mImageInfoLinkedHashMap != null
                    && mImageInfoLinkedHashMap.size() == imageInfoLinkedHashMap.size()
                    && mImageInfoLinkedHashMap.size() == 1
                    && !isImageWidthAndHeightSame(mImageInfoLinkedHashMap.get(mPicIds.get(0)), imageInfoLinkedHashMap.get(pic_ids.get(0)))) { //这个时候图片长度一样
                isNeedRequestLayout = true;
            }

            if (isNeedRequestLayout && !isLayoutRequested()) {
                requestLayout();
            }

            this.mImageInfoLinkedHashMap = imageInfoLinkedHashMap;
            this.mPicIds = pic_ids;

            //因为请求重新绘制requestLayout是通过主线程handler发送消息， 这个再通过handler发送消息展示图片就会在绘制以后
            mHandler.post(this);
        }
    }
}

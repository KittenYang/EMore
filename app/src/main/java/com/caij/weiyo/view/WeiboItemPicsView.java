package com.caij.weiyo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caij.weiyo.R;
import com.caij.weiyo.bean.PicUrl;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/6/6.
 */
public class WeiboItemPicsView extends ViewGroup{

    private List<Rect> mPicsRect;
    private int mSpaceWidth;
    private List<PicUrl> mPicUrls;

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
            addView(createImageView(LayoutParams.WRAP_CONTENT));
        }
        mSpaceWidth = getResources().getDimensionPixelSize(R.dimen.weibo_image_space);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        if (mPicUrls != null && mPicUrls.size() != 0) {
            int defaultImageWidth = (width - 2 * mSpaceWidth) / 3;
            if (mPicUrls.size() == 1) {
                Rect rect = create1PicsRectF(mPicsRect, width, mPicUrls.get(0));
                height = rect.height();
            } else if (mPicUrls.size() == 4) {
                create4PicsRectF(mPicsRect, mPicUrls, defaultImageWidth, mSpaceWidth);
                height = defaultImageWidth * 2 + mSpaceWidth;
            } else {
                create9PicsRectF(mPicsRect, mPicUrls, defaultImageWidth, mSpaceWidth);
                height = defaultImageWidth * (mPicUrls.size() / 4 + 1) + mPicUrls.size() / 4 * mSpaceWidth;
            }
            disPlayPics(mPicUrls, mPicsRect);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mPicsRect == null || mPicsRect.size() == 0)
            return;

        for (int i = 0; i < mPicsRect.size(); i++) {
            try {
                Rect imgRect = mPicsRect.get(i);
                ImageView childView = (ImageView) getChildAt(i);
                childView.layout(imgRect.left, imgRect.top, imgRect.right, imgRect.bottom);
            }catch (Exception e) {

            }
        }
    }

    public void setPics(List<PicUrl> picUrls) {
        if (null == picUrls || picUrls.size() == 0) {
            setVisibility(GONE);
        }else {
            this.mPicUrls = picUrls;
            if (mPicsRect == null) {
                mPicsRect = new ArrayList<>(9);
            }else {
                mPicsRect.clear();
            }
            setVisibility(VISIBLE);
            requestLayout();
        }

    }

    private void disPlayPics(List<PicUrl> picUrls, List<Rect> rects) {
        for (int i = 0; i < getChildCount(); i++) {
            ImageView imgView = (ImageView) getChildAt(i);

            // 隐藏多余的View
            if (i >= picUrls.size()) {
                imgView.setVisibility(View.GONE);
            }else {
                imgView.setVisibility(View.VISIBLE);
                LayoutParams params = imgView.getLayoutParams();
                Rect rect = rects.get(i);
                int imageWidth = rect.width();
                int imageHeight = rect.height();
                if (params == null) {
                    params = new LayoutParams(imageWidth, imageHeight);
                    imgView.setLayoutParams(params);
                }else {
                    if (params.height != imageHeight || params.width != imageWidth) {
                        params.height = imageHeight;
                        params.width = imageWidth;
                        imgView.setLayoutParams(params);
                    }
                }

                ImageLoader.load(getContext(), imgView, ImageView.ScaleType.CENTER_CROP,
                        picUrls.get(i).getThumbnail_pic().replace("thumbnail", "bmiddle"));
            }
        }
    }

    private static void create9PicsRectF(List<Rect> picsRectF, List<PicUrl> picUrls, int imageWidth,
                                         int spaceWidth) {
        Rect rect = null;
        for (int i = 0; i < picUrls.size(); i ++) {
            int line = i / 3; // 0 1  2
            int column = i % 3;// 0 1  2
            int left  = column * imageWidth + column * spaceWidth;
            int top = line * imageWidth + line * spaceWidth;
            int right  = column * imageWidth + column * spaceWidth + imageWidth;
            int button = line * imageWidth + line * spaceWidth + imageWidth;
            rect = new Rect(left, top, right, button);
            picsRectF.add(rect);
        }

    }

    private static void create4PicsRectF(List<Rect> picsRectF, List<PicUrl> picUrls, int imageWidth, int spaceWidth) {
        Rect rect = null;
        for (int i = 0; i < picUrls.size(); i ++) {
            int line = i / 2; // 0 1  2
            int column = i % 2;// 0 1  2
            int left  = column * imageWidth + column * spaceWidth;
            int top = line * imageWidth + line * spaceWidth;
            int right  = column * imageWidth + column * spaceWidth + imageWidth;
            int button = line * imageWidth + line * spaceWidth + imageWidth;
            rect = new Rect(left, top, right, button);
            picsRectF.add(rect);
        }
    }

    private static Rect create1PicsRectF(List<Rect> mPicsRectF, int width,  PicUrl picUrl) {
        Rect rect = null;
        if (picUrl.getHeight() > 0 && picUrl.getWidth() > 0) {
            float maxRadio = 13 * 1.0f / 16;
            if (picUrl.getWidth() * 1.0f / picUrl.getHeight() < maxRadio) { //宽比高小很多  竖着的图
                int imageWidth  = (int) (width * 1.0f / 2);
                int imageHeight = (int) (imageWidth * 1.34f);
                rect = new Rect(0, 0, imageWidth, imageHeight);
            }else if (picUrl.getHeight() * 1.0f / picUrl.getWidth() < maxRadio) {//宽比高大很多  横着的图
                int imageWidth  = (int) (width * 1.0f / 3 * 2);
                int imageHeight = (int) (imageWidth / 1.34f);
                rect = new Rect(0, 0, imageWidth, imageHeight);
            }else { //接近正方形
                int imageWidth  = (int) (width * 1.0f / 3 * 2);
                int imageHeight = imageWidth;
                rect = new Rect(0, 0, imageWidth, imageHeight);
            }
        }else { //没有宽度信息就是默认正方形
            int imageWidth  = (int) (width * 1.0f / 3 * 2);
            int imageHeight = imageWidth;
            rect = new Rect(0, 0, imageWidth, imageHeight);
        }
        mPicsRectF.add(rect);
        return rect;
    }

    private ImageView createImageView(int imageWidth) {
        ImageView imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(imageWidth, imageWidth);
        imageView.setLayoutParams(params);
        return imageView;
    }
}

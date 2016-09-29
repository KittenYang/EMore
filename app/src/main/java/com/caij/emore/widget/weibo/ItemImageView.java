package com.caij.emore.widget.weibo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.caij.emore.R;
import com.caij.emore.bean.WeiboImageInfo;
import com.caij.emore.utils.ImageUtil;

/**
 * Created by Caij on 2016/6/7.
 */
public class ItemImageView extends ImageView {

    private Bitmap mGifDrawable;
    private Paint paint;
    private WeiboImageInfo mImageInfo;
    private Bitmap mLongImageDrawable;

    public ItemImageView(Context context) {
        super(context);
        init();
    }

    public ItemImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isGif()) {
            if (mGifDrawable == null) {
                mGifDrawable = BitmapFactory.decodeResource(getResources(), R.mipmap.timeline_image_gif);
                paint = new Paint();
            }
            canvas.drawBitmap(mGifDrawable, getWidth() - mGifDrawable.getWidth(), getHeight() - mGifDrawable.getHeight(), paint);
        }else if (isLongImage()) {
            if (mLongImageDrawable == null) {
                mLongImageDrawable = BitmapFactory.decodeResource(getResources(), R.mipmap.timeline_image_longimage);
                paint = new Paint();
            }
            canvas.drawBitmap(mLongImageDrawable, getWidth() - mLongImageDrawable.getWidth(), getHeight() - mLongImageDrawable.getHeight(), paint);
        }
    }

    public void setUrl(WeiboImageInfo imageInfo) {
        mImageInfo = imageInfo;
    }

    public boolean isLongImage() {
        return mImageInfo != null
                && ImageUtil.isLongImage(mImageInfo.getBmiddle().getWidth(), mImageInfo.getBmiddle().getHeight());
    }

    public boolean isGif() {
        return mImageInfo != null && mImageInfo.getBmiddle().getType().contains("gif");
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        boolean superOnTouchEvent =  super.onTouchEvent(event);
//        Drawable drawable = getDrawable();
//        if (drawable == null) return superOnTouchEvent;
//        GifDrawable gifDrawable = null;
//        if (drawable instanceof GifDrawable) {
//            gifDrawable = (GifDrawable) drawable;
//        }else if (drawable instanceof TransitionDrawable) {
//            // we fade in images on loadUrl which uses a TransitionDrawable; check its layers
//            TransitionDrawable fadingIn = (TransitionDrawable) drawable;
//            for (int i = 0; i < fadingIn.getNumberOfLayers(); i++) {
//                if (fadingIn.getDrawable(i) instanceof GifDrawable) {
//                    gifDrawable = (GifDrawable) fadingIn.getDrawable(i);
//                    break;
//                }
//            }
//        }
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                if (gifDrawable != null && gifDrawable.isRunning()) {
//                    gifDrawable.stop();
//                }
//                break;
//        }
//        return superOnTouchEvent;
//    }
//
//    @Override
//    public boolean onLongClick(View v) {
//        //如果是gif图片 长按播放 up和cancel停止
//        Drawable drawable = getDrawable();
//        GifDrawable gifDrawable = null;
//        if (drawable instanceof GifDrawable) {
//            gifDrawable = (GifDrawable) drawable;
//        }else if (drawable instanceof TransitionDrawable) {
//            // we fade in images on loadUrl which uses a TransitionDrawable; check its layers
//            TransitionDrawable fadingIn = (TransitionDrawable) drawable;
//            for (int i = 0; i < fadingIn.getNumberOfLayers(); i++) {
//                if (fadingIn.getDrawable(i) instanceof GifDrawable) {
//                    gifDrawable = (GifDrawable) fadingIn.getDrawable(i);
//                    break;
//                }
//            }
//        }
//        if (gifDrawable != null) {
//            gifDrawable.start();
//        }
//        return true;
//    }

}

package com.caij.weiyo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.caij.weiyo.R;
import com.caij.weiyo.bean.PicUrl;

/**
 * Created by Caij on 2016/6/7.
 */
public class ItemImageView extends ImageView implements View.OnLongClickListener {

    private Bitmap mGifDrawable;
    private Paint paint;
    private PicUrl mPicUrl;
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
        setOnLongClickListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() <= 0 && getHeight() <= 0) {
            return;
        }
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

    public void setUrl(PicUrl picUrl) {
        mPicUrl = picUrl;
    }

    //在layout 后才有效
    public boolean isLongImage() {
        return mPicUrl != null && (mPicUrl.getWidth() / mPicUrl.getHeight() > 3 || mPicUrl.getHeight() / mPicUrl.getWidth() > 3);
    }


    public boolean isGif() {
        return mPicUrl != null && mPicUrl.getThumbnail_pic().contains("gif");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean superOnTouchEvent =  super.onTouchEvent(event);
        Drawable drawable = getDrawable();
        if (drawable == null) return superOnTouchEvent;
        GifDrawable gifDrawable = null;
        if (drawable instanceof GifDrawable) {
            gifDrawable = (GifDrawable) drawable;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (gifDrawable != null && gifDrawable.isRunning()) {
                    gifDrawable.stop();
                }
                break;
        }
        return superOnTouchEvent;
    }

    @Override
    public boolean onLongClick(View v) {
        //如果是gif图片 长按播放 up和cancel停止
        Drawable drawable = getDrawable();
        GifDrawable gifDrawable = null;
        if (drawable instanceof GifDrawable) {
            gifDrawable = (GifDrawable) drawable;
        }
        if (gifDrawable != null) {
            gifDrawable.start();
        }
        return true;
    }

}

package com.caij.weiyo.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.caij.weiyo.R;

/**
 * Created by Caij on 2016/6/7.
 */
public class ItemImageView extends ImageView{

    private boolean isGif;
    private Bitmap mGifDrawble;
    private Paint paint;

    public ItemImageView(Context context) {
        super(context);
    }

    public ItemImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() > 0 && getHeight() > 0) {
            if (isGif) {
                if (mGifDrawble == null) {
                    mGifDrawble = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_gif);
                    paint = new Paint();
                }
                canvas.drawBitmap(mGifDrawble, getWidth() - mGifDrawble.getWidth(), getHeight() - mGifDrawble.getHeight(), paint);
            }
        }
    }

    public void setUrl(String url) {
        isGif = url != null && url.contains("gif");
    }

    //在layout 后才有效
    public boolean isLongImage() {
        return getWidth() / getHeight() >= 3 || getHeight() / getWidth() >= 3;
    }
}

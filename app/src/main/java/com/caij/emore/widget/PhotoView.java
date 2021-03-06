package com.caij.emore.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.caij.emore.utils.LogUtil;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Caij on 2016/6/24.
 */
public class PhotoView extends ImageView {

    private PhotoViewAttacher mAttacher;

    public PhotoView(Context context) {
        super(context);
        init();
    }

    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mAttacher = new PhotoViewAttacher(this);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mAttacher.update();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttacher.cleanup();
        LogUtil.d(this, "onDetachedFromWindow mAttacher.cleanup()");
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                l.onClick(view);
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mAttacher.setOnLongClickListener(l);
//        super.setOnLongClickListener(l);
    }
}

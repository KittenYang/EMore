package com.caij.emore.widget.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Caij on 2016/8/10.
 */
public class MyDrawable extends Drawable{

    private Drawable mDrawable;
    private WeakReference<ImageView> mImageViewReference;
    private Matrix mMatrix;

    public MyDrawable(Drawable drawable, ImageView imageView) {
        mDrawable = drawable;
        mImageViewReference = new WeakReference<ImageView>(imageView);
        mMatrix = new Matrix();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mDrawable != null && mMatrix != null) {
            canvas.save();
            canvas.concat(mMatrix);
            mDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public void setScaleAndTranslate(float scale, float dx, float dy) {
        mMatrix.reset();
        mMatrix.setScale(scale, scale);
        mMatrix.postTranslate(Math.round(dx), Math.round(dy));
//        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mDrawable.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return mDrawable.getOpacity();
    }

    @Override
    public int getIntrinsicHeight() {
        ImageView  imageView = mImageViewReference.get();
        if (imageView != null) {
            return imageView.getHeight();
        }
        return  -1;
    }

    @Override
    public int getIntrinsicWidth() {
        ImageView  imageView = mImageViewReference.get();
        if (imageView != null) {
            return imageView.getWidth();
        }
        return  -1;
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        Log.d(this.getClass().getSimpleName(), "onBoundsChange" + bounds.left + ":" + bounds.top+ ":" + bounds.right+ ":" + bounds.bottom);
        Rect rect = new Rect(0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight());
        mDrawable.setBounds(rect);
    }
}

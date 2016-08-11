package com.caij.emore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Caij on 2016/8/11.
 */
public class ClipImageView extends ImageView {

    private Matrix mDrawMatrix;

    public ClipImageView(Context context) {
        super(context);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClipImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getDrawable() == null) {
            return; // couldn't resolve the URI
        }

        if (getDrawable().getIntrinsicWidth() == 0 || getDrawable().getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            getDrawable().draw(canvas);
        } else {
            int saveCount = canvas.getSaveCount();
            canvas.save();

            canvas.translate(getPaddingLeft(), getPaddingTop());

            if (mDrawMatrix != null) {
                canvas.concat(mDrawMatrix);
            }
            getDrawable().draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

    public void setDrawMatrix(Matrix matrix) {
        this.mDrawMatrix = matrix;
    }

}

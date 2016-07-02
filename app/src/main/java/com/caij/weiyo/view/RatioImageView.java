package com.caij.weiyo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.caij.weiyo.R;

public class RatioImageView extends ImageView {

    private float mRatio;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context,  AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RatioImageView, defStyle, 0);
        mRatio = a.getFloat(R.styleable.RatioImageView_ratio, -1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        if (mRatio > 0) {
            setMeasuredDimension(measuredWidth, (int) (measuredWidth * mRatio));
        }
    }

    public void setRatio(int ratio) {
        this.mRatio = ratio;
        requestLayout();
    }
}

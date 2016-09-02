package com.caij.emore.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.caij.emore.R;

public class RatioRelativeLayout extends RelativeLayout {

    private float mRatio;

    public RatioRelativeLayout(Context context) {
        this(context, null);
    }

    public RatioRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RatioRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context,  AttributeSet attrs, int defStyle) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RatioView, defStyle, 0);
        mRatio = a.getFloat(R.styleable.RatioView_ratio, -1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width = getSize(widthMode, widthSize);
        int height = (int) (width * mRatio);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private int getSize(int mode, int maxSize) {
        if (mode == MeasureSpec.EXACTLY) {
            return maxSize;
        }else if (mode == MeasureSpec.UNSPECIFIED) {
            return maxSize;
        }else {
            return maxSize;
        }
    }

    public void setRatio(int ratio) {
        this.mRatio = ratio;
        requestLayout();
    }
}

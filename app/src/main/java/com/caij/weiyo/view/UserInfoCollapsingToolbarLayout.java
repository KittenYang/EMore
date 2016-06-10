package com.caij.weiyo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.widget.TextView;

import com.caij.weiyo.R;
import com.caij.weiyo.utils.SystemUtil;

/**
 * Created by Caij on 2016/6/9.
 */
public class UserInfoCollapsingToolbarLayout extends CollapsingToolbarLayout {

    private Drawable mInsetForeground;
    private Rect mTempRect;
    private int mVerticalOffset;
    private TextView name;

    public UserInfoCollapsingToolbarLayout(Context context) {
        super(context);
        init(context, null, -1);
    }

    public UserInfoCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public UserInfoCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ScrimInsetsView, defStyleAttr, 0);
        mInsetForeground = a.getDrawable(R.styleable.ScrimInsetsView_appInsetForeground);
        a.recycle();
        mTempRect = new Rect();
        name = (TextView) findViewById(R.id.txtName);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int height = 0;
//        for (int i = 0; i < getChildCount(); i ++) {
//            View child = getChildAt(i);
//            height += child.getMeasuredHeight();
//        }
//        setMeasuredDimension(getMeasuredWidth(), height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mTempRect.set(0, -mVerticalOffset, getWidth(), SystemUtil.getStatusBarHeight(getContext()) + -mVerticalOffset);
//        mInsetForeground.setBounds(0, 0, getMeasuredWidth(), SystemUtil.getStatusBarHeight(getContext()));
//        mInsetForeground.draw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).addOnOffsetChangedListener(innerOffsetChangedListener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(innerOffsetChangedListener);
        }
    }

    private AppBarLayout.OnOffsetChangedListener innerOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            mVerticalOffset = verticalOffset;

            float b = verticalOffset * 1.0f / getHeight();
            b  = Math.abs(b);
            name.setY(getChildAt(0).getHeight() * b);
        }
    };
}

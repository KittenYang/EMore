package com.caij.emore.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.caij.emore.R;


/**
 * Created by Ca1j on 2016/12/15.
 */

public class BadgeTextView extends TextView {

    private Paint mBadgePaint;
    private int mBadgePadding = 0;
    private RectF mBadgeRectF;
    private Drawable mBackground;

    public BadgeTextView(Context context) {
        this(context, null);
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setGravity(Gravity.CENTER);

        mBadgePaint = new Paint();
        mBadgePaint.setAntiAlias(true);
        mBadgePaint.setStyle(Paint.Style.FILL);
        // 设置mBadgeText居中，保证mBadgeText长度为1时，文本也能居中
        mBadgePaint.setTextAlign(Paint.Align.CENTER);

        mBadgeRectF = new RectF();

        mBadgePadding = getPaddingTop();

        initBackground();
    }

    private void initBackground() {
        int color;
        if (mBackground instanceof ColorDrawable) {
            color = ((ColorDrawable)mBackground).getColor();
        }else {
            color = ContextCompat.getColor(getContext(), R.color.red_message_bg);
        }

        // 设置徽章背景色
        if (mBadgePaint != null) {
            mBadgePaint.setColor(color);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBadge(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (getText() == null || getText().length() == 1) {
            width = height;
        }else {
            width = Math.max(height, width) + mBadgePadding * 4;
        }

        setMeasuredDimension(width, height);
    }

    private void drawBadge(Canvas canvas) {
        String text = getText() == null ? "" : getText().toString();

        // 计算徽章背景的宽高
        int badgeHeight = getHeight();

        int badgeWidth;

        // 当mBadgeText的长度为1或0时，计算出来的高度会比宽度大，此时设置宽度等于高度
        if (text.length() == 1 || text.length() == 0) {
            badgeWidth = badgeHeight;
        } else {
            badgeWidth = getWidth();
        }

        // 计算徽章背景上下的值
        mBadgeRectF.top = 0;
        mBadgeRectF.bottom = badgeHeight;

        // 计算徽章背景左右的值
        mBadgeRectF.left = 0;
        mBadgeRectF.right = badgeWidth;

        // 绘制徽章背景
        canvas.drawRoundRect(mBadgeRectF, getHeight() / 2f, getHeight() / 2f, mBadgePaint);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }


    @Override
    public void setBackground(Drawable background) {
        mBackground = background;
        initBackground();
        postInvalidate();
//        super.setBackground(background);
    }
}

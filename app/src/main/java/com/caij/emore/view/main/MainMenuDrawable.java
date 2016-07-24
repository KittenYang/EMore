package com.caij.emore.view.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;

import com.caij.emore.R;

/**
 * Created by Caij on 2016/7/24.
 */
public class MainMenuDrawable extends DrawerArrowDrawable implements ActionBarDrawerToggle.DrawerToggle {

    private boolean redVisible = false;
    private int redSize;
    Paint p = new Paint();

    /**
     * @param context used to get the configuration for the drawable from
     */
    public MainMenuDrawable(Context context) {
        super(context);
        p.setColor(Color.RED);// 设置红色
        redSize = context.getResources().getDimensionPixelOffset(R.dimen.main_menu_red_size);
    }

    public void setPosition(float position) {
        if (position == 1f) {
            setVerticalMirror(true);
        } else if (position == 0f) {
            setVerticalMirror(false);
        }
        setProgress(position);
    }

    public float getPosition() {
        return getProgress();
    }

    @Override
    public void setRedVisible(boolean visible) {
        if (visible != redVisible) {
            redVisible = visible;
            invalidateSelf();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (redVisible) {
            Rect bounds = getBounds();
            canvas.drawCircle(bounds.right - redSize / 2, bounds.top + redSize, redSize / 2, p);
        }
    }
}

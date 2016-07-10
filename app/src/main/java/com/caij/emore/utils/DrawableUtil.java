package com.caij.emore.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.caij.emore.R;

/**
 * Created by Caij on 2016/7/10.
 */
public class DrawableUtil {

    public static Drawable createCheckThemeDrawable(Context context, int drawableId, int colorId, int selectColorId) {
        int defColor = context.getResources().getColor(colorId);
        int selectedColor = context.getResources().getColor(selectColorId);
        ColorStateList mIconTints = new ColorStateList(
                new int[][]{{android.R.attr.state_checked},
                        {}},
                new int[]{selectedColor, defColor});
        Drawable drawableIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, drawableId));
        DrawableCompat.setTintList(drawableIcon, mIconTints);
        drawableIcon.setBounds(0, 0, drawableIcon.getIntrinsicWidth(), drawableIcon.getIntrinsicHeight());
        return drawableIcon;
    }

    public static Drawable createPressThemeDrawable(Context context, int drawableId, int colorId, int selectColorId) {
        int defColor = context.getResources().getColor(colorId);
        int selectedColor = context.getResources().getColor(selectColorId);
        ColorStateList mIconTints = new ColorStateList(
                new int[][]{{android.R.attr.state_pressed},
                        {}},
                new int[]{selectedColor, defColor});
        Drawable drawableIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, drawableId));
        DrawableCompat.setTintList(drawableIcon, mIconTints);
        drawableIcon.setBounds(0, 0, drawableIcon.getIntrinsicWidth(), drawableIcon.getIntrinsicHeight());
        return drawableIcon;
    }

    public static Drawable createSelectThemeDrawable(Context context, int drawableId, int colorId, int selectColorId) {
        int defColor = context.getResources().getColor(colorId);
        int selectedColor = context.getResources().getColor(selectColorId);
        ColorStateList mIconTints = new ColorStateList(
                new int[][]{{android.R.attr.state_selected},
                        {}},
                new int[]{selectedColor, defColor});
        Drawable drawableIcon = DrawableCompat.wrap(ContextCompat.getDrawable(context, drawableId));
        DrawableCompat.setTintList(drawableIcon, mIconTints);
        drawableIcon.setBounds(0, 0, drawableIcon.getIntrinsicWidth(), drawableIcon.getIntrinsicHeight());
        return drawableIcon;
    }
}

package com.caij.emore.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.caij.emore.utils.LogUtil;
import com.caij.emore.view.drawable.MyDrawable;

import java.lang.reflect.Field;

/**
 * A transition that morphs a rectangle into a circle, changing it's background color.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MorphDialogToFab extends ChangeBounds {

    private String PROPNAME_BOUNDS = "android:changeBounds:bounds";

    public MorphDialogToFab() {
        try {
            Class clazz = ChangeBounds.class;
            Field field_bound = clazz.getDeclaredField("PROPNAME_BOUNDS");
            field_bound.setAccessible(true);
            PROPNAME_BOUNDS = (String) field_bound.get(clazz);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Animator createAnimator(final ViewGroup sceneRoot,
                                   TransitionValues startValues,
                                   TransitionValues endValues) {
        Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (startValues == null || endValues == null || changeBounds == null) {
            return null;
        }

        final ImageView imageView = (ImageView) startValues.view;
        final Drawable drawable = imageView.getDrawable();

        final int dwidth = drawable.getIntrinsicWidth();
        final int dheight = drawable.getIntrinsicHeight();
        float dx = 0, dy = 0;
        final float maxScale;
        float minScale;
        final int imageHeight = imageView.getHeight();
        final int imageWidth = imageView.getWidth();

        if (dwidth * imageHeight > imageWidth * dheight) {
            maxScale = (float) imageWidth / (float) dwidth;
            minScale = (float) imageHeight / (float) dheight;
            dy = (imageHeight - dheight * maxScale) * 0.5f;
            dx = (imageWidth - dwidth * maxScale) * 0.5f;
        } else {
            maxScale = (float) imageHeight / (float) dheight;
            minScale = (float) imageWidth / (float) dwidth;
            dx = (imageWidth - dwidth * maxScale) * 0.5f;
            dy = (imageHeight - dheight * maxScale) * 0.5f;
        }

        final MyDrawable myDrawable = new MyDrawable(drawable, imageView);
        imageView.setImageDrawable(myDrawable);

        myDrawable.setScaleAndTranslate(maxScale, dx, dy);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int dwidth = drawable.getIntrinsicWidth();
                final int dheight = drawable.getIntrinsicHeight();
                float dx = 0, dy = 0;
                final float maxScale;
                float minScale;
                final int imageHeight = imageView.getHeight();
                final int imageWidth = imageView.getWidth();
                if (dwidth * imageHeight > imageWidth * dheight) {
                    maxScale = (float) imageWidth / (float) dwidth;
                    minScale = (float) imageHeight / (float) dheight;
                } else {
                    maxScale = (float) imageHeight / (float) dheight;
                    minScale = (float) imageWidth / (float) dwidth;
                }

                float value = (float) animation.getAnimatedValue();
                float dscale = (minScale - maxScale) * value;
                float scale = maxScale + dscale;

                dy = (imageHeight - dheight * scale) * 0.5f;
                dx = (imageWidth - dwidth * scale) * 0.5f;

                myDrawable.setScaleAndTranslate(scale, dx, dy);
            }
        });

        AnimatorSet transition = new AnimatorSet();
        transition.playTogether(changeBounds, valueAnimator);
        transition.setDuration(500);
        transition.setInterpolator(AnimationUtils.loadInterpolator(sceneRoot.getContext(),
                android.R.interpolator.fast_out_slow_in));

        return transition;
    }

}

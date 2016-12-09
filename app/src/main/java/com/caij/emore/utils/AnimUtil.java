package com.caij.emore.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by Caij on 2016/12/9.
 */

public class AnimUtil {


    public static void scale(View view, float... values) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", values);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", values);
        animatorX.setDuration(500);
        animatorY.setDuration(500);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.start();
    }
}

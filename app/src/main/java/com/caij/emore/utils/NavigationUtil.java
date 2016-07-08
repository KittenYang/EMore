package com.caij.emore.utils;

import android.content.Context;
import android.content.Intent;

import com.caij.emore.ui.activity.ImagePrewActivity;
import com.caij.emore.ui.activity.SelectImageActivity;

import java.util.ArrayList;

/**
 * Created by Caij on 2016/6/23.
 */
public class NavigationUtil {

    public static Intent newSelectImageActivityIntent(Context context, int maxImageCount) {
        Intent intent = SelectImageActivity.newIntent(context, maxImageCount);
        return intent;
    }

    public static Intent newImagePreActivityIntent(Context context, ArrayList<String> paths, int position) {
        Intent intent = ImagePrewActivity.newIntent(context, paths, position);
        return intent;
    }
}

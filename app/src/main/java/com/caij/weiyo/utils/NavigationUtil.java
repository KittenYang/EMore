package com.caij.weiyo.utils;

import android.content.Context;
import android.content.Intent;

import com.caij.weiyo.ui.activity.SelectImageActivity;

/**
 * Created by Caij on 2016/6/23.
 */
public class NavigationUtil {

    public static Intent getSelectImageActivity(Context context) {
        Intent intent = new Intent(context, SelectImageActivity.class);
        return intent;
    }
}

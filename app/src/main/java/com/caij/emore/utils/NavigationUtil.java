package com.caij.emore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.ui.activity.ImagePrewActivity;
import com.caij.emore.ui.activity.LocalImagePrewActivity;
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

    public static void startImagePreActivity(Context context, View view, ArrayList<ImageInfo> paths, ArrayList<ImageInfo> hdPaths, int position) {
        Intent intent = ImagePrewActivity.newIntent(context, paths, hdPaths, position);
//        ActivityOptionsCompat optionsCompat
//                = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                (Activity) context, view, Key.TRANSIT_PIC);
//        ActivityCompat.startActivity((Activity) context, intent,
//                optionsCompat.toBundle());
        context.startActivity(intent);
    }

    public static void startLocalImagePreActivity(Context context, View view, ArrayList<String> paths, int position) {
        ArrayList<ImageInfo> imageInfos = new ArrayList<>(paths.size());
        for (String path : paths) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setUrl(path);
            imageInfos.add(imageInfo);
        }
        Intent intent = LocalImagePrewActivity.newIntent(context, imageInfos, null, position);
        context.startActivity(intent);
    }
}

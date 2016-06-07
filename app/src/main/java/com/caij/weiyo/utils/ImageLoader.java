package com.caij.weiyo.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.caij.weiyo.okhttp.CropCircleTransformation;
import com.caij.weiyo.okhttp.TopTransformation;

/**
 * Created by Caij on 2016/6/7.
 */
public class ImageLoader {

    public static interface ScaleType {
        int CENTER_CROP = 1;
        int FIT_CENTER = 2;
        int TOP = 3;  //优先显示上面部分
    }

    private static void load(Context context, ImageView view, int scaleType, String url, int width, int height, Transformation transformation) {
        DrawableTypeRequest<String> request = createRequest(context, url);
        switch (scaleType) {
            case ScaleType.CENTER_CROP:
                request.centerCrop();
                break;

            case ScaleType.FIT_CENTER:
                request.fitCenter();
                break;

            case ScaleType.TOP:
                request.bitmapTransform(new TopTransformation(context), transformation);
                break;
        }
        if (width > 0 && height > 0) {
            request.override(width, height);
        }

        if (transformation != null) {
            request.bitmapTransform(transformation);
        }

        request.into(view);
    }

    public static void load(Context context, ImageView view, int scaleType, String url, int width, int height) {
        load(context, view, scaleType, url, width, height, null);
    }


    public static void load(Context context, ImageView view, int scaleType, String url) {
        load(context, view, scaleType, url, -1, -1, null);
    }

    public static void load(Context context, ImageView view, String url) {
        load(context, view, ScaleType.FIT_CENTER, url);
    }

    public static void load(Context context, ImageView view, String url, boolean isCircle) {
        load(context, view, ScaleType.FIT_CENTER, url, -1, -1,
                isCircle ? new CropCircleTransformation(context) : null);
    }

    private static DrawableTypeRequest<String> createRequest(Context context, String url) {
        return (DrawableTypeRequest<String>) Glide.with(context).
                load(url).
                diskCacheStrategy(DiskCacheStrategy.RESULT);
    }



}

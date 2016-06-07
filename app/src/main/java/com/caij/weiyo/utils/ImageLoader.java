package com.caij.weiyo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.caij.weiyo.okhttp.CropCircleTransformation;

/**
 * Created by Caij on 2016/6/7.
 */
public class ImageLoader {

    public static void load(Context context, ImageView view, ImageView.ScaleType scaleType, String url) {
        DrawableTypeRequest<String> request = createRequest(context, url);
        switch (scaleType) {
            case CENTER_CROP:
                request.centerCrop();
                break;

            case FIT_CENTER:
                request.fitCenter();
                break;
        }
        request.into(view);
    }

    public static void load(Context context, ImageView view, String url) {
        load(context, view, ImageView.ScaleType.FIT_CENTER, url);
    }

    public static void load(Context context, ImageView view, String url, boolean isCircle) {
        if (isCircle) {
            DrawableRequestBuilder request = createRequest(context, url);
            request.bitmapTransform(new CropCircleTransformation(context));
            request.into(view);
        }else {
            load(context, view, url);
        }
    }

    private static DrawableTypeRequest<String> createRequest(Context context, String url) {
        return (DrawableTypeRequest<String>) Glide.with(context).
                load(url).
                diskCacheStrategy(DiskCacheStrategy.RESULT);
    }

}

package com.caij.weiyo.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.caij.weiyo.utils.glide.CropCircleTransformation;
import com.caij.weiyo.utils.glide.GifTarget;
import com.caij.weiyo.utils.glide.TopTransformation;

/**
 * Created by Caij on 2016/6/7.
 */
public class ImageLoader {

    public static interface ScaleType {
        int CENTER_CROP = 1;
        int FIT_CENTER = 2;
        int TOP = 3;  //优先显示上面部分
    }

    public static interface Priority {
        int HIGH = 3;
        int NORMAL = 2;
        int LOW = 1;
    }

    private static void load(Context context, ImageView view, int scaleType, String url, int width,
                             int height, Transformation transformation,
                             boolean isSupportGif, int priority,
                             int resourceId ) {
        DrawableTypeRequest<String> request = createRequest(context, url);
        switch (scaleType) {
            case ScaleType.CENTER_CROP:
                request.centerCrop();
                break;

            case ScaleType.FIT_CENTER:
                request.fitCenter();
                break;

            case ScaleType.TOP:
                request.bitmapTransform(new TopTransformation(context));
                break;
        }
        if (width > 0 && height > 0) {
            request.override(width, height);
        }

        if (transformation != null) {
            request.bitmapTransform(transformation);
        }

        switch (priority) {
            case Priority.HIGH:
                request.priority(com.bumptech.glide.Priority.HIGH);
                break;

            case Priority.LOW:
                request.priority(com.bumptech.glide.Priority.LOW);
                break;

            default:
                request.priority(com.bumptech.glide.Priority.NORMAL);
                break;
        }

        request.placeholder(resourceId);
        if (isSupportGif) {
            request.into(view);
        }else {
            request.into(new GifTarget(view, false));
        }
    }

    public static void load(Context context, ImageView view, int scaleType, String url, int width, int height, int resourceId) {
        load(context, view, scaleType, url, width, height, null, false, Priority.NORMAL, resourceId);
    }


    public static void load(Context context, ImageView view, int scaleType, String url, boolean isSupportGif, int resourceId ) {
        load(context, view, scaleType, url, isSupportGif, Priority.NORMAL, resourceId);
    }

    public static void load(Context context, ImageView view, int scaleType, String url, boolean isSupportGif, int priority, int resourceId ) {
        load(context, view, scaleType, url, -1, -1, null, isSupportGif, priority, resourceId);
    }

    public static void load(Context context, ImageView view, int scaleType, String url, int resourceId ) {
        load(context, view, scaleType, url, false, resourceId);
    }

    public static void load(Context context, ImageView view, String url,int resourceId ) {
        load(context, view, ScaleType.FIT_CENTER, url, resourceId);
    }


    public static void load(Context context, ImageView view, String url, boolean isCircle, int resourceId) {
        load(context, view, ScaleType.FIT_CENTER, url, -1, -1,
                isCircle ? new CropCircleTransformation(context) : null, false, Priority.NORMAL, resourceId);
    }

    private static DrawableTypeRequest<String> createRequest(Context context, String url) {
        return (DrawableTypeRequest<String>) Glide.with(context).
                load(url).
                diskCacheStrategy(DiskCacheStrategy.RESULT);
    }

}

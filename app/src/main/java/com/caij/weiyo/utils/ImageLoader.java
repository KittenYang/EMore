package com.caij.weiyo.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.caij.weiyo.utils.glide.CropCircleTransformation;
import com.caij.weiyo.utils.glide.GifTarget;
import com.caij.weiyo.utils.glide.TopTransformation;
import com.caij.weiyo.view.weibo.ItemImageView;

import java.io.File;
import java.util.concurrent.ExecutionException;

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

    public static interface CacheConfig {
        int RESULT = 1;
        int SOURCE = 2;
        int All = 3;
    }

    public static class ImageConfig {

        private int scaleType;
        private int height;
        private int width;
        private boolean isCircle;
        private int priority;
        private int diskCacheStrategy;
        private boolean isCacheMemory;
        private boolean isSupportGif;

        private ImageConfig(ImageConfigBuild build) {
            scaleType = build.scaleType;
            height = build.height;
            width = build.width;
            isCircle = build.isCircle;
            priority = build.priority;
            diskCacheStrategy = build.diskCacheStrategy;
            isCacheMemory = build.isCacheMemory;
            isSupportGif = build.isSupportGif;
        }
    }

    public static class ImageConfigBuild {
        private int scaleType = ScaleType.FIT_CENTER;
        private int height = -1;
        private int width = -1;
        private boolean isCircle = false;
        private int priority = Priority.NORMAL;
        private int diskCacheStrategy = CacheConfig.RESULT;
        private boolean isCacheMemory = true;
        private boolean isSupportGif = false;

        public ImageConfigBuild(){

        }

        public ImageConfigBuild setWidthAndHeight(int width, int height) {
            this.width = width;
            this.height = height;

            return this;
        }

        public ImageConfigBuild setScaleType(int scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public ImageConfigBuild setCircle(boolean isCircle) {
            this.isCircle = isCircle;
            return this;
        }

        public ImageConfigBuild setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public ImageConfigBuild setDiskCacheStrategy(int diskCacheStrategy) {
            this.diskCacheStrategy = diskCacheStrategy;
            return this;
        }

        public ImageConfigBuild setCacheMemory(boolean isCacheMemory) {
            this.isCacheMemory = isCacheMemory;
            return this;
        }

        public ImageConfigBuild setSupportGif(boolean isSupportGif) {
            this.isSupportGif = isSupportGif;
            return this;
        }

        public ImageConfig build() {
            return new ImageConfig(this);
        }
    }

    public static void load(Context context, ImageView view, String url, int resourceId,  ImageConfig imageConfig) {
        DrawableTypeRequest<String> request = createRequest(context, url);

        GenericRequestBuilder genericRequestBuilder;
        if (!imageConfig.isSupportGif) {
            BitmapTypeRequest bitmapTypeRequest = request.asBitmap();
            switch (imageConfig.scaleType) {
                case ScaleType.CENTER_CROP:
                    bitmapTypeRequest.centerCrop();
                    break;

                case ScaleType.FIT_CENTER:
                    bitmapTypeRequest.fitCenter();
                    break;

                case ScaleType.TOP:
                    bitmapTypeRequest.transform(new TopTransformation(context));
                    break;
            }

            if (imageConfig.isCircle) {
                bitmapTypeRequest.transform(new CropCircleTransformation(context));
            }

            genericRequestBuilder  = bitmapTypeRequest;
        }else {
            switch (imageConfig.scaleType) {
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

            if (imageConfig.isCircle) {
                request.bitmapTransform(new CropCircleTransformation(context));
            }

            genericRequestBuilder = request;
        }


        if (imageConfig.width > 0 && imageConfig.height > 0) {
            genericRequestBuilder.override(imageConfig.width, imageConfig.height);
        }

        switch (imageConfig.priority) {
            case Priority.HIGH:
                genericRequestBuilder.priority(com.bumptech.glide.Priority.HIGH);
                break;

            case Priority.LOW:
                genericRequestBuilder.priority(com.bumptech.glide.Priority.LOW);
                break;

            default:
                genericRequestBuilder.priority(com.bumptech.glide.Priority.NORMAL);
                break;
        }

        switch (imageConfig.diskCacheStrategy) {
            case CacheConfig.All:
                genericRequestBuilder.diskCacheStrategy(DiskCacheStrategy.ALL);
                break;

            case CacheConfig.RESULT:
                genericRequestBuilder.diskCacheStrategy(DiskCacheStrategy.RESULT);
                break;

            case CacheConfig.SOURCE:
                genericRequestBuilder.diskCacheStrategy(DiskCacheStrategy.SOURCE);
                break;
        }

        genericRequestBuilder.skipMemoryCache(!imageConfig.isCacheMemory);

        genericRequestBuilder.placeholder(resourceId);

        genericRequestBuilder.into(view);

    }

    public static void load(Context context, ItemImageView imgView, String url, int imagePlaceholder) {
        load(context, imgView, url, imagePlaceholder, new ImageConfigBuild().build());
    }

    private static DrawableTypeRequest<String> createRequest(Context context, String url) {
        return Glide.with(context).
                load(url);
    }


    /**
     * @param context
     * @param url
     * @param width
     * @param height
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * 这个方法需要在子线程中调用
     */
    public static File getFile(Context context, String url, int width, int height) throws ExecutionException, InterruptedException {
        return createRequest(context, url).downloadOnly(width, height).get();
    }

}

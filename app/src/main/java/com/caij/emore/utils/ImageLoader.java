package com.caij.emore.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.caij.emore.utils.glide.CropCircleTransformation;
import com.caij.emore.utils.glide.TopTransformation;

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
        private Transformation transformation;

        private ImageConfig(ImageConfigBuild build) {
            scaleType = build.scaleType;
            height = build.height;
            width = build.width;
            isCircle = build.isCircle;
            priority = build.priority;
            diskCacheStrategy = build.diskCacheStrategy;
            isCacheMemory = build.isCacheMemory;
            isSupportGif = build.isSupportGif;
            transformation = build.transformation;
        }
    }

    public static class ImageConfigBuild {
        private int scaleType = ScaleType.FIT_CENTER;
        private int height = -1;
        private int width = -1;
        private boolean isCircle = false;
        private int priority = Priority.NORMAL;
        private int diskCacheStrategy = CacheConfig.SOURCE;
        private boolean isCacheMemory = true;
        private boolean isSupportGif = false;
        private Transformation transformation;

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

        public ImageConfigBuild setTransformation(Transformation transformation) {
            this.transformation = transformation;
            return this;
        }

        public ImageConfig build() {
            return new ImageConfig(this);
        }
    }

    public static interface ImageLoadListener {
        public void onFail(Exception e);
        public void onSuccess();
    }

    public static void load(Context context, ImageView view, String url, int resourceId,  ImageConfig imageConfig) {
        DrawableTypeRequest request = createRequest(context, url);
        loadImage(context, view, request, resourceId, imageConfig);
    }

    public static void load(Context context, ImageView view, File file, int resourceId,  ImageConfig imageConfig) {
        DrawableTypeRequest request = createRequest(context, file);
        loadImage(context, view, request, resourceId, imageConfig);
    }

    private static void loadImage(Context context, ImageView view, DrawableTypeRequest request, int resourceId, ImageConfig imageConfig) {
        if (!imageConfig.isSupportGif) {
            BitmapTypeRequest bitmapTypeRequest = request.asBitmap();
            if (imageConfig.transformation == null) {
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
            }else {
                bitmapTypeRequest.transform(imageConfig.transformation);
            }

            if (imageConfig.isCircle) {
                bitmapTypeRequest.transform(new CropCircleTransformation(context));
            }

            if (imageConfig.width > 0 && imageConfig.height > 0) {
                bitmapTypeRequest.override(imageConfig.width, imageConfig.height);
            }

            switch (imageConfig.priority) {
                case Priority.HIGH:
                    bitmapTypeRequest.priority(com.bumptech.glide.Priority.HIGH);
                    break;

                case Priority.LOW:
                    bitmapTypeRequest.priority(com.bumptech.glide.Priority.LOW);
                    break;

                default:
                    bitmapTypeRequest.priority(com.bumptech.glide.Priority.NORMAL);
                    break;
            }

            switch (imageConfig.diskCacheStrategy) {
                case CacheConfig.All:
                    bitmapTypeRequest.diskCacheStrategy(DiskCacheStrategy.ALL);
                    break;

                case CacheConfig.RESULT:
                    bitmapTypeRequest.diskCacheStrategy(DiskCacheStrategy.RESULT);
                    break;

                case CacheConfig.SOURCE:
                    bitmapTypeRequest.diskCacheStrategy(DiskCacheStrategy.SOURCE);
                    break;
            }

            bitmapTypeRequest.skipMemoryCache(!imageConfig.isCacheMemory);

            if (resourceId > 0) {
                bitmapTypeRequest.placeholder(resourceId);
            }

            bitmapTypeRequest.into(view);
        }else {
            if (imageConfig.transformation == null) {
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
            }else {
                request.transform(imageConfig.transformation);
            }

            if (imageConfig.isCircle) {
                request.bitmapTransform(new CropCircleTransformation(context));
            }

            if (imageConfig.width > 0 && imageConfig.height > 0) {
                request.override(imageConfig.width, imageConfig.height);
            }

            switch (imageConfig.priority) {
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

            switch (imageConfig.diskCacheStrategy) {
                case CacheConfig.All:
                    request.diskCacheStrategy(DiskCacheStrategy.ALL);
                    break;

                case CacheConfig.RESULT:
                    request.diskCacheStrategy(DiskCacheStrategy.RESULT);
                    break;

                case CacheConfig.SOURCE:
                    request.diskCacheStrategy(DiskCacheStrategy.SOURCE);
                    break;
            }

            request.skipMemoryCache(!imageConfig.isCacheMemory);

            if (resourceId > 0) {
                request.placeholder(resourceId);
            }

            request.into(view);
        }

    }

    public static void load(Context context, ImageView imgView, String url, int imagePlaceholder) {
        load(context, imgView, url, imagePlaceholder, new ImageConfigBuild().setScaleType(ScaleType.CENTER_CROP).build());
    }


    private static DrawableTypeRequest createRequest(Context context, String url) {
        return Glide.with(context).
                load(url);
    }

    private static DrawableTypeRequest createRequest(Context context, File file) {
        return Glide.with(context).
                load(file);
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
        return (File) createRequest(context, url).downloadOnly(width, height).get();
    }

}

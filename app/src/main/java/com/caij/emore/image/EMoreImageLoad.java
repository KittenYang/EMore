package com.caij.emore.image;

import android.content.Context;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ca1j on 2016/12/29.
 */

public class EMoreImageLoad implements ImageLoad {

    /**
     * 图片加载配置缓存  防止没加载一张图片都会创建对象
     */
    public static class ImageConfigPool {
        public static final ImageConfig defaultCircle = new ImageConfigBuild()
                .setScaleType(ScaleType.CIRCLE)
                .build();
        public static final ImageConfig defaultFitCenter = new ImageConfigBuild()
                .setScaleType(ScaleType.FIT_CENTER)
                .build();
        public static final ImageConfig defaultCenterCrop = new ImageConfigBuild()
                .setScaleType(ScaleType.CENTER_CROP)
                .build();
    }

    private ImageLoad mImageLoad;

    public EMoreImageLoad(ImageLoad imageLoad) {
        mImageLoad = imageLoad;
    }

    @Override
    public void loadImage(Context context, ImageView view, String url, int resourceId, ImageConfig imageConfig) {
        mImageLoad.loadImage(context, view, url, resourceId, imageConfig);
    }

    @Override
    public File getFile(Context context, String url, int width, int height) throws ExecutionException, InterruptedException {
        return mImageLoad.getFile(context, url, width, height);
    }

    @Override
    public void onTrimMemory(Context context, int level) {
        mImageLoad.onTrimMemory(context, level);
    }

    @Override
    public void onLowMemory(Context context) {
        mImageLoad.onLowMemory(context);
    }

    public void loadImageCircle(Context context, ImageView view, String url, int resourceId) {
        loadImage(context, view, url, resourceId, ImageConfigPool.defaultCircle);
    }

    public void loadImageCenterCrop(Context context, ImageView view, String url, int resourceId) {
        loadImage(context, view, url, resourceId, ImageConfigPool.defaultCenterCrop);
    }

    public void loadImageFitCenter(Context context, ImageView view, String url, int resourceId) {
        loadImage(context, view, url, resourceId, ImageConfigPool.defaultFitCenter);
    }
}

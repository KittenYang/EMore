package com.caij.emore.image;

import android.content.Context;
import android.widget.ImageView;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ca1j on 2016/12/29.
 */

public interface ImageLoad {

    public static interface ScaleType {
        int CENTER_CROP = 1;
        int FIT_CENTER = 2;
        int TOP = 3;  //优先显示上面部分
        int CIRCLE = 4; //圆
        int MASK = 5;
    }

    public static interface Priority {
        int HIGH = 3;
        int NORMAL = 2;
        int LOW = 1;
    }

    public static interface DiskCacheConfig {
        int RESULT = 1;
        int SOURCE = 2;
        int All = 3;
    }

    public static class ImageConfig {

        private int scaleType;
        private int height;
        private int width;
        private int priority;
        private int diskCacheStrategy;
        private boolean isCacheMemory;
        private boolean isAnimate;
        private boolean isSupportGif;
        private int maskResourceId;

        private ImageConfig(ImageConfigBuild build) {
            scaleType = build.scaleType;
            height = build.height;
            width = build.width;
            priority = build.priority;
            diskCacheStrategy = build.diskCacheStrategy;
            isCacheMemory = build.isCacheMemory;
            isSupportGif = build.isSupportGif;
            isAnimate = build.isAnimate;
            maskResourceId = build.maskResourceId;
        }

        public int getScaleType() {
            return scaleType;
        }

        public void setScaleType(int scaleType) {
            this.scaleType = scaleType;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getPriority() {
            return priority;
        }

        public int getDiskCacheStrategy() {
            return diskCacheStrategy;
        }

        public boolean isCacheMemory() {
            return isCacheMemory;
        }

        public boolean isAnimate() {
            return isAnimate;
        }


        public boolean isSupportGif() {
            return isSupportGif;
        }

        public int getMaskResourceId() {
            return maskResourceId;
        }

    }

    public static class ImageConfigBuild {
        private int scaleType = ScaleType.FIT_CENTER;
        private int height = -1;
        private int width = -1;
        private int priority = Priority.NORMAL;
        private int diskCacheStrategy = DiskCacheConfig.SOURCE;
        private boolean isCacheMemory = true;
        private boolean isSupportGif = false;
        private boolean isAnimate = true;
        private int maskResourceId = -1;

        public ImageConfigBuild(){

        }

        public static ImageConfigBuild clone(ImageConfig imageConfig) {
            ImageConfigBuild imageConfigBuild = new ImageConfigBuild();
            imageConfigBuild.setCacheMemory(imageConfig.isCacheMemory);
            imageConfigBuild.setWidthAndHeight(imageConfig.width, imageConfig.height);
            imageConfigBuild.setDiskCacheStrategy(imageConfig.diskCacheStrategy);
            imageConfigBuild.setPriority(imageConfig.priority);
            imageConfigBuild.setScaleType(imageConfig.scaleType);
            imageConfigBuild.setSupportGif(imageConfig.isSupportGif);
            imageConfigBuild.setMaskResourceId(imageConfig.maskResourceId);
            imageConfigBuild.setAnimate(imageConfig.isAnimate);
            return imageConfigBuild;
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

        public ImageConfigBuild setAnimate(boolean animate) {
            isAnimate = animate;
            return this;
        }

        public ImageConfigBuild setMaskResourceId(int maskResourceId) {
            this.maskResourceId = maskResourceId;
            return this;
        }

        public ImageConfig build() {
            return new ImageConfig(this);
        }
    }

    public void loadImage(Context context, ImageView view, String url, int resourceId, ImageConfig imageConfig);

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
    public File getFile(Context context, String url, int width, int height) throws ExecutionException, InterruptedException;

    public void onTrimMemory(Context context, int level);

    public  void onLowMemory(Context context);
}

package com.caij.emore.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapperTransformation;
import com.caij.emore.image.glide.CropCircleTransformation;
import com.caij.emore.image.glide.MaskTransformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ca1j on 2016/12/29.
 */

public class GlideImageLoad implements ImageLoad {

    private MultiTransformation<GifBitmapWrapper> mCircleDrawableTransformation;
    private MultiTransformation<Bitmap> mCircleBitmapTransformation;
    private CenterCrop bitmapCenterCrop;
    private FitCenter bitmapFitCenter;
    private GifBitmapWrapperTransformation drawableCenterCrop;
    private GifBitmapWrapperTransformation drawableFitCenter;

    private SparseArray<Transformation<GifBitmapWrapper>> mMaskDrawableTransformationSparseArray;
    private SparseArray<Transformation<Bitmap>> mMaskBitmapTransformationSparseArray;

    public GlideImageLoad() {
        mMaskDrawableTransformationSparseArray = new SparseArray<>();
        mMaskBitmapTransformationSparseArray = new SparseArray<>();
    }

    private Transformation<GifBitmapWrapper> getCircleDrawableTransformation(Context context) {
        if (mCircleDrawableTransformation == null) {
            List<GifBitmapWrapperTransformation> gifBitmapWrapperTransformations = new ArrayList<>(2);
            gifBitmapWrapperTransformations.add(new GifBitmapWrapperTransformation(Glide.get(context).getBitmapPool(), centerCrop(context)));
            gifBitmapWrapperTransformations.add(new GifBitmapWrapperTransformation(Glide.get(context).getBitmapPool(), new CropCircleTransformation(context)));
            mCircleDrawableTransformation = new MultiTransformation<GifBitmapWrapper>(gifBitmapWrapperTransformations);
        }

        return mCircleDrawableTransformation;
    }

    private Transformation<Bitmap> getCircleBitmapTransformation(Context context) {
        if (mCircleBitmapTransformation == null) {
            List<Transformation<Bitmap>> transformations = new ArrayList<>(2);
            transformations.add(centerCrop(context));
            transformations.add(new CropCircleTransformation(context));
            mCircleBitmapTransformation = new MultiTransformation<Bitmap>(transformations);
        }
        return mCircleBitmapTransformation;
    }

    private Transformation<GifBitmapWrapper> getMaskDrawableTransformation(Context context, int resourceId) {
        Transformation<GifBitmapWrapper> transformation = mMaskDrawableTransformationSparseArray.get(resourceId);
        if (transformation == null) {
            List<GifBitmapWrapperTransformation> gifBitmapWrapperTransformations = new ArrayList<>(2);
            gifBitmapWrapperTransformations.add(new GifBitmapWrapperTransformation(Glide.get(context).getBitmapPool(), centerCrop(context)));
            gifBitmapWrapperTransformations.add(new GifBitmapWrapperTransformation(Glide.get(context).getBitmapPool(), mask(context, resourceId)));
            transformation = new MultiTransformation<GifBitmapWrapper>(gifBitmapWrapperTransformations);
            mMaskDrawableTransformationSparseArray.put(resourceId, transformation);
        }
        return transformation;
    }

    private Transformation<Bitmap> getMaskBitmapTransformation(Context context, int resourceId) {
        Transformation<Bitmap> transformation = mMaskBitmapTransformationSparseArray.get(resourceId);
        if (transformation == null) {
            List<Transformation<Bitmap>> transformations = new ArrayList<>(2);
            transformations.add(centerCrop(context));
            transformations.add(mask(context, resourceId));
            transformation = new MultiTransformation<Bitmap>(transformations);
            mMaskBitmapTransformationSparseArray.put(resourceId, transformation);
        }
        return transformation;
    }

    private MaskTransformation mask(Context context, int resourceId) {
        return new MaskTransformation(context, resourceId);
    }

    private CenterCrop centerCrop(Context context) {
        if (bitmapCenterCrop == null) {
            bitmapCenterCrop = new CenterCrop(context);
        }
        return bitmapCenterCrop;
    }

    private GifBitmapWrapperTransformation drawableCenterCrop(Context context) {
        if (drawableCenterCrop == null) {
            drawableCenterCrop = new GifBitmapWrapperTransformation(Glide.get(context).getBitmapPool(), centerCrop(context));
        }
        return drawableCenterCrop;
    }

    private FitCenter fitCenter(Context context) {
        if (bitmapFitCenter == null) {
            bitmapFitCenter = new FitCenter(context);
        }
        return bitmapFitCenter;
    }

    private GifBitmapWrapperTransformation drawableFitCenter(Context context) {
        if (drawableFitCenter == null) {
            drawableFitCenter = new GifBitmapWrapperTransformation(Glide.get(context).getBitmapPool(), fitCenter(context));
        }
        return drawableFitCenter;
    }

    private DiskCacheStrategy getDiskCacheType(int cacheType) {
        if(cacheType == DiskCacheConfig.All) {
            return DiskCacheStrategy.ALL;
        }else if (cacheType == DiskCacheConfig.SOURCE) {
            return DiskCacheStrategy.SOURCE;
        }else if (cacheType == DiskCacheConfig.RESULT) {
            return DiskCacheStrategy.RESULT;
        }
        return DiskCacheStrategy.ALL;
    }

    private com.bumptech.glide.Priority getPriority(int priority) {
        if (priority == Priority.HIGH) {
            return com.bumptech.glide.Priority.HIGH;
        }else if (priority == Priority.NORMAL) {
            return com.bumptech.glide.Priority.NORMAL;
        }else if (priority == Priority.LOW) {
            return com.bumptech.glide.Priority.LOW;
        }
        return com.bumptech.glide.Priority.NORMAL;
    }

    @Override
    public File getFile(Context context, String url, int width, int height) throws ExecutionException, InterruptedException {
        return load(context, url).downloadOnly(width, height).get();
    }

    @Override
    public void onTrimMemory(Context context, int level) {
        Glide.with(context).onTrimMemory(level);
    }

    @Override
    public void onLowMemory(Context context) {
        Glide.with(context).onLowMemory();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadImage(Context context, ImageView view, String url, int resourceId, ImageConfig imageConfig) {
        GenericRequestBuilder requestBuilder;
        if (imageConfig.isSupportGif()) {
            requestBuilder = load(context, url).transform(getDrawableTransformation(context, imageConfig));
        }else {
            requestBuilder = load(context, url).asBitmap().transform(getBitmapTransformation(context, imageConfig));
        }

        if (!imageConfig.isAnimate()) {
            requestBuilder.dontAnimate();
        }

        if (imageConfig.getWidth() > 0 && imageConfig.getHeight() > 0) {
            requestBuilder.override(imageConfig.getWidth(), imageConfig.getHeight());
        }

        requestBuilder.placeholder(resourceId)
                .diskCacheStrategy(getDiskCacheType(imageConfig.getDiskCacheStrategy()))
                .priority(getPriority(imageConfig.getPriority()))
                .skipMemoryCache(!imageConfig.isCacheMemory())
                .into(view);
    }

    private Transformation<Bitmap> getBitmapTransformation(Context context, ImageConfig imageConfig) {
        if (imageConfig.getScaleType() == ScaleType.CIRCLE) {
            return getCircleBitmapTransformation(context);
        }else if (imageConfig.getScaleType() == ScaleType.FIT_CENTER) {
            return fitCenter(context);
        }else if (imageConfig.getScaleType() == ScaleType.CENTER_CROP) {
            return centerCrop(context);
        }else if (imageConfig.getScaleType() == ScaleType.MASK) {
            return getMaskBitmapTransformation(context, imageConfig.getMaskResourceId());
        }
        return centerCrop(context);
    }

    private Transformation<GifBitmapWrapper> getDrawableTransformation(Context context, ImageConfig imageConfig) {
        if (imageConfig.getScaleType() == ScaleType.CIRCLE) {
            return getCircleDrawableTransformation(context);
        }else if (imageConfig.getScaleType() == ScaleType.FIT_CENTER) {
            return drawableFitCenter(context);
        }else if (imageConfig.getScaleType() == ScaleType.CENTER_CROP) {
            return drawableCenterCrop(context);
        }else if (imageConfig.getScaleType() == ScaleType.MASK) {
            return getMaskDrawableTransformation(context, imageConfig.getMaskResourceId());
        }
        return drawableCenterCrop(context);
    }

    private DrawableTypeRequest<String> load(Context context, String url) {
        return  Glide.with(context).load(url);
    }

}

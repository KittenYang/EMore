package com.caij.emore.present.imp;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.ui.view.ImagePreView;
import com.caij.emore.utils.ExecutorServicePool;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import java.io.File;
import java.io.IOException;

import rx.Subscription;

/**
 * Created by Caij on 2016/7/27.
 */
public class LocalImagePrePresentImp extends AbsBasePresent implements ImagePrePresent {

    private ImagePreView mImagePreView;

    private ImageInfo mImageInfo;

    public LocalImagePrePresentImp(ImageInfo imageInfo, ImagePreView imagePreView) {
        mImagePreView = imagePreView;
        mImageInfo = imageInfo;
    }

    @Override
    public void loadImage() {
        showImage(mImageInfo.getUrl());
    }

    @Override
    public void onViewLongClick() {

    }

    private void showImage(final String localFilePath) {
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<Zip>() {
                @Override
                public Zip getData() throws Exception {
                    File file = new File(localFilePath);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(localFilePath, options);
                    Zip imageInfo = new Zip();
                    try {
                        imageInfo.imageType = ImageUtil.getImageType(file);
                    } catch (IOException e) {}
                    imageInfo.width = options.outWidth;
                    imageInfo.height = options.outHeight;
                    return imageInfo;
                }
            })
            .compose(SchedulerTransformer.<Zip>create())
            .subscribe(new SubscriberAdapter<Zip>() {
                @Override
                public void onNext(Zip zip) {
                    if (zip != null) {
                        if (ImageUtil.ImageType.GIF.equals(zip.imageType)) {
                            mImagePreView.showGifImage("file://" + localFilePath);
                        }else {
                            if (ImagePrePresentImp.isLongHImage(zip.width, zip.height)) {
                                mImagePreView.showLongHImage(localFilePath);
                            }else {
                                mImagePreView.showLocalImage(localFilePath);
                            }
                        }
                    }else {
                        mImagePreView.onDefaultLoadError();
                    }
                }
            });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {

    }

    private static class Zip {
        String imageType = ImageUtil.ImageType.JPEG;
        int width;
        int height;
    }

}

package com.caij.emore.present.imp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bumptech.glide.request.target.Target;
import com.caij.emore.R;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.ui.view.ImagePreView;
import com.caij.emore.utils.CacheUtils;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.FileUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Caij on 2016/7/27.
 */
public class LocalImagePrePresentImp implements ImagePrePresent {

    private Context mContent;
    private ImagePreView mImagePreView;
    private AsyncTask mImageLoadAsyncTask;

    private ImageInfo mImageInfo;

    public LocalImagePrePresentImp(Context context, ImageInfo imageInfo, ImagePreView imagePreView) {
        mContent = context;
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
        ExecutorServiceUtil.executeAsyncTask(mImageLoadAsyncTask = new AsyncTask<Object, Object, Zip>() {
            @Override
            protected Zip doInBackground(Object... params) {
                File file = new File(localFilePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(localFilePath, options);
                Zip imageInfo = new Zip();
                try {
                    imageInfo.imageType = ImageUtil.getImageType(file);
                } catch (IOException e) {
                }
                imageInfo.width = options.outWidth;
                imageInfo.height = options.outHeight;
                return imageInfo;
            }

            @Override
            protected void onPostExecute(Zip imageInfo) {
                super.onPostExecute(imageInfo);
                if (imageInfo != null) {
                    if (imageInfo.imageType == ImageUtil.ImageType.GIF) {
                        mImagePreView.showGifImage("file://" + localFilePath);
                    }else if (ImageUtil.isLongImage(imageInfo.width, imageInfo.height)) {
                        mImagePreView.showBigImage(localFilePath);
                    } else {
                        mImagePreView.showLocalImage(localFilePath);
                    }
                }else {
                    mImagePreView.onDefaultLoadError();
                }
            }
        });
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mContent = null;
        if (mImageLoadAsyncTask != null) {
            mImageLoadAsyncTask.cancel(true);
        }
    }

    private static class Zip {
        ImageUtil.ImageType imageType = ImageUtil.ImageType.JPEG;
        int width;
        int height;
    }

}

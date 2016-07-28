package com.caij.emore.present.imp;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.bumptech.glide.request.target.Target;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.present.view.ImagePreView;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.ImageUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by Caij on 2016/7/27.
 */
public class ImagePrePresentImp implements ImagePrePresent {

    public Context mContent;
    ImagePreView mImagePreView;
    private String mImageUrl;
    private AsyncTask mImageLoadAsyncTask;

    public ImagePrePresentImp(Context context, String url, ImagePreView imagePreView) {
        mContent = context;
        mImagePreView = imagePreView;
        mImageUrl = url;
    }

    @Override
    public void loadImage() {
        if (mImageUrl.startsWith("/")) {
            showImage(mImageUrl);
        }else {
            if (mImageUrl.contains("gif")) {
                mImagePreView.showWebImage(mImageUrl);
            }else {
                ExecutorServiceUtil.executeAsyncTask(mImageLoadAsyncTask = new AsyncTask<Object, Object, File>() {
                    @Override
                    protected File doInBackground(Object... params) {
                        File file = null;
                        try {
                            file = ImageLoader.getFile(mContent, mImageUrl, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return file;
                    }

                    @Override
                    protected void onPostExecute(File file) {
                        super.onPostExecute(file);
                        if (file == null) {
                            mImagePreView.onDefaultLoadError();
                        }else {
                            showImage(file.getAbsolutePath());
                        }
                    }
                });
            }
        }
    }

    private void showImage(final String localFilePath) {
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, BitmapFactory.Options>() {
            @Override
            protected BitmapFactory.Options doInBackground(Object... params) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(localFilePath, options);
                return options;
            }

            @Override
            protected void onPostExecute(BitmapFactory.Options options) {
                super.onPostExecute(options);
                if (options != null) {
                    if (ImageUtil.isBigImage(options.outWidth, options.outHeight)) {
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
}

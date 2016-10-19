package com.caij.emore.present.imp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.caij.emore.R;
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
public class ImagePrePresentImp implements ImagePrePresent {

    private Context mContent;
    private ImagePreView mImagePreView;
    private String mImageUrl;
    private String mHdImageUrl;
    private AsyncTask mImageLoadAsyncTask;
    private String mShowFilePath;
    private ImageUtil.ImageType mImageType = ImageUtil.ImageType.JPEG;

    private String mShowImageUrl;

    public ImagePrePresentImp(Context context, String url, String hdUrl, ImagePreView imagePreView) {
        mContent = context;
        mImagePreView = imagePreView;
        mImageUrl = url;
        mHdImageUrl = hdUrl;
    }

    @Override
    public void loadImage() {
        loadImage(mImageUrl);
    }

    private void loadImage(final String url) {
        mShowImageUrl = url;

        if (mImageUrl.startsWith("/")) {  //本地文件
            showImage(mImageUrl);
        }else {
            mImagePreView.showProgress(true);
            ExecutorServiceUtil.executeAsyncTask(mImageLoadAsyncTask = new AsyncTask<Object, Object, File>() {
                @Override
                protected File doInBackground(Object... params) {

                    File file = null;
                    try {
                        file = ImageLoader.getFile(mContent, url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    return file;
                }

                @Override
                protected void onPostExecute(File file) {
                    super.onPostExecute(file);
                    mImagePreView.showProgress(false);
                    if (file == null) {
                        mImagePreView.onDefaultLoadError();
                    }else {
                        showImage(file.getAbsolutePath());
                    }
                }
            });
        }
    }

    @Override
    public void onViewLongClick() {
        if (mShowFilePath == null) return;
        String[] items;
        if (!TextUtils.isEmpty(mHdImageUrl) && !mShowImageUrl.equals(mHdImageUrl)) {
            items = new String[]{mContent.getString(R.string.save_image), mContent.getString(R.string.preview_big_image)};
            mImagePreView.showSelectDialog(items, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        saveImage(new File(mShowFilePath));
                    }else {
                        loadImage(mHdImageUrl);
                    }
                }
            });
        }else if (mImageUrl.startsWith("http")){
            items = new String[]{mContent.getString(R.string.save_image)};
            mImagePreView.showSelectDialog(items, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveImage(new File(mShowFilePath));
                }
            });
        }
    }

    private void saveImage(final File source) {
        final File target = new File(CacheUtils.getImageSaveDir(), ImageUtil.createImageName(mImageType.getValue()));
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                boolean isSuccess;
                try {
                    FileUtil.copy(source, target);
                    isSuccess = true;
                } catch (Exception e) {
                    isSuccess = false;
                }
                return isSuccess;
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
               if (isSuccess) {
                   mImagePreView.showHint("图片已保存:" + target.getAbsolutePath());
                   SystemUtil.notifyScanFile(mContent, target.getAbsolutePath());
               }else {
                   mImagePreView.showHint(R.string.save_image_error);
               }
            }
        });

    }

    private void showImage(final String localFilePath) {
        mShowFilePath = localFilePath;
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, ImageInfo>() {
            @Override
            protected ImageInfo doInBackground(Object... params) {
                File file = new File(localFilePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(localFilePath, options);
                ImageInfo imageInfo = new ImageInfo();
                try {
                    imageInfo.imageType = ImageUtil.getImageType(file);
                } catch (IOException e) {
                }
                imageInfo.width = options.outWidth;
                imageInfo.height = options.outHeight;
                return imageInfo;
            }

            @Override
            protected void onPostExecute(ImageInfo imageInfo) {
                super.onPostExecute(imageInfo);
                if (imageInfo != null) {
                    mImageType = imageInfo.imageType;
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

    private static class ImageInfo {
        ImageUtil.ImageType imageType = ImageUtil.ImageType.JPEG;
        int width;
        int height;
    }
}

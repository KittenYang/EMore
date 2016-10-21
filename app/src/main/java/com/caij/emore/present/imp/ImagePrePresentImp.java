package com.caij.emore.present.imp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.caij.emore.EMApplication;
import com.caij.emore.R;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.ui.view.ImagePreView;
import com.caij.emore.utils.CacheUtils;
import com.caij.emore.utils.DownLoadUtil;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.FileUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.MD5Util;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import java.io.File;

import rx.Subscription;

/**
 * Created by Caij on 2016/7/27.
 */
public class ImagePrePresentImp  extends AbsBasePresent implements ImagePrePresent {

    private Context mContent;
    private ImagePreView mImagePreView;
    private ImageInfo mImageInfo;
    private ImageInfo mHdImageInfo;

    private String mShowImagePath;
    private ImageInfo mShowImageInfo;

    public ImagePrePresentImp(Context context, ImageInfo imageInfo, ImageInfo hdImageInfo, ImagePreView imagePreView) {
        mContent = context;
        mImagePreView = imagePreView;
        mImageInfo = imageInfo;
        mHdImageInfo = hdImageInfo;
    }

    @Override
    public void loadImage() {
        if (mHdImageInfo != null) {
            String fileName = MD5Util.string2MD5(mHdImageInfo.getUrl());
            File file = new File(CacheUtils.getCacheHdImageDir(EMApplication.getInstance()), fileName);
            if (file.exists()) {
                mShowImageInfo = mHdImageInfo;
                showFile(file, mHdImageInfo);
            }else {
                loadImage(mImageInfo);
            }
        }else {
            loadImage(mImageInfo);
        }
    }

    private void loadImage(ImageInfo imageInfo) {
        getImageFile(imageInfo);
    }

    private void getImageFile(final ImageInfo imageInfo) {
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<File>() {
                @Override
                public File getData() throws Exception {
                    return ImageLoader.getFile(mContent, imageInfo.getUrl(), Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                }
            }).compose(SchedulerTransformer.<File>create())
            .subscribe(new SubscriberAdapter<File>() {
                @Override
                public void onNext(File file) {
                    mShowImageInfo = imageInfo;
                    onGetFileSuccess(file, imageInfo);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    onGetFileError();
                }
            });

        addSubscription(subscription);
    }

    private void onGetFileSuccess(File file, ImageInfo imageInfo) {
        showFile(file, imageInfo);

        if ((SystemUtil.isNetworkFast(EMApplication.getInstance()) && mHdImageInfo != null)
                || (mHdImageInfo != null && mHdImageInfo.getImageType() == ImageUtil.ImageType.GIF)) {
            loadHdImage();
        }
    }

    private void showFile(File file, ImageInfo imageInfo) {
        mShowImagePath = file.getAbsolutePath();

        if (imageInfo.getImageType() == ImageUtil.ImageType.GIF) {
            mImagePreView.showGifImage(Uri.fromFile(file).getPath());
        }else if (ImageUtil.isLongImage(imageInfo.getWidth(), imageInfo.getHeight())) {
            mImagePreView.showBigImage(Uri.fromFile(file).getPath());
        }else {
            mImagePreView.showLocalImage(Uri.fromFile(file).getPath());
        }
    }

    private void loadHdImage() {
        String fileName = MD5Util.string2MD5(mHdImageInfo.getUrl());
        File file = new File(CacheUtils.getCacheHdImageDir(EMApplication.getInstance()), fileName);
        mImagePreView.showProgress(true);
        DownLoadUtil.down(mHdImageInfo.getUrl(), file.getAbsolutePath(), new DownLoadUtil.Callback() {
            @Override
            public void onSuccess(File file) {
                mShowImageInfo = mHdImageInfo;
                if (mImagePreView != null) {
                    showFile(file, mHdImageInfo);
                    mImagePreView.showProgress(false);
                }
            }

            @Override
            public void onProgress(long total, long progress) {
                LogUtil.d(ImagePrePresentImp.this, "total %s progress %s", total, progress);
                if (mImagePreView != null) {
                    mImagePreView.showProgress(total, progress);
                }
            }

            @Override
            public void onError(Exception e) {
                if (mImagePreView != null) {
                    mImagePreView.onDefaultLoadError();
                    mImagePreView.showProgress(false);
                }
            }
        });
    }

    private void onGetFileError() {
        mImagePreView.onDefaultLoadError();
    }

    @Override
    public void onViewLongClick() {
        if (mShowImagePath == null) return;
        String[] items;
        if (mHdImageInfo != null && mHdImageInfo != mShowImageInfo) {
            items = new String[]{mContent.getString(R.string.save_image), mContent.getString(R.string.preview_big_image)};
            mImagePreView.showSelectDialog(items, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        saveImage(new File(mShowImagePath));
                    }else {
                        loadHdImage();
                    }
                }
            });
        }else {
            items = new String[]{mContent.getString(R.string.save_image)};
            mImagePreView.showSelectDialog(items, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveImage(new File(mShowImagePath));
                }
            });
        }
    }

    private void saveImage(final File source) {
        final File target = new File(CacheUtils.getImageSaveDir(), ImageUtil.createImageName(mImageInfo.getImageType().getValue()));
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<Object>() {
                @Override
                public Object getData() throws Exception {
                    FileUtil.copy(source, target);
                    return null;
                }
            }).compose(SchedulerTransformer.create())
            .subscribe(new SubscriberAdapter<Object>() {
                @Override
                public void onNext(Object o) {
                    mImagePreView.showHint("图片已保存:" + target.getAbsolutePath());
                    SystemUtil.notifyScanFile(mContent, target.getAbsolutePath());
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    mImagePreView.showHint(R.string.save_image_error);
                }
            });
        addSubscription(subscription);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImagePreView = null;
    }
}

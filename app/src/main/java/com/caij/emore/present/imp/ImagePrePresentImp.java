package com.caij.emore.present.imp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.bumptech.glide.request.target.Target;
import com.caij.emore.EMApplication;
import com.caij.emore.R;
import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.bean.ImageInfo;
import com.caij.emore.present.ImagePrePresent;
import com.caij.emore.ui.view.ImagePreView;
import com.caij.emore.utils.CacheUtils;
import com.caij.emore.utils.DownLoadUtil;
import com.caij.emore.utils.FileUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.MD5Util;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;

import java.io.File;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

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
    public void onCreate() {

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
        }else if (isBigImage(imageInfo.getWidth(), imageInfo.getHeight())) {
            if (isLongHImage(imageInfo.getWidth(), imageInfo.getHeight())) {
                mImagePreView.showLongHImage(Uri.fromFile(file).getPath());
            }else {
                mImagePreView.showLocalImage(Uri.fromFile(file).getPath());
            }
        }else {
            showLocalFile(file, imageInfo);
        }
    }

    public static boolean isBigImage(int width, int height) {
        return width * height > 2048 * 2048 || width > 6000 || height > 6000;
    }

    public static boolean isLongHImage(int width, int height) {
        return height * 1f / width >= 2.5f;
    }

    private void showLocalFile(final File file, final ImageInfo imageInfo) {
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<Bitmap>() {
                @Override
                public Bitmap getData() throws Exception {
                    return BitmapFactory.decodeFile(file.getAbsolutePath());
                }
            })
            .compose(SchedulerTransformer.<Bitmap>create())
            .subscribe(new SubscriberAdapter<Bitmap>() {
                @Override
                public void onNext(Bitmap bitmap) {
                    if (isLongHImage(imageInfo.getWidth(), imageInfo.getHeight())) {
                        mImagePreView.showLongHImage(bitmap);
                    }else {
                        mImagePreView.showLocalImage(bitmap);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    mImagePreView.showLocalImage(Uri.fromFile(file).getPath());
                }
            });
        addSubscription(subscription);
    }

    private void loadHdImage() {
        String fileName = MD5Util.string2MD5(mHdImageInfo.getUrl());
        final File file = new File(CacheUtils.getCacheHdImageDir(EMApplication.getInstance()), fileName);
        mImagePreView.showProgress(true);
        Subscription subscription = DownLoadUtil.down(mHdImageInfo.getUrl(), file.getAbsolutePath())
            .compose(SchedulerTransformer.<DownLoadUtil.Progress>create())
            .doOnTerminate(new Action0() {
                @Override
                public void call() {
                    mImagePreView.showProgress(false);
                }
            })
            .subscribe(new SubscriberAdapter<DownLoadUtil.Progress>() {

                @Override
                public void onError(Throwable e) {
                    if (mImagePreView != null) {
                        mImagePreView.onDefaultLoadError();
                    }
                }

                @Override
                public void onNext(DownLoadUtil.Progress progress) {
                    LogUtil.d(ImagePrePresentImp.this.toString(), "total %s progress %s", progress.total, progress.read);
                    if (mImagePreView != null) {
                        mImagePreView.showProgress(progress.total, progress.read);
                    }
                }

                @Override
                public void onCompleted() {
                    super.onCompleted();
                    mShowImageInfo = mHdImageInfo;
                    if (mImagePreView != null) {
                        showFile(file, mHdImageInfo);
                    }
                }
            });
        addSubscription(subscription);
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
        Subscription subscription = RxUtil.createDataObservable(new RxUtil.Provider<File>() {
                @Override
                public File getData() throws Exception {
                    final File target = new File(CacheUtils.getImageSaveDir(), ImageUtil.createImageName(mImageInfo.getImageType().getValue()));
                    FileUtil.copy(source, target);
                    return target;
                }
            }).compose(SchedulerTransformer.<File>create())
            .subscribe(new SubscriberAdapter<File>() {
                @Override
                public void onNext(File o) {
                    mImagePreView.showHint("图片已保存:" + o.getAbsolutePath());
                    SystemUtil.notifyScanFile(mContent, o.getAbsolutePath());
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
    public void onDestroy() {
        super.onDestroy();
        mImagePreView = null;
    }

}

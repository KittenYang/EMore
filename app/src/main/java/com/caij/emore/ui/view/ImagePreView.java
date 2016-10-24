package com.caij.emore.ui.view;

import android.content.DialogInterface;
import android.graphics.Bitmap;

/**
 * Created by Caij on 2016/7/27.
 */
public interface ImagePreView extends BaseView {
    void showGifImage(String picUrl);

    void showLocalImage(String localFilePath);

    void showSelectDialog(String[] items, DialogInterface.OnClickListener onClickListener);

    void showProgress(boolean isShow);

    void showProgress(long total, long progress);

    void showLocalImage(Bitmap bitmap);

    void showLongHImage(String path);

    void showLongHImage(Bitmap bitmap);
}

package com.caij.emore.present.view;

import android.content.DialogInterface;

/**
 * Created by Caij on 2016/7/27.
 */
public interface ImagePreView extends BaseView {
    void showGifImage(String picUrl);

    void showBigImage(String localFilePath);

    void showLocalImage(String localFilePath);

    void showSelectDialog(String[] items, DialogInterface.OnClickListener onClickListener);

    void showProgress(boolean isShow);
}

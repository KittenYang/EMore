package com.caij.emore.present.view;

/**
 * Created by Caij on 2016/7/27.
 */
public interface ImagePreView extends BaseView {
    void showWebImage(String picUrl);

    void showBigImage(String localFilePath);

    void showLocalImage(String localFilePath);
}

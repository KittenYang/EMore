package com.caij.weiyo.present.view;

/**
 * Created by Caij on 2016/5/28.
 */
public interface BaseView {

    void onAuthenticationError();

    void onDefaultLoadError();

    void showHint(int stringId);

    void showDialogLoading(boolean isShow, int hintStringId);

    void showDialogLoading(boolean isShow);
}

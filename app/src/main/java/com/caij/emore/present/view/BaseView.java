package com.caij.emore.present.view;

/**
 * Created by Caij on 2016/5/28.
 */
public interface BaseView {

    void onAuthenticationError();

    void onDefaultLoadError();

    void showHint(int stringId);

    void showHint(String string);

    void showDialogLoading(boolean isShow, int hintStringId);

    void showDialogLoading(boolean isShow);
}

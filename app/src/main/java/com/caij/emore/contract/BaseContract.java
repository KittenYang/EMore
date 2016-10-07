package com.caij.emore.contract;

public interface BaseContract {

    interface BasePresenter<T> {

        void attachView(T view);

        void detachView();
    }

    interface BaseView {

        void onAuthenticationError();

        void onDefaultLoadError();

        void showHint(int stringId);

        void showHint(String string);

        void showDialogLoading(boolean isShow, int hintStringId);

        void showDialogLoading(boolean isShow);

    }
}

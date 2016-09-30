package com.caij.emore.utils.rxjava;

import android.text.TextUtils;

import com.caij.emore.R;
import com.caij.emore.ui.view.BaseView;
import com.caij.emore.utils.LogUtil;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Caij on 2016/5/28.
 */
public abstract class DefaultResponseSubscriber<T> extends Subscriber<T> {

    private BaseView mBaseView;

    public DefaultResponseSubscriber(BaseView baseView) {
        this.mBaseView = baseView;
    }

    public void onError(Throwable e) {
        if (e instanceof IOException) { // network is bad
            onNetworkError(e);
        }else if (e instanceof HttpException){
            HttpException httpException = (HttpException) e;
            int code  = httpException.code();
            if (code == 401) {//AuthFailureError
//                || code == 403 可能是没有权限
                mBaseView.onAuthenticationError();
                return;
            }else if (code == 403) {
                mBaseView.showHint(R.string.server_error_permissions);
            }else {
                mBaseView.showHint(R.string.server_error);
            }
        }else if (e instanceof RuntimeException && e.getCause() instanceof ErrorResponseException) {
            ErrorResponseException errorResponseException = (ErrorResponseException) e.getCause();
            if (TextUtils.isEmpty(errorResponseException.mResponse.getError())) {
                mBaseView.showHint(errorResponseException.mResponse.getErrmsg());
            }else {
                mBaseView.showHint(errorResponseException.mResponse.getError());
            }
        }else {
            mBaseView.showHint(R.string.net_request_error);
        }

        LogUtil.d(this, e.getMessage());

        onFail(e);
    }

    private void onNetworkError(Throwable e) {
        mBaseView.showHint(R.string.network_error);
    }

    protected abstract void onFail(Throwable e);

    @Override
    public void onCompleted() {

    }
}

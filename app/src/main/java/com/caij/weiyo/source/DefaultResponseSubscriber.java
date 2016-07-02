package com.caij.weiyo.source;

import com.caij.weiyo.present.view.BaseView;

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
            onFail(e);
        }else if (e instanceof HttpException){
            HttpException httpException = (HttpException) e;
            int code  = httpException.code();
            if (code == 401) {//AuthFailureError
//                || code == 403 可能是没有权限
                mBaseView.onAuthenticationError();
            }else {
                onFail(e);
            }
        }else {
            onFail(e);
        }
    }

    protected abstract void onFail(Throwable e);

}

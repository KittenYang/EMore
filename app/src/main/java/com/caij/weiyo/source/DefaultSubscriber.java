package com.caij.weiyo.source;

import com.caij.weiyo.present.view.BaseView;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Caij on 2016/5/28.
 */
public abstract class DefaultSubscriber<T> extends Subscriber<T> {

    private BaseView mBaseView;

    public DefaultSubscriber(BaseView baseView) {
        this.mBaseView = baseView;
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof IOException) { // network is bad
            onError(e);
        }else if (e instanceof HttpException){
            HttpException httpException = (HttpException) e;
            int code  = httpException.code();
            if (code == 401 || code == 403) {//AuthFailureError
                mBaseView.onAuthenticationError();
            }else {
                onError(e);
            }
        }
    }

    protected abstract void onError(Exception e);

}

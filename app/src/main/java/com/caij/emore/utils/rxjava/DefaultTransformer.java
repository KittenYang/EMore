package com.caij.emore.utils.rxjava;

import com.caij.emore.bean.response.Response;

import rx.Observable;

/**
 * Created by Caij on 2016/8/3.
 */
public class DefaultTransformer<T extends Response>
        implements Observable.Transformer<T, T> {

    @Override
    public Observable<T> call(Observable<T> observable) {
        return observable
                .compose(new SchedulerTransformer<T>())
                .compose(new ErrorCheckerTransformer<T>());
    }
}
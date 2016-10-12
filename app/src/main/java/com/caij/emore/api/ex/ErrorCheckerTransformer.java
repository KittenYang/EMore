package com.caij.emore.api.ex;


import com.caij.emore.bean.response.Response;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/8/3.
 */
public class ErrorCheckerTransformer<T extends Response>
        implements Observable.Transformer<T, T> {

    @Override
    public Observable<T> call(Observable<T> observable) {
        return observable.map(new Func1<T, T>() {
            @Override
            public T call(T t) {
                if (!t.isSuccessful()) {
                    throw new RuntimeException(new ErrorResponseException(t));
                }
                return t;
            }
        });
    }

    public static <T extends Response> ErrorCheckerTransformer<T> create() {
        return new ErrorCheckerTransformer<>();
    }
}
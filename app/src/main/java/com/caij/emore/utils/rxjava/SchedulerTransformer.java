package com.caij.emore.utils.rxjava;

import com.caij.emore.utils.ExecutorServiceUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/13.
 */
public class SchedulerTransformer<T> implements Observable.Transformer<T, T> {

    @Override
    public Observable<T> call(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SchedulerTransformer<T> create() {
        return new SchedulerTransformer<>();
    }
}

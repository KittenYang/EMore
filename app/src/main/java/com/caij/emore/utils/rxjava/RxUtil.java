package com.caij.emore.utils.rxjava;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/10/11.
 */

public class RxUtil {

    /**
     * 生成Observable
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createDataObservable(final Provider<T> provider) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(provider.getData());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public static interface Provider<T> {
        public T getData();
    }
}

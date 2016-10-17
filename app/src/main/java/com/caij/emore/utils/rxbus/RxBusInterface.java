package com.caij.emore.utils.rxbus;

import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Created by Caij on 2016/8/9.
 */
public interface RxBusInterface {

    public <T> Observable<T> register(@NonNull Object tag);

    public void unregister(@NonNull Object tag, @NonNull Observable observable);

    public void post(@NonNull Object tag, Object content);

    public void addEventListener(Listener listener);

    public void removeEventListener(Listener listener);

    public static interface Listener {
        public void onPost(Object tag, @NonNull Object content);
        public void onRegister(Object tag);
        public void onUnregister(Object tag, @NonNull Observable observable);
    }

}

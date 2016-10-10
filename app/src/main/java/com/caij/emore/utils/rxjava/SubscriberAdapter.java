package com.caij.emore.utils.rxjava;

import com.caij.emore.present.imp.MainPresentImp;
import com.caij.emore.utils.LogUtil;

import rx.Subscriber;

/**
 * Created by Caij on 2016/10/9.
 */

public abstract class SubscriberAdapter<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        LogUtil.d(SubscriberAdapter.this, e.getMessage());
    }

}

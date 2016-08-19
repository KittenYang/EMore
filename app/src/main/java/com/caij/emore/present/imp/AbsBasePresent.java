package com.caij.emore.present.imp;

import com.caij.emore.present.BasePresent;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/8/17.
 */
public abstract class AbsBasePresent implements BasePresent {

    private CompositeSubscription mCompositeSubscription;

    public AbsBasePresent() {
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }

    protected void addSubscription(Subscription s) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(s);
    }
}

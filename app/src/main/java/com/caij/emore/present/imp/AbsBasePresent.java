package com.caij.emore.present.imp;

import com.caij.emore.present.BasePresent;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/8/17.
 */
public class AbsBasePresent implements BasePresent {

    protected CompositeSubscription mCompositeSubscription;

    public AbsBasePresent() {
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mCompositeSubscription.clear();
    }

    protected void addSubscription(Subscription s) {
        mCompositeSubscription.add(s);
    }
}

package com.caij.emore.view.span;

import android.os.Parcel;
import android.view.View;


/**
 * Created by Caij on 2016/7/17.
 */
public class TopicSpan extends MyURLSpan {

    public TopicSpan(String url) {
        super(url);
    }

    public TopicSpan(Parcel src) {
        super(src);
    }

    @Override
    public void onClick(View widget) {

    }
}

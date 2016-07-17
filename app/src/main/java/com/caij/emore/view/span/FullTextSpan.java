package com.caij.emore.view.span;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.caij.emore.ui.activity.WeiboDetialActivity;

/**
 * Created by Caij on 2016/7/17.
 */
public class FullTextSpan extends MyURLSpan {
    public FullTextSpan(String url) {
        super(url);
    }

    @Override
    public void onClick(View widget) {
        Context context = widget.getContext();
        Intent intent = WeiboDetialActivity.newIntent(context, Long.parseLong(getURL()));
        context.startActivity(intent);
    }
}

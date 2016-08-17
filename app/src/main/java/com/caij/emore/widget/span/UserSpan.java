package com.caij.emore.widget.span;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.caij.emore.ui.activity.UserInfoActivity;

/**
 * Created by Caij on 2016/7/17.
 */
public class UserSpan extends MyURLSpan {

    public UserSpan(String url) {
        super(url);
    }

    @Override
    public void onClick(View widget) {
        Context context = widget.getContext();
        Intent intent = UserInfoActivity.newIntent(context, getURL().replace("@", ""));
        context.startActivity(intent);
    }
}

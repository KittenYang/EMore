package com.caij.emore.widget.span;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.caij.emore.Key;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.fragment.TopicsFragment;


/**
 * Created by Caij on 2016/7/17.
 */
public class TopicSpan extends MyURLSpan {

    public TopicSpan(String url) {
        super(url);
    }

    @Override
    public void onClick(View widget) {
        Context context = widget.getContext();
        String key  = getURL().replaceAll("#", "");
        Bundle bundle = new Bundle();
        bundle.putString(Key.ID, key);
        Intent intent  = DefaultFragmentActivity.starFragmentV4(context, key, TopicsFragment.class, bundle);
        context.startActivity(intent);
    }
}

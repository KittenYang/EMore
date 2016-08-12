package com.caij.emore.view.recyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Caij on 2016/8/12.
 */
public interface OnScrollListener {

    public void onScrollStateChanged(RecyclerView recyclerView, int newState);


    public void onScrolled(RecyclerView recyclerView, int dx, int dy);

}

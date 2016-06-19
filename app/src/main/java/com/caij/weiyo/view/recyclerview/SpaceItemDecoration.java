package com.caij.weiyo.view.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Caij on 2016/6/18.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        if (parent.getChildLayoutPosition(view) >= 14) { //最小面一行不要间距
            outRect.bottom = 0;
        }else {
            outRect.bottom = space;
        }
    }

}
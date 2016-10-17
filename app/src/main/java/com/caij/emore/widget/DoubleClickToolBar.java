package com.caij.emore.widget;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.caij.emore.utils.LogUtil;

/**
 * Created by Caij on 2016/7/19.
 */
public class DoubleClickToolBar extends Toolbar implements View.OnClickListener {

    private DoubleClickListener mDoubleClickListener;
    private OnClickListener mClickListener;
    final long[] mHits = new long[2];

    public DoubleClickToolBar(Context context) {
        super(context);
        init();
    }

    public DoubleClickToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DoubleClickToolBar(Context context, @Nullable AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setOnClickListener(this);
    }

    public void setOnDoubleClickListener(DoubleClickListener listener) {
        mDoubleClickListener = listener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
//        super.setOnClickListener(l);
        mClickListener = l;
    }

    @Override
    public void onClick(View v) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length-1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            if (mDoubleClickListener != null) {
                mDoubleClickListener.onDoubleClick(v);
            }
        }else {
            if (mClickListener != null) {
                mClickListener.onClick(v);
            }
        }
    }

    public static interface DoubleClickListener {
        void onDoubleClick(View v);
    }
}

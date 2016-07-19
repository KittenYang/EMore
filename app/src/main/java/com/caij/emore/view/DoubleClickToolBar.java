package com.caij.emore.view;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.caij.emore.utils.LogUtil;

/**
 * Created by Caij on 2016/7/19.
 */
public class DoubleClickToolBar extends Toolbar {

    private OnClickListener mDoubleClickListener;
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
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    if (mDoubleClickListener != null) {
                        mDoubleClickListener.onClick(v);
                    }
                    LogUtil.d(DoubleClickToolBar.this, "double click");
                }
            }
        });
    }

    public void setOnDoubleClickListener(OnClickListener listener) {
        mDoubleClickListener = listener;
    }

}

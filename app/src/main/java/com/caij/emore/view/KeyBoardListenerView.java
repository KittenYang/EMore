package com.caij.emore.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Caij on 2016/6/20.
 */
public class KeyBoardListenerView extends LinearLayout{

    private int firstHeight = -1;
    private OnKeyBoardListener onKeyBoardListener;

    public KeyBoardListenerView(Context context) {
        super(context);
    }

    public KeyBoardListenerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyBoardListenerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getHeight() > 0 && firstHeight < 0) {
            firstHeight = getMeasuredHeight();
        }else {
            if (changed) {
                if (getHeight() < firstHeight) {
                    if (onKeyBoardListener != null) {
                        onKeyBoardListener.onKeyBoardShow();
                    }
                } else if (getHeight() == firstHeight) {
                    onKeyBoardListener.onKeyBoardHide();
                }
            }
        }
    }

    public void setOnKeyBoardListener(OnKeyBoardListener onKeyBoardListener) {
        this.onKeyBoardListener = onKeyBoardListener;
    }

    public static interface OnKeyBoardListener {
        void onKeyBoardShow();
        void onKeyBoardHide();
    }
}

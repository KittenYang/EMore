package com.caij.emore.view;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.caij.emore.utils.LogUtil;
import com.caij.emore.view.span.MyURLSpan;

/**
 * fix TextView onClick failure on set ClickableSpan
 */
public class FixClickableSpanBugTextView extends TextView {

    public FixClickableSpanBugTextView(Context context) {
        super(context);
        init();
    }

    public FixClickableSpanBugTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FixClickableSpanBugTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //设置了这个会强行设置textview clickable 见fixFocusableAndClickableSettings方法 如果不要点击事件重写几个方法
        setMovementMethod(new LinkTouchMovementMethod());
    }

    public void setClickable(boolean clickable) {
    }

    public void setLongClickable(boolean longClickable) {
    }

    public void setFocusable(boolean focusable) {
    }

    private static class LinkTouchMovementMethod extends LinkMovementMethod {
        private MyURLSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));
                    return true;
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                MyURLSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                    return true;
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return false;
        }

        private MyURLSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            LogUtil.d(this, "line = %s  off = %s   x = %s", line, off, x);

            MyURLSpan[] link = spannable.getSpans(off, off, MyURLSpan.class);
            MyURLSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }

    }

}
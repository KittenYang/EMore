package com.caij.weiyo.view;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.caij.weiyo.R;

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
        setMovementMethod(LinkMovementMethod.getInstance());
        setOnTouchListener(onTouchListener);
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {

        ClickableTextViewMentionLinkOnTouchListener listener =
                new ClickableTextViewMentionLinkOnTouchListener(getResources().getColor(R.color.link_text_press_bg_color));

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listener.onTouch(v, event);

        }
    };

}
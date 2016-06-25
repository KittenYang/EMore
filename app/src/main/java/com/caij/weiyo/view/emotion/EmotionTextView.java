package com.caij.weiyo.view.emotion;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.caij.weiyo.utils.ExecutorServiceUtil;
import com.caij.weiyo.utils.SpannableStringUtil;
import com.caij.weiyo.view.FixClickableSpanBugTextView;

/**
 * Created by Caij on 2016/6/24.
 */
public class EmotionTextView extends FixClickableSpanBugTextView {

    public EmotionTextView(Context context) {
        super(context);
    }

    public EmotionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmotionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public synchronized void setText(final CharSequence text, final BufferType type) {
        if (text != null && !TextUtils.isEmpty(text)) {
            final Context context = getContext().getApplicationContext();
            ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, Spannable>() {
                @Override
                protected Spannable doInBackground(Object[] params) {
                    SpannableString contentSpannableString = SpannableString.valueOf(text);
                    SpannableStringUtil.paraeSpannable(contentSpannableString, context);
                    return contentSpannableString;
                }

                @Override
                protected void onPostExecute(Spannable spannable) {
                    super.onPostExecute(spannable);
                    EmotionTextView.super.setText(text, type);
                }
            });
        }else {
            super.setText(text, type);
        }
    }
}

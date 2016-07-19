package com.caij.emore.view.emotion;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.view.FixClickableSpanBugTextView;

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
                    return SpannableStringUtil.paraeSpannable(text.toString());
                }

                @Override
                protected void onPostExecute(Spannable spannable) {
                    super.onPostExecute(spannable);
                    EmotionTextView.super.setText(spannable, type);
                }
            });
        }else {
            super.setText(text, type);
        }
    }
}

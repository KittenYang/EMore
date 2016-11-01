package com.caij.emore.widget.emotion;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.caij.emore.api.ex.SchedulerTransformer;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.rxjava.RxUtil;
import com.caij.emore.utils.rxjava.SubscriberAdapter;
import com.caij.emore.widget.FixClickableSpanBugTextView;

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
            RxUtil.createDataObservable(new RxUtil.Provider<Spannable>() {
                    @Override
                    public Spannable getData() throws Exception {
                        return SpannableStringUtil.formatSpannable(text.toString());
                    }
                })
                .compose(SchedulerTransformer.<Spannable>create())
                .subscribe(new SubscriberAdapter<Spannable>() {
                    @Override
                    public void onNext(Spannable spannable) {
                        EmotionTextView.super.setText(spannable, type);
                    }
                });
        }else {
            super.setText(text, type);
        }
    }
}

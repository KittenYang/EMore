package com.caij.emore.view.emotion;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.caij.emore.utils.SpannableStringUtil;

/**
 * Created by Caij on 2016/6/24.
 */
public class EmotionEditText extends EditText{

    public EmotionEditText(Context context) {
        super(context);
    }

    public EmotionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmotionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmotionEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onTextChanged(final CharSequence text, int start, int lengthBefore, int lengthAfter) {
        final Context context = getContext().getApplicationContext();
        final Editable editable = getText();
        SpannableStringUtil.paraeSpannable(editable, context);
    }

}

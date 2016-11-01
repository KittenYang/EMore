package com.caij.emore.widget.emotion;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.caij.emore.utils.SpannableStringUtil;

/**
 * Created by Caij on 2016/6/24.
 */
public class EmotionEditText extends EditText{

    public EmotionEditText(Context context) {
        super(context);
        init();
    }

    public EmotionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmotionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmotionEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        TextWatcher textWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, final int count) {
//                LogUtil.d("onTextChanged", "onTextChanged %s %s", getSelectionStart(), getSelectionEnd());
//                removeTextChangedListener(this);
//                Spannable spannable = SpannableStringUtil.formatSpannable(s.toString());
//                setText(spannable);
//                addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
                SpannableStringUtil.formatSpannable(s);
            }
        };
        addTextChangedListener(textWatcher);
    }

}

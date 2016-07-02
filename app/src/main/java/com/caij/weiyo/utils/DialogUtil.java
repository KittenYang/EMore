package com.caij.weiyo.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.caij.weiyo.R;

/**
 * Created by Caij on 2016/6/25.
 */
public class DialogUtil {

    public static Dialog showHintDialog(Context context, String title,
                                 String message,
                                 String pbText,
                                 DialogInterface.OnClickListener pbOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Dialog dialog =  builder.setMessage(message).setTitle(title)
                .setPositiveButton(pbText, pbOnClickListener)
                .create();
        dialog.show();
        return dialog;
    }

    public static Dialog showHintDialog(Context context, String title,
                                        String message,
                                        String pbText,
                                        DialogInterface.OnClickListener okOnClickListener,
                                        String cancelText,
                                        DialogInterface.OnClickListener cancelOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Dialog dialog =  builder.setMessage(message).setTitle(title)
                .setPositiveButton(pbText, okOnClickListener)
                .setNegativeButton(cancelText, cancelOnClickListener)
                .setCancelable(false)
                .create();
        dialog.show();
        return dialog;
    }

    public static Dialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog dialog =  ProgressDialog.show(context, title, message);
        return dialog;
    }

    public static Dialog showSingleSelectDialog(Context context, String title, String[] items,
                                                int selectIndex, final SingleSelectListener singleSelectListener) {
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        int paddingTop = context.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);
        int paddingLeft = context.getResources().getDimensionPixelOffset(R.dimen.spacing_micro);
        int padding  = context.getResources().getDimensionPixelOffset(R.dimen.spacing_large);
        radioGroup.setPadding(padding, paddingTop, padding, paddingTop);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < items.length; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(items[i]);
            radioButton.setId(i);
            radioButton.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
            radioGroup.addView(radioButton, params);
            if (selectIndex == i) {
                radioButton.setChecked(true);
            }
            final int finalI = i;
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    singleSelectListener.onSelect(finalI);
                }
            });
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Dialog dialog =  builder.setTitle(title)
                .setNegativeButton(context.getText(R.string.cancel), null)
                .setView(radioGroup)
                .create();
        dialog.show();
        return dialog;
    }

    public static interface SingleSelectListener {
        public void onSelect(int position);
    }
}

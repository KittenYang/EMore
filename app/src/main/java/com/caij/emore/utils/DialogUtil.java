package com.caij.emore.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.caij.emore.R;

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
                                                int selectIndex, final DialogInterface.OnClickListener singleSelectListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Dialog dialog =  builder.setTitle(title)
                .setNegativeButton(context.getText(R.string.cancel), null)
                .setSingleChoiceItems(items, selectIndex, singleSelectListener)
                .create();
        dialog.show();
        return dialog;
    }

    public static Dialog showItemDialog(Context context, String title, String[] items,
                                        DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Dialog dialog =  builder.setTitle(title)
                .setItems(items, onClickListener)
                .create();
        dialog.show();
        return dialog;
    }

}

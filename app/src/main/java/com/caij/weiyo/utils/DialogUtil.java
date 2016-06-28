package com.caij.weiyo.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

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

    public static Dialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog dialog =  ProgressDialog.show(context, title, message);
        return dialog;
    }
}

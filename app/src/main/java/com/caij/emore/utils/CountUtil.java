package com.caij.emore.utils;

import android.content.Context;
import android.content.res.Resources;

import com.caij.emore.R;

import java.text.DecimalFormat;

/**
 * Created by Caij on 2016/6/30.
 */
public class CountUtil {

    public static String getCounter(Context context, int count, String append) {
        Resources res = context.getResources();

        if (count < 10000)
            return String.valueOf(count) + append;
        else if (count < 100 * 10000)
            return new DecimalFormat("#.0").format(count * 1.0f / 10000) + append + res.getString(R.string.msg_ten_thousand);
        else
            return new DecimalFormat("#").format(count * 1.0f / 10000) + append + res.getString(R.string.msg_ten_thousand);
    }

    public static String getCounter(Context context, int count) {
        return getCounter(context, count, "");
    }
}

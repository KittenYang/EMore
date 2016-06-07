package com.caij.weiyo.utils;

import android.content.Context;
import android.content.res.Resources;

import com.caij.weiyo.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Caij on 2016/6/6.
 */
public class DateUtil {

    @SuppressWarnings("deprecation")
    public static String convDate(Context context, String time) {
        try {
            Resources res = context.getResources();

            StringBuffer buffer = new StringBuffer();

            Calendar createCal = Calendar.getInstance();

            if (time.length() == 13) {
                try {
                    createCal.setTimeInMillis(Long.parseLong(time));
                } catch (Exception e) {
                    createCal.setTimeInMillis(Date.parse(time));
                }
            }
            else {
                createCal.setTimeInMillis(Date.parse(time));
            }

            Calendar currentcal = Calendar.getInstance();
            currentcal.setTimeInMillis(System.currentTimeMillis());

            long diffTime = (currentcal.getTimeInMillis() - createCal.getTimeInMillis()) / 1000;

            // 同一月
            if (currentcal.get(Calendar.MONTH) == createCal.get(Calendar.MONTH)) {
                // 同一天
                if (currentcal.get(Calendar.DAY_OF_MONTH) == createCal.get(Calendar.DAY_OF_MONTH)) {
                    if (diffTime < 3600 && diffTime >= 60) {
                        buffer.append((diffTime / 60) + res.getString(R.string.msg_few_minutes_ago));
                    } else if (diffTime < 60) {
                        buffer.append(res.getString(R.string.msg_now));
                    } else {
                        buffer.append(res.getString(R.string.msg_today)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                    }
                }
                // 前一天
                else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) {
                    buffer.append(res.getString(R.string.msg_yesterday)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                }
            }

            if (buffer.length() == 0) {
                buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
            }

            String timeStr = buffer.toString();
            if (currentcal.get(Calendar.YEAR) != createCal.get(Calendar.YEAR)) {
                timeStr = createCal.get(Calendar.YEAR) + " " + timeStr;
            }

            return timeStr;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return time;
    }

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

    public static String formatDate(long time, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return (new SimpleDateFormat(format)).format(cal.getTime());
    }
}

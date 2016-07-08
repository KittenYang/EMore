package com.caij.emore.utils;

import android.content.Context;
import android.content.res.Resources;

import com.caij.emore.R;

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
                    } else if (diffTime >= 3600 && diffTime < 3600 * 6) {
                        buffer.append(diffTime / 3600).append(res.getString(R.string.msg_hour));
                    }else {
                        buffer.append(res.getString(R.string.msg_today)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                    }
                }else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) { // 前一天
                    buffer.append(res.getString(R.string.msg_yesterday)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                }else {
                    buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
                }
            }else {
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

    public static String formatDate(long time, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return new SimpleDateFormat(format).format(cal.getTime());
    }

    public static String formatTime(long milliseconds) {
        long hours;
        long minutes;
        long seconds;
        long day;

        long totalSeconds = milliseconds / 1000;

        day = totalSeconds / (24 * 60 * 60);
        hours = (totalSeconds % (60 * 60 * 24)) / (60 * 60);
        minutes = (totalSeconds % (60 * 60)) / 60;
        seconds = (totalSeconds % (60 * 60)) % 60;

        if (seconds > 0) {
            minutes++;
        }

        return (day == 0 ? "" : day + "天")
                + (hours == 0 ? "" : hours + "小时")
                + (minutes == 0 ? "" : minutes + "分钟");
    }
}

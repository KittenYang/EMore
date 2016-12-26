package com.caij.emore.utils;

import android.content.Context;
import android.content.res.Resources;

import com.caij.emore.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Caij on 2016/6/6.
 */
public class DateUtil {

//    Fri Jul 15 11:45:31 +0800 2016
    public static final SimpleDateFormat CREATE_TIME_SIMPLE_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

    static {
        CREATE_TIME_SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
    }

    public static String convWeiboDate(Context context, long time) {
            Resources res = context.getResources();

            StringBuffer buffer = new StringBuffer();

            Calendar createCal = Calendar.getInstance();

            createCal.setTimeInMillis(time);

            Calendar currentcal = Calendar.getInstance();
            currentcal.setTimeInMillis(System.currentTimeMillis());

            long diffTime = (currentcal.getTimeInMillis() - createCal.getTimeInMillis()) / 1000;

            if (currentcal.get(Calendar.YEAR) == createCal.get(Calendar.YEAR)) {
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
                        } else {
                            buffer.append(res.getString(R.string.msg_today)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                        }
                    } else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) { // 昨天
                        buffer.append(res.getString(R.string.msg_yesterday)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                    } else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 2) { //前天
                        buffer.append(res.getString(R.string.msg_before_yesterday)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                    } else {
                        buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd"));
                    }
                } else {
                    buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd"));
                }
            }else {
                buffer.append(formatDate(createCal.getTimeInMillis(), "yyyy-MM-dd"));
            }
            return buffer.toString();
    }

    public static String convWeiboDate(Context context, String time) {
        try {
            return convWeiboDate(context, CREATE_TIME_SIMPLE_DATE_FORMAT.parse(time).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }

    public static String formatDate(long time, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return new SimpleDateFormat(format).format(cal.getTime());
    }

    public static String formatCreatetime(long time) {
        return CREATE_TIME_SIMPLE_DATE_FORMAT.format(new Date(time));
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

    public static Long parseCreateTime(String created_at) {
        try {
            return CREATE_TIME_SIMPLE_DATE_FORMAT.parse(created_at).getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }


    public static String convMessageDate(Context context, long time) {
        Resources res = context.getResources();

        StringBuffer buffer = new StringBuffer();

        Calendar createCal = Calendar.getInstance();

        createCal.setTimeInMillis(time);

        Calendar currentcal = Calendar.getInstance();
        currentcal.setTimeInMillis(System.currentTimeMillis());

        long diffTime = (currentcal.getTimeInMillis() - createCal.getTimeInMillis()) / 1000;

        if (currentcal.get(Calendar.YEAR) == createCal.get(Calendar.YEAR)) {
            // 同一月
            if (currentcal.get(Calendar.MONTH) == createCal.get(Calendar.MONTH)) {
                // 同一天
                if (currentcal.get(Calendar.DAY_OF_MONTH) == createCal.get(Calendar.DAY_OF_MONTH)) {
                    buffer.append(res.getString(R.string.msg_today)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                } else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) { // 昨天
                    buffer.append(res.getString(R.string.msg_yesterday)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                } else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 2) { //前天
                    buffer.append(res.getString(R.string.msg_before_yesterday)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
                } else {
                    buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
                }
            } else {
                buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
            }
        }else {
            buffer.append(formatDate(createCal.getTimeInMillis(), "yyyy-MM-dd HH:mm"));
        }
        return buffer.toString();
    }

    public static String formatSeconds(int seconds) {
        StringBuilder builder = new StringBuilder();
        int temp = seconds / 60;
        if (temp == 0 || temp < 10) {
            builder.append("0" + temp);
        } else {
            builder.append(temp);
        }
        builder.append(":");
        temp = seconds % 60;
        if (temp == 0 || temp < 10) {
            builder.append("0" + temp);
        } else {
            builder.append(temp);
        }
        return builder.toString();
    }

}

package com.caij.weiyo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import com.caij.weiyo.R;
import com.caij.weiyo.view.CenteredImageSpan;
import com.caij.weiyo.view.MyURLSpan;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Caij on 2016/6/8.
 */
public class SpannableStringUtil {

    public static final String USER_INFO_SCHEME = "com.caij.weiyo.userinfo://";
    public static final String HTTP_SCHEME = "weibohttp://";
    public static final String TOPIC_SCHEME = "com.caij.weiyo.topics://";

    private static Map<String, String> sEmotions;

    public static void praseName(Spannable spannableString) {
        // 用户名称
//			Pattern pattern = Pattern.compile("@([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)");
        Pattern pattern = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");
        String scheme = USER_INFO_SCHEME;
        Linkify.addLinks(spannableString, pattern, scheme);
    }

    public static void praseHttpUrl(Spannable spannableString) {
        String scheme = HTTP_SCHEME;
        Linkify.addLinks(spannableString, Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]"),
                scheme);
    }

    public static void praseTopic(Spannable spannableString) {
        // 话题
        Pattern dd = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#");
        //Pattern dd = Pattern.compile("#([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)#");
        String scheme = TOPIC_SCHEME;
        Linkify.addLinks(spannableString, dd, scheme);
    }

    public static void praseEmotions(Context context, Spannable spannableString) {
        if (sEmotions == null) {
            synchronized (SpannableStringUtil.class) {
                if (sEmotions == null) {
                    sEmotions = new HashMap<>();
                    try {
                        InputStream in = context.getAssets().open("emotions.properties");
                        Properties properties = new Properties();
                        properties.load(new InputStreamReader(in, "utf-8"));
                        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
                        for (Map.Entry entry : entrySet) {
                            sEmotions.put(entry.getKey().toString(), entry.getValue().toString());
                        }
                    } catch (IOException e) {
                        LogUtil.d("praseEmotions", "Emotions Properties is not found");
                    }
                }
            }
        }

        if (sEmotions.size() > 0) {
            Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(spannableString);
            while (localMatcher.find()) {

                String key = localMatcher.group(0);

                int k = localMatcher.start();
                int m = localMatcher.end();

                String value = sEmotions.get(key);
                if (TextUtils.isEmpty(value))
                    continue;
                try {
                    int id = context.getResources().getIdentifier(value, "mipmap", context.getPackageName());
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
                    int size = context.getResources().getDimensionPixelSize(R.dimen.text_size_medium);
                    bitmap = ImageUtil.zoomBitmap(bitmap, size);
                    CenteredImageSpan l = new CenteredImageSpan(context, bitmap);
                    spannableString.setSpan(l, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }catch (Exception e) {
                    LogUtil.d("praseEmotions", key + ":" + value + " 资源未找到");
                }
            }
        }
    }


    public static void urlSpan2ClickSpan(Spannable spannableString, int color) {
        URLSpan[] urlSpans = spannableString.getSpans(0, spannableString.length(), URLSpan.class);
        MyURLSpan weiboSpan = null;
        for (URLSpan urlSpan : urlSpans) {
            weiboSpan = new MyURLSpan(urlSpan.getURL());
            weiboSpan.setColor(color);
            int start = spannableString.getSpanStart(urlSpan);
            int end = spannableString.getSpanEnd(urlSpan);
            try {
                spannableString.removeSpan(urlSpan);
            } catch (Exception e) {
            }
            spannableString.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}

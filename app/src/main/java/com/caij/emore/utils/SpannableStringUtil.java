package com.caij.emore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;

import com.caij.emore.R;
import com.caij.emore.EMoreApplication;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.User;
import com.caij.emore.bean.Weibo;
import com.caij.emore.view.CenteredImageSpan;
import com.caij.emore.view.MyURLSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Caij on 2016/6/8.
 */
public class SpannableStringUtil {

    public static final String USER_INFO_SCHEME = "com.caij.weiyo.userinfo://";
    public static final String HTTP_SCHEME = "weibohttp://";
    public static final String TOPIC_SCHEME = "com.caij.weiyo.topics://";

    public static void praseName(Spannable spannableString) {
        // 用户名称
//			Pattern pattern = Pattern.compile("@([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)");
        Pattern pattern = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");
        String scheme = USER_INFO_SCHEME;
        Linkify.addLinks(spannableString, pattern, scheme);
    }

    public static void praseHttpUrl(Spannable spannableString) {
        // 网页链接
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

    public static void praseDefaultEmotions(Context context, Spannable spannableString) {
        Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]").matcher(spannableString);
        while (localMatcher.find()) {

            String key = localMatcher.group(0);

            int k = localMatcher.start();
            int m = localMatcher.end();

            Integer drawable = EmotionsUtil.getDefaultDrawableId(key);
            if (drawable == null) {
                LogUtil.d("praseDefaultEmotions", "%s 表情未找到资源", key);
                continue;
            }
            Bitmap bitmap = EmotionsUtil.getCacheEmotion(key);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
                int size = context.getResources().getDimensionPixelSize(R.dimen.text_size_medium);
                bitmap = ImageUtil.zoomBitmap(bitmap, size);
                EmotionsUtil.putCacheEmotion(key, bitmap);
            }

            CenteredImageSpan l = new CenteredImageSpan(context, bitmap);
            spannableString.setSpan(l, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void praseSoftEmotions(Spannable spannableString) {
        for (int i = 0; i < spannableString.length(); i ++) {
            int unicode = Character.codePointAt(spannableString, i);
            int drawableId = EmotionsUtil.getSoftDrawableId(unicode);
            if (drawableId <= 0) {
                continue;
            }
            Bitmap bitmap = EmotionsUtil.getCacheEmotion(String.valueOf(unicode));
            Context context = EMoreApplication.getInstance();
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
                int size = context.getResources().getDimensionPixelSize(R.dimen.text_size_medium);
                bitmap = ImageUtil.zoomBitmap(bitmap, size);
                EmotionsUtil.putCacheEmotion(String.valueOf(unicode), bitmap);
            }
            CenteredImageSpan l = new CenteredImageSpan(context, bitmap);
            spannableString.setSpan(l, i, i + Character.charCount(unicode), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void urlSpan2ClickSpan(Spannable spannableString, int color, int pressColor) {
        URLSpan[] urlSpans = spannableString.getSpans(0, spannableString.length(), URLSpan.class);
        MyURLSpan weiboSpan = null;
        for (URLSpan urlSpan : urlSpans) {
            weiboSpan = new MyURLSpan(urlSpan.getURL());
            weiboSpan.setTextColor(color);
            weiboSpan.setPressColor(pressColor);
            int start = spannableString.getSpanStart(urlSpan);
            int end = spannableString.getSpanEnd(urlSpan);
            try {
                spannableString.removeSpan(urlSpan);
            } catch (Exception e) {
            }
            spannableString.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void paraeSpannable(Weibo weibo) {
        Context applicationContent = EMoreApplication.getInstance();
        int color = applicationContent.getResources().getColor(R.color.link_text_color);
        int pressColor = applicationContent.getResources().getColor(R.color.link_text_press_color);
        SpannableString contentSpannableString = SpannableString.valueOf(weibo.getText());
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseHttpUrl(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseDefaultEmotions(applicationContent, contentSpannableString);
        SpannableStringUtil.praseSoftEmotions(contentSpannableString);
        SpannableStringUtil.urlSpan2ClickSpan(contentSpannableString, color, pressColor);
        weibo.setContentSpannableString(contentSpannableString);

        if (weibo.getRetweeted_status() != null) {
            Weibo reWeibo = weibo.getRetweeted_status();
            String reUserName = "";
            User reUser = reWeibo.getUser();
            if (reUser != null && !TextUtils.isEmpty(reUser.getScreen_name()))
                reUserName = String.format("@%s :", reUser.getScreen_name());
            SpannableString reContentSpannableString = SpannableString.valueOf(reUserName + reWeibo.getText());
            SpannableStringUtil.praseName(reContentSpannableString);
            SpannableStringUtil.praseHttpUrl(reContentSpannableString);
            SpannableStringUtil.praseTopic(reContentSpannableString);
            SpannableStringUtil.praseDefaultEmotions(applicationContent, reContentSpannableString);
            SpannableStringUtil.praseSoftEmotions(contentSpannableString);
            SpannableStringUtil.urlSpan2ClickSpan(reContentSpannableString, color, pressColor);
            reWeibo.setContentSpannableString(reContentSpannableString);
        }
    }

    public static void paraeSpannable(Comment comment, Context applicationContent) {
        int color = applicationContent.getResources().getColor(R.color.link_text_color);
        int pressColor = applicationContent.getResources().getColor(R.color.link_text_press_color);
        SpannableString contentSpannableString = SpannableString.valueOf(comment.getText());
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseHttpUrl(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseDefaultEmotions(applicationContent, contentSpannableString);
        SpannableStringUtil.praseSoftEmotions(contentSpannableString);
        SpannableStringUtil.urlSpan2ClickSpan(contentSpannableString, color, pressColor);

        comment.setTextSpannableString(contentSpannableString);
    }

    public static void paraeSpannable(Spannable text, Context applicationContent) {
        int color = applicationContent.getResources().getColor(R.color.link_text_color);
        int pressColor = applicationContent.getResources().getColor(R.color.link_text_press_color);
        SpannableStringUtil.praseName(text);
        SpannableStringUtil.praseHttpUrl(text);
        SpannableStringUtil.praseTopic(text);
        SpannableStringUtil.praseDefaultEmotions(applicationContent, text);
        SpannableStringUtil.praseSoftEmotions(text);
        SpannableStringUtil.urlSpan2ClickSpan(text, color, pressColor);
    }

}

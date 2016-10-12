package com.caij.emore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;

import com.caij.emore.R;
import com.caij.emore.AppApplication;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Status;
import com.caij.emore.widget.span.CenteredImageSpan;
import com.caij.emore.widget.span.MyURLSpan;
import com.caij.emore.widget.span.TopicSpan;
import com.caij.emore.widget.span.UserSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Caij on 2016/6/8.
 */
public class SpannableStringUtil {

    public static final String FULL_TEXT_SCHEME = "fulltext://";

    public static int color = AppApplication.getInstance().getResources().getColor(R.color.link_text_color);
    public static int pressColor = AppApplication.getInstance().getResources().getColor(R.color.link_text_press_color);


    static String httpRegular = "(http|https)://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]";
    static Pattern sHttpPattern = Pattern.compile(httpRegular);

    public static void praseName(Spannable spannableString) {
        // 用户名称
        Pattern pattern = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");
        MyURLSpan weiboSpan = null;
        Matcher matcher = pattern.matcher(spannableString);
        while (matcher.find()) {
            String name  = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            weiboSpan = new UserSpan(name);
            weiboSpan.setTextColor(color);
            weiboSpan.setPressColor(pressColor);
            spannableString.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static SpannableStringBuilder praseHttpUrlText(String text, List<ShortUrl> shortUrls) {
        text = parseFullText(text);
        text  = parseTextHttpUrl(text, shortUrls);
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(text);
        URLSpan[] urlSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
        MyURLSpan weiboSpan = null;
        for (URLSpan urlSpan : urlSpans) {
            int start = spanned.getSpanStart(urlSpan);
            int end = spanned.getSpanEnd(urlSpan);
            weiboSpan= new MyURLSpan(urlSpan.getURL());
            if (shortUrls != null) {
                for (ShortUrl s : shortUrls) {
                    if (urlSpan.getURL().equals(s.getShort_url())) {
                        weiboSpan.setUrlBean(s);
                        break;
                    }
                }
            }
            weiboSpan.setTextColor(color);
            weiboSpan.setPressColor(pressColor);
            spanned.removeSpan(urlSpan);
            spanned.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanned;
    }

    private static String parseTextHttpUrl(String spannableString, List<ShortUrl> shortUrls) {
        String str = spannableString;
        Matcher matcher = sHttpPattern.matcher(spannableString);
        while (matcher.find()) {
            String url  = matcher.group();
            ShortUrl shortUrl = null;
            if (shortUrls != null) {
                for (ShortUrl s : shortUrls) {
                    if (url.equals(s.getShort_url())) {
                        shortUrl = s;
                        break;
                    }
                }
            }
            String display_name = "网页链接";
            String imageKey  = EmotionsUtil.WEB_CARD_IMAGE_KEY;
            String tagetUrl = url;
            if (shortUrl != null) {
                display_name = shortUrl.getUrl_title();
                switch (shortUrl.getObj_type()) {
                    case ShortUrl.TYPE_WEB:
                        imageKey = EmotionsUtil.WEB_CARD_IMAGE_KEY;
                        break;

                    case ShortUrl.TYPE_VIDEO:
                        imageKey = EmotionsUtil.VIDEO_CARD_IMAGE_KEY;
                        break;

                    case ShortUrl.TYPE_IMAGE:
                        imageKey = EmotionsUtil.IMAGE_CARD_IMAGE_KEY;
                        break;

                    case ShortUrl.TYPE_MUSIC:
                        break;

                    default:
                        break;
                }
            }
            str = str.replace(url, createHtmlString(tagetUrl, imageKey, display_name));
        }
        return str;
    }

    public static List<String> getTextUrl(String text, List<String> shortUrls) {
        Matcher matcher = sHttpPattern.matcher(text);
        if (shortUrls == null) {
            shortUrls = new ArrayList<>();
        }
        while (matcher.find()) {
            String url  = matcher.group();
            shortUrls.add(url);
        }
        return shortUrls;
    }

    private static String createHtmlString(String url, String imageKey, String desc) {
        return "<a href=\"" + url + "\">" + imageKey + desc + "</a>";
    }

    public static void praseTopic(Spannable spannableString) {
        // 话题
        Matcher matcher = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#").matcher(spannableString);
        //Pattern dd = Pattern.compile("#([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)#");
        MyURLSpan weiboSpan = null;
        while (matcher.find()) {
            String topic  = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            weiboSpan = new TopicSpan(topic);
            weiboSpan.setTextColor(color);
            weiboSpan.setPressColor(pressColor);
            spannableString.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static String  parseFullText(String text) {
        Matcher matcher = Pattern.compile("http://m.weibo.cn/client/version").matcher(text);
        String str = text;
        while (matcher.find()) {
            String url  = matcher.group();
            str = str.replace(url, createFullTextString(url));
        }
        return str;
    }

    private static CharSequence createFullTextString(String url) {
        String path = url.replace("http://", FULL_TEXT_SCHEME).replace("https://", FULL_TEXT_SCHEME);
        return " <a href=\"" + path + "\">全文</a>";
    }

    public static void praseDefaultEmotions(Spannable spannableString) {
        Context context = AppApplication.getInstance();
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
            Context context = AppApplication.getInstance();
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


    /**---------------------------------------------------------------------------------------------------*/

    public static void paraeSpannable(Comment comment) {
        SpannableStringBuilder contentSpannableString = praseHttpUrlText(comment.getText() + " ", null);
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseDefaultEmotions(contentSpannableString);
        SpannableStringUtil.praseSoftEmotions(contentSpannableString);
        comment.setTextSpannableString(contentSpannableString);
    }

    public static Spannable paraeSpannable(String text) {
        SpannableStringBuilder contentSpannableString = praseHttpUrlText(text + " ", null);
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseDefaultEmotions(contentSpannableString);
        SpannableStringUtil.praseSoftEmotions(contentSpannableString);
        return contentSpannableString;
    }

    public static void paraeSpannable(Spannable text) {
        SpannableStringUtil.praseName(text);
        SpannableStringUtil.praseTopic(text);
        SpannableStringUtil.praseDefaultEmotions(text);
        SpannableStringUtil.praseSoftEmotions(text);
    }

    public static void paraeSpannable(Status weibo) {
        paraeSpannable(weibo, false);
    }


    public static void paraeSpannable(Status weibo, boolean isLongText) {
        String text;
        if (isLongText && weibo.getIsLongText() && weibo.getLongText() != null
                && !TextUtils.isEmpty(weibo.getLongText().getContent())) {
            text = weibo.getLongText().getContent();
        }else {
            text = weibo.getText();
        }
        SpannableStringBuilder contentSpannableString = praseHttpUrlText(text + " ", weibo.getUrl_struct());
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseDefaultEmotions(contentSpannableString);
        SpannableStringUtil.praseSoftEmotions(contentSpannableString);
        weibo.setContentSpannableString(contentSpannableString);

        Status reWeibo = weibo.getRetweeted_status();
        if (reWeibo != null) {
            String reUserName = "";
            User reUser = reWeibo.getUser();
            if (reUser != null && !TextUtils.isEmpty(reUser.getScreen_name())) {
                reUserName = String.format("@%s :", reUser.getScreen_name());
            }
            SpannableStringBuilder reContentSpannableString = praseHttpUrlText(reUserName + reWeibo.getText() + " ", weibo.getUrl_struct());
            SpannableStringUtil.praseName(reContentSpannableString);
            SpannableStringUtil.praseTopic(reContentSpannableString);
            SpannableStringUtil.praseDefaultEmotions(reContentSpannableString);
            SpannableStringUtil.praseSoftEmotions(contentSpannableString);

            reWeibo.setContentSpannableString(reContentSpannableString);
        }
    }

    public static Spannable paraeSpannable(DirectMessage directMessage) {
        if (!TextUtils.isEmpty(directMessage.getText())) {
            SpannableStringBuilder contentSpannableString = SpannableStringBuilder.valueOf(directMessage.getText());
            SpannableStringUtil.praseName(contentSpannableString);
            SpannableStringUtil.praseTopic(contentSpannableString);
            SpannableStringUtil.praseDefaultEmotions(contentSpannableString);
            SpannableStringUtil.praseSoftEmotions(contentSpannableString);
            return contentSpannableString;
        }
        return null;
    }
}

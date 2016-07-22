package com.caij.emore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;

import com.caij.emore.R;
import com.caij.emore.AppApplication;
import com.caij.emore.bean.Comment;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.view.span.CenteredImageSpan;
import com.caij.emore.view.span.MyURLSpan;
import com.caij.emore.view.span.TopicSpan;
import com.caij.emore.view.span.UserSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Caij on 2016/6/8.
 */
public class SpannableStringUtil {

    public static int color = AppApplication.getInstance().getResources().getColor(R.color.link_text_color);
    public static int pressColor = AppApplication.getInstance().getResources().getColor(R.color.link_text_press_color);

    static Pattern sHttpPattern = Pattern.compile("(http|https)://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]");

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

    public static SpannableStringBuilder praseHttpUrlText(String text, Map<String, UrlInfo> shortLongLinkMap) {
        text = parseFullText(text);
        text  = parseTextHttpUrl(text, shortLongLinkMap);
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(text);
        URLSpan[] urlSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
        MyURLSpan weiboSpan = null;
        for (URLSpan urlSpan : urlSpans) {
            int start = spanned.getSpanStart(urlSpan);
            int end = spanned.getSpanEnd(urlSpan);
            weiboSpan= new MyURLSpan(urlSpan.getURL());
            weiboSpan.setTextColor(color);
            weiboSpan.setPressColor(pressColor);
            spanned.removeSpan(urlSpan);
            spanned.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanned;
    }

    private static String parseTextHttpUrl(String spannableString, Map<String, UrlInfo> shortLongLinkMap) {
        String str = spannableString;
        Matcher matcher = Pattern.compile("(http|https)://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]").matcher(spannableString);
        while (matcher.find()) {
            String url  = matcher.group();
            UrlInfo shortLongLink = null;
            if (shortLongLinkMap != null) {
                shortLongLink = shortLongLinkMap.get(url);
            }
            String display_name = "网页链接";
            String imageKey  = EmotionsUtil.WEB_CARD_IMAGE_KEY;
            int type = UrlInfo.TYPE_WEB;
            String tagetUrl = url;
            if (shortLongLink != null) {
                if (!TextUtils.isEmpty(shortLongLink.getDisplay_name())) {
                    display_name = shortLongLink.getDisplay_name();
                }

                switch (shortLongLink.getUrlType()) {
                    case UrlInfo.TYPE_WEB:
                    case UrlInfo.TYPE_WEB_PAGE:
                        imageKey = EmotionsUtil.WEB_CARD_IMAGE_KEY;
                        type = UrlInfo.TYPE_WEB;
                        break;

                    case UrlInfo.TYPE_VIDEO:
                        imageKey = EmotionsUtil.VIDEO_CARD_IMAGE_KEY;
                        type = UrlInfo.TYPE_VIDEO;
                        break;

                    case UrlInfo.TYPE_IMAGE:
                        imageKey = EmotionsUtil.IMAGE_CARD_IMAGE_KEY;
                        type = UrlInfo.TYPE_IMAGE;
                        tagetUrl = shortLongLink.getLongUrl();
                        break;

                    case UrlInfo.TYPE_MUSIC:
                        type = UrlInfo.TYPE_MUSIC;
                        break;

                    default:
                        type = UrlInfo.TYPE_WEB;
                        break;
                }
            }
            str = str.replace(url, createHtmlString(tagetUrl, type, imageKey, display_name));
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

    private static String createHtmlString(String url, int type, String imageKey, String desc) {
        return "<a href=\"" + type + MyURLSpan.SCHEME_SPIT + url + "\">" + imageKey + desc + "</a>";
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

    public static String  parseFullText(String text) {
//        "...全文： http://m.weibo.cn/1815070622/3998176861989899"
        Matcher matcher = Pattern.compile("全文： (http|https)://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]").matcher(text);
        String str = text;
        while (matcher.find()) {
            String url  = matcher.group();
            str = str.replace(url, createFullTextString(url));
        }
        return str;
    }

    private static CharSequence createFullTextString(String url) {
        String[] strs  = url.split("/");
        return " <a href=\"" + UrlInfo.TYPE_FULL_TEXT + MyURLSpan.SCHEME_SPIT + strs[strs.length - 1] +"\">全文</a>";
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

    public static void paraeSpannable(Weibo weibo, Map<String, UrlInfo> shortLongLinkMap) {
        SpannableStringBuilder contentSpannableString = praseHttpUrlText(weibo.getText() + " ", shortLongLinkMap);
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseDefaultEmotions(contentSpannableString);
        SpannableStringUtil.praseSoftEmotions(contentSpannableString);
        weibo.setContentSpannableString(contentSpannableString);


        if (weibo.getRetweeted_status() != null) {
            Weibo reWeibo = weibo.getRetweeted_status();
            String reUserName = "";
            User reUser = reWeibo.getUser();
            if (reUser != null && !TextUtils.isEmpty(reUser.getScreen_name()))
                reUserName = String.format("@%s :", reUser.getScreen_name());
            SpannableStringBuilder reContentSpannableString = praseHttpUrlText(reUserName + reWeibo.getText() + " ", shortLongLinkMap);
            SpannableStringUtil.praseName(reContentSpannableString);
            SpannableStringUtil.praseTopic(reContentSpannableString);
            SpannableStringUtil.praseDefaultEmotions(reContentSpannableString);
            SpannableStringUtil.praseSoftEmotions(contentSpannableString);
            reWeibo.setContentSpannableString(reContentSpannableString);
        }
    }

    public static void paraeSpannable(Comment comment, Map<String, UrlInfo> shortLongLinkMap) {
        SpannableStringBuilder contentSpannableString = praseHttpUrlText(comment.getText() + " ", shortLongLinkMap);
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

    public static List<String> getWeiboTextHttpUrl(List<Weibo> weibos) {
        List<String> shortUrls = new ArrayList<>();
        for (Weibo weibo : weibos) {
            getWeiboTextHttpUrl(weibo, shortUrls);
        }
        return shortUrls;
    }

    public static List<String> getWeiboTextHttpUrl(Weibo weibo, List<String> shortUrls) {
        List<String> list = getTextUrl(weibo.getText(), shortUrls);
        if (weibo.getRetweeted_status() != null) {
            getWeiboTextHttpUrl(weibo.getRetweeted_status(), list);
        }
        return list;
    }

    public static List<String> getCommentTextHttpUrl(List<Comment> comments) {
        List<String> shortUrls = new ArrayList<>();
        for (Comment comment : comments) {
            getTextUrl(comment.getText(), shortUrls);
        }
        return shortUrls;
    }

    public static List<String> getCommentTextHttpUrl(Comment comment, List<String> shortUrls) {
        return getTextUrl(comment.getText(), shortUrls);
    }

}

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
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.database.bean.LongText;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.widget.span.CenteredImageSpan;
import com.caij.emore.widget.span.MyURLSpan;
import com.caij.emore.widget.span.TopicSpan;
import com.caij.emore.widget.span.UserSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    static String fullTextRegularHead = "全文： ";
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

    public static SpannableStringBuilder praseHttpUrlText(String text, Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap) {
        text = parseFullText(text);
        text  = parseTextHttpUrl(text, shortLongLinkMap);
        SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(text);
        URLSpan[] urlSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
        MyURLSpan weiboSpan = null;
        for (URLSpan urlSpan : urlSpans) {
            int start = spanned.getSpanStart(urlSpan);
            int end = spanned.getSpanEnd(urlSpan);
            weiboSpan= new MyURLSpan(urlSpan.getURL());
            if (shortLongLinkMap != null) {
                ShortUrlInfo.UrlsBean urlsBean = shortLongLinkMap.get(urlSpan.getURL());
                weiboSpan.setUrlBean(urlsBean);
            }
            weiboSpan.setTextColor(color);
            weiboSpan.setPressColor(pressColor);
            spanned.removeSpan(urlSpan);
            spanned.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanned;
    }

    private static String parseTextHttpUrl(String spannableString, Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap) {
        String str = spannableString;
        Matcher matcher = sHttpPattern.matcher(spannableString);
        while (matcher.find()) {
            String url  = matcher.group();
            ShortUrlInfo.UrlsBean shortLongLink = null;
            if (shortLongLinkMap != null) {
                shortLongLink = shortLongLinkMap.get(url);
            }
            String display_name = "网页链接";
            String imageKey  = EmotionsUtil.WEB_CARD_IMAGE_KEY;
            String tagetUrl = url;
            if (shortLongLink != null) {
                if (shortLongLink.getAnnotations() != null
                        && shortLongLink.getAnnotations().size() > 0
                        && shortLongLink.getAnnotations().get(0) != null) {

                    ShortUrlInfo.UrlsBean.AnnotationsBean annotationsBean = shortLongLink.getAnnotations().get(0);

                    if (annotationsBean != null) {

                        ShortUrlInfo.UrlsBean.AnnotationsBean.ObjectBean objectBean = annotationsBean.getObject();
                        if (objectBean != null
                                && !TextUtils.isEmpty(objectBean.getDisplay_name())) {
                            display_name = objectBean.getDisplay_name();
                        }

                        switch (annotationsBean.getUrlType()) {
                            case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_WEB:
                            case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_WEB_PAGE:
                                imageKey = EmotionsUtil.WEB_CARD_IMAGE_KEY;
                                break;

                            case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_VIDEO:
                                imageKey = EmotionsUtil.VIDEO_CARD_IMAGE_KEY;
                                break;

                            case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_IMAGE:
                                imageKey = EmotionsUtil.IMAGE_CARD_IMAGE_KEY;
                                break;

                            case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_MUSIC:
                                break;

                            default:
                                break;
                        }
                    }
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
//        "...全文： http://m.weibo.cn/1815070622/3998176861989899"
        Matcher matcher = Pattern.compile(fullTextRegularHead + "(http|https)://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]").matcher(text);
        String str = text;
        while (matcher.find()) {
            String url  = matcher.group();
            str = str.replace(url, createFullTextString(url));
        }
        return str;
    }

    private static CharSequence createFullTextString(String url) {
        String path = url.replace(fullTextRegularHead, "").
                replace("http://", FULL_TEXT_SCHEME).replace("https://", FULL_TEXT_SCHEME);
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

    public static List<String> getWeiboTextHttpUrl(List<Weibo> weibos) {
        List<String> shortUrls = new ArrayList<>();
        for (Weibo weibo : weibos) {
            getWeiboTextHttpUrl(weibo, false, shortUrls);
        }
        return shortUrls;
    }

    public static List<String> getWeiboTextHttpUrl(Weibo weibo, boolean isLongText, List<String> shortUrls) {
        String text;
        LongText longText = weibo.getLongText();
        if (isLongText && longText != null && !TextUtils.isEmpty(longText.getLongTextContent())) {
            text = longText.getLongTextContent();
        }else {
            text = weibo.getText();
        }
        List<String> list = getTextUrl(text, shortUrls);
        if (weibo.getRetweeted_status() != null) {
            getWeiboTextHttpUrl(weibo.getRetweeted_status(), isLongText, list);
        }
        return list;
    }


    public static void paraeSpannable(Weibo weibo, Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap) {
        paraeSpannable(weibo, false, shortLongLinkMap);
    }


    public static void paraeSpannable(Weibo weibo, boolean isLongText, Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap) {
        String text;
        if (isLongText && weibo.getIsLongText() && weibo.getLongText() != null
                && !TextUtils.isEmpty(weibo.getLongText().getLongTextContent())) {
            text = weibo.getLongText().getLongTextContent();
        }else {
            text = weibo.getText();
        }
        SpannableStringBuilder contentSpannableString = praseHttpUrlText(text + " ", shortLongLinkMap);
        SpannableStringUtil.praseName(contentSpannableString);
        SpannableStringUtil.praseTopic(contentSpannableString);
        SpannableStringUtil.praseDefaultEmotions(contentSpannableString);
        SpannableStringUtil.praseSoftEmotions(contentSpannableString);
        weibo.setContentSpannableString(contentSpannableString);

        Weibo reWeibo = weibo.getRetweeted_status();
        if (reWeibo != null) {
            String reUserName = "";
            User reUser = reWeibo.getUser();
            if (reUser != null && !TextUtils.isEmpty(reUser.getScreen_name())) {
                reUserName = String.format("@%s :", reUser.getScreen_name());
            }
            SpannableStringBuilder reContentSpannableString = praseHttpUrlText(reUserName + reWeibo.getText() + " ", shortLongLinkMap);
            SpannableStringUtil.praseName(reContentSpannableString);
            SpannableStringUtil.praseTopic(reContentSpannableString);
            SpannableStringUtil.praseDefaultEmotions(reContentSpannableString);
            SpannableStringUtil.praseSoftEmotions(contentSpannableString);

            reWeibo.setContentSpannableString(reContentSpannableString);
        }
    }
}

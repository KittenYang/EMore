package com.caij.emore.view.span;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.caij.emore.AppSettings;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.ImagePrewActivity;
import com.caij.emore.ui.activity.VideoViewPlayingActivity;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.utils.EmotionsUtil;
import com.caij.emore.utils.SpannableStringUtil;
import com.caij.emore.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Caij on 2016/6/8.
 */
public class MyURLSpan extends ClickableSpan implements ParcelableSpan {

    public static final String SCHEME_SPIT = "__";

    private final String mURL;
    private int color;
    private int pressColor;
    private boolean pressed;
    private ShortUrlInfo.UrlsBean urlBean;

    public MyURLSpan(String url) {
        this.mURL = url;
    }

    public MyURLSpan(Parcel src) {
        this.mURL = src.readString();
    }

    public int getSpanTypeId() {
        return 11;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mURL);
    }

    public String getURL() {
        return this.mURL;
    }

    public void onClick(View widget) {
        Context context = widget.getContext();
        toDetaiPage(context, widget);
    }

    private void toDetaiPage(Context context, View widget){
        if (urlBean != null) {
            if (urlBean.getAnnotations() != null
                    && urlBean.getAnnotations().size() > 0
                    && urlBean.getAnnotations().get(0) != null) {

                ShortUrlInfo.UrlsBean.AnnotationsBean annotationsBean = urlBean.getAnnotations().get(0);
                ShortUrlInfo.UrlsBean.AnnotationsBean.ObjectBean objectBean = annotationsBean.getObject();
                switch (annotationsBean.getUrlType()) {
                    case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_WEB:
                    case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_WEB_PAGE:
                        toWebActivity(context, getURL());
                        break;

                    case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_VIDEO:
                        Intent intent;
                        if (objectBean != null && !objectBean.getUrl().contains("video.weibo.com")) {
                            intent = VideoViewPlayingActivity.newIntent(context, objectBean.getStream().getUrl());
                            context.startActivity(intent);
                        }else if (widget.getTag() != null && widget.getTag() instanceof Weibo) {
                            Weibo weibo = (Weibo) widget.getTag();
                            intent = VideoViewPlayingActivity.newIntent(context, weibo.getId());
                            context.startActivity(intent);
                        }else {
                            toWebActivity(context, urlBean.getUrl_short());
                        }
                        break;

                    case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_IMAGE:
                        toWebActivity(context, urlBean.getUrl_long());
                        break;

                    case ShortUrlInfo.UrlsBean.AnnotationsBean.TYPE_MUSIC:
                        toWebActivity(context, urlBean.getUrl_long());
                        break;

                    default:
                        toWebActivity(context, urlBean.getUrl_long());
                        break;
                }
            }else {
                toWebActivity(context, getURL());
            }
        }else {
            //这里有两种情况 一种是网页 一种是全文
            if (getURL().startsWith(SpannableStringUtil.FULL_TEXT_SCHEME)) {
                String weibiId = getURL().replace(SpannableStringUtil.FULL_TEXT_SCHEME, "");
                Intent intent = WeiboDetialActivity.newIntent(context, Long.parseLong(weibiId));
                context.startActivity(intent);
            }else {
                toWebActivity(context, getURL());
            }
        }
    }

    public void setTextColor(int color) {
        this.color = color;
    }

    public void setPressColor(int color) {
        this.pressColor = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false);
        ds.bgColor = pressed ? pressColor : 0x00000000;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    private void toWebActivity(Context context, String url) {
        if (AppSettings.isInnerBrower(context)) {
            url = url.replace("http", context.getString(R.string.emore_http_scheme));
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        context.startActivity(intent);
    }

    public void setUrlBean(ShortUrlInfo.UrlsBean urlBean) {
        this.urlBean = urlBean;
    }
}

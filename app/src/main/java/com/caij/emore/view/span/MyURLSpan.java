package com.caij.emore.view.span;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.caij.emore.AppSettings;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.ui.activity.ImagePrewActivity;
import com.caij.emore.ui.activity.VideoViewPlayingActivity;
import com.caij.emore.ui.activity.WeiboDetialActivity;
import com.caij.emore.utils.SpannableStringUtil;

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
        String[] values = getURL().split(MyURLSpan.SCHEME_SPIT);
        if (values.length == 1 || values.length == 0) {
            toWebActivity(context, getURL());
        }else {
            int type = Integer.parseInt(values[0]);
            String url =values[1];
            switch (type) {
                case UrlInfo.TYPE_WEB:
                case UrlInfo.TYPE_WEB_PAGE:
                    toWebActivity(context, url);
                    break;

                case UrlInfo.TYPE_VIDEO:
                    if (url.contains("video.weibo.com") &&  widget.getTag() != null && widget.getTag() instanceof Weibo) {
                        Intent intent = new Intent(context, VideoViewPlayingActivity.class);
                        intent.putExtra(Key.ID, url);
                        context.startActivity(intent);
                    }else {
                        toWebActivity(context, url);
                    }
                    break;

                case UrlInfo.TYPE_IMAGE: {
                    toWebActivity(context, url);
                    break;
                }

                case UrlInfo.TYPE_MUSIC:
                    toWebActivity(context, url);
                    break;
                case UrlInfo.TYPE_FULL_TEXT:
                    Intent intent = WeiboDetialActivity.newIntent(context, Long.parseLong(url));
                    context.startActivity(intent);
                    break;

                default:
                    toWebActivity(context, url);
                    break;
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
}

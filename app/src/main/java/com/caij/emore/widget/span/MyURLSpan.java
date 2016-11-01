package com.caij.emore.widget.span;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import com.caij.emore.AppSettings;
import com.caij.emore.R;
import com.caij.emore.bean.ShortUrl;
import com.caij.emore.database.bean.Status;
import com.caij.emore.ui.activity.StatusDetailActivity;
import com.caij.emore.utils.SpannableStringUtil;

/**
 * Created by Caij on 2016/6/8.
 */
public class MyURLSpan extends URLSpan implements ParcelableSpan {

    private int color;
    private int pressColor;
    private boolean pressed;
    private ShortUrl urlBean;

    public MyURLSpan(String url) {
        super(url);
    }

    public MyURLSpan(Parcel in) {
        super(in);
        urlBean = (ShortUrl) in.readSerializable();
    }

    public void onClick(View widget) {
        Context context = widget.getContext();
        toDetailPage(context, widget);
    }

    private void toDetailPage(Context context, View widget){
        if (urlBean != null) {
            switch (urlBean.getObj_type()) {
                case ShortUrl.TYPE_WEB:
                    toWebActivity(context, getURL());
                    break;

                case ShortUrl.TYPE_VIDEO:
                    toWebActivity(context, urlBean.getShort_url());
                    break;

                case ShortUrl.TYPE_IMAGE:
                    toWebActivity(context, urlBean.getShort_url());
                    break;

                case ShortUrl.TYPE_MUSIC:
                    toWebActivity(context, urlBean.getShort_url());
                    break;

                default:
                    toWebActivity(context, urlBean.getShort_url());
                    break;
            }
        }else {
            //这里有两种情况 一种是网页 一种是全文
            if (getURL().startsWith(SpannableStringUtil.FULL_TEXT_SCHEME)) {
                if (widget.getTag() instanceof Status) {
                    Status status = (Status) widget.getTag();
                    Intent intent = StatusDetailActivity.newIntent(context, status.getId());
                    context.startActivity(intent);
                }
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

    public void setUrlBean(ShortUrl urlBean) {
        this.urlBean = urlBean;
    }

    public static final Creator<MyURLSpan> CREATOR = new Creator<MyURLSpan>() {
        @Override
        public MyURLSpan createFromParcel(Parcel in) {
            return new MyURLSpan(in);
        }

        @Override
        public MyURLSpan[] newArray(int size) {
            return new MyURLSpan[size];
        }
    };

}

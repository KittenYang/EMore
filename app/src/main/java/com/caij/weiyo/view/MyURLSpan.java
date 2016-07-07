package com.caij.weiyo.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.caij.weiyo.AppSettings;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.SpannableStringUtil;

/**
 * Created by Caij on 2016/6/8.
 */
public class MyURLSpan extends ClickableSpan implements ParcelableSpan {

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
        Uri uri = Uri.parse(this.getURL());
        Context context = widget.getContext();
        if (SpannableStringUtil.HTTP_SCHEME.contains(uri.getScheme()) && !AppSettings.isInnerBrower(widget.getContext())) {
            uri = Uri.parse(getURL().replace(SpannableStringUtil.HTTP_SCHEME, ""));
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        context.startActivity(intent);
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
}

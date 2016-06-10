package com.caij.weiyo.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

import com.caij.weiyo.utils.LogUtil;

/**
 * Created by Caij on 2016/6/8.
 */
public class MyURLSpan extends ClickableSpan implements ParcelableSpan {

    private final String mURL;
    private int color;

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
        LogUtil.v(MyURLSpan.class.getSimpleName(), String.format("the link(%s) was clicked ", new Object[]{this.getURL()}));
        Uri uri = Uri.parse(this.getURL());
        Context context = widget.getContext();
        Intent intent;
        intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false);
    }

    public void onLongClick(View widget) {

    }
}

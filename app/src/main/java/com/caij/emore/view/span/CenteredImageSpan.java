package com.caij.emore.view.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;

/**
 * Created by Caij on 2016/6/8.
 */
public class CenteredImageSpan extends ImageSpan {

    public CenteredImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        Drawable b = getDrawable();
        canvas.save();

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2
                - b.getBounds().bottom / 2;


        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}

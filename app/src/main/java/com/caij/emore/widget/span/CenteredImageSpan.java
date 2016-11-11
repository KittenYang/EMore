package com.caij.emore.widget.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.ImageSpan;

import java.lang.ref.WeakReference;

/**
 * Created by Caij on 2016/6/8.
 */
public class CenteredImageSpan extends ImageSpan {

    public CenteredImageSpan(Context context, Bitmap b) {
        super(context, b, ALIGN_BASELINE);
    }

//    @Override
//    public void draw(@NonNull Canvas canvas, CharSequence text,
//                     int start, int end, float x,
//                     int top, int y, int bottom, @NonNull Paint paint) {
//        Drawable b = getDrawable();
//        canvas.save();
//
//        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
//        int transY = (y + fm.descent + y + fm.ascent) / 2
//                - b.getBounds().bottom / 2;
//
//        canvas.translate(x, transY);
//        b.draw(canvas);
//        canvas.restore();
//
////        Drawable b = getCachedDrawable();
////        canvas.save();
////
////        int transY = bottom - b.getBounds().bottom;
////        if (mVerticalAlignment == ALIGN_BASELINE) {
////            transY = top + ((bottom - top) / 2) - ((b.getBounds().bottom - b.getBounds().top) / 2) - mTop;
////        }
////
////        canvas.translate(x, transY);
////        b.draw(canvas);
////        canvas.restore();
//    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = 0;
        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }
}

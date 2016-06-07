package com.caij.weiyo.okhttp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

//caij
public class TopTransformation extends BitmapTransformation {
  public static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;

  public TopTransformation(Context context) {
    super(context);
  }

  @Override
  protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
    final Bitmap toReuse = pool.get(outWidth, outHeight, toTransform.getConfig() != null
            ? toTransform.getConfig() : Bitmap.Config.ARGB_8888);

    Matrix m = new Matrix();
    float widthR = outWidth * 1.0f / toTransform.getWidth();
    float heightR = outHeight * 1.0f / toTransform.getHeight();
    float scale = Math.max(widthR, heightR);
    m.setScale(scale, scale);
    m.postTranslate(0, 0);
    Bitmap result;
    if (toReuse != null) {
      result = toReuse;
    } else {
      result = Bitmap.createBitmap(outWidth, outHeight, getSafeConfig(toTransform));
    }

    TransformationUtils.setAlpha(toTransform, result);

    Canvas canvas = new Canvas(result);
    Paint paint = new Paint(PAINT_FLAGS);
    canvas.drawBitmap(toTransform, m, paint);

    return result;
  }

  private static Bitmap.Config getSafeConfig(Bitmap bitmap) {
    return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
  }

  @Override public String getId() {
    return "TopTransformation()";
  }
}

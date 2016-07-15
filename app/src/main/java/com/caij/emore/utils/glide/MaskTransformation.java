package com.caij.emore.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.caij.emore.utils.LogUtil;

public class MaskTransformation implements Transformation<Bitmap> {

    private static Paint maskingPaint = new Paint();
    private Context context;
    private BitmapPool bitmapPool;
    private int maskId;

    static {
        maskingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    }

    /**
     * 如果要更新mask文件，请同时修改mask文件的文件名，否则会导致mask文件更新后，cache依然是旧的情况
     */
    public MaskTransformation(Context context, int maskId) {
        bitmapPool = Glide.get(context).getBitmapPool();
        this.context = context.getApplicationContext();
        this.maskId = maskId;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();
        int width = source.getWidth();
        int height = source.getHeight();

        LogUtil.e("MaskTransformation", "outWidth " + outWidth + " outHeight " + outHeight
                + ", resource width " + width + " height " + height);

        Bitmap result = bitmapPool.get(width, height, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        Drawable mask = ContextCompat.getDrawable(context, maskId);

        Canvas canvas = new Canvas(result);
        mask.setBounds(0, 0, width, height);
        mask.draw(canvas);
        canvas.drawBitmap(source, 0, 0, maskingPaint);

        return BitmapResource.obtain(result, bitmapPool);
    }

    @Override
    public String getId() {
        return "MaskTransformation(maskId="
                + context.getResources().getResourceEntryName(maskId) + ")";
    }

}

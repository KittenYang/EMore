package com.caij.weiyo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.caij.weiyo.utils.okhttp.OkHttpClientProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Created by Caij on 2016/6/7.
 */
public class ImageUtil {

    public static BitmapFactory.Options getImageOptions(String url) throws IOException {
        OkHttpClient okHttpClient = OkHttpClientProvider.getDefaultOkHttpClient();
        okHttpClient = okHttpClient.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS).build();
        okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
        okHttpRequestBuilder.url(url);
        okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
        Call okHttpCall = okHttpClient.newCall(okHttpRequest);
        ResponseBody responseBody = okHttpCall.execute().body();
        InputStream inputStream = responseBody.byteStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        responseBody.close();
        return options;
    }

    public static Bitmap zoomBitmap(Bitmap source, int width) {
        Matrix matrix = new Matrix();
        float scale = (float)width * 1.0F / (float)source.getWidth();
        matrix.setScale(scale, scale);
        Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        if (result != source) {
            source.recycle();
        }
        return result;
    }
}

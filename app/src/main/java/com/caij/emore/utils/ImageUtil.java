package com.caij.emore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Caij on 2016/6/7.
 */
public class ImageUtil {

    private final static String PATTERN = "yyyyMMddHH_mmss";

    public static BitmapFactory.Options getImageOptions(String url) throws IOException {
        OkHttpClient okHttpClient = OkHttpClientProvider.getDefaultOkHttpClient();
        okHttpClient = okHttpClient.newBuilder().readTimeout(3000, TimeUnit.MILLISECONDS)
                .writeTimeout(3000, TimeUnit.MILLISECONDS).build();
        Request.Builder okHttpRequestBuilder = new Request.Builder();
        okHttpRequestBuilder.url(url);
        Request okHttpRequest = okHttpRequestBuilder.build();
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

    public static String getImageType(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ImageHeaderParser imageHeaderParser= new ImageHeaderParser(fileInputStream);
        ImageHeaderParser.ImageType imageType = imageHeaderParser.getType();
        switch (imageType) {
            case GIF:
                return "gif";

            case JPEG:
                return "jpeg";

            case PNG_A:
                return "png";

            case PNG:
                return "png";

            case UNKNOWN:
                return "jpeg";
        }
        return "jpeg";
    }

    public static String createCamareImage(Context context) {
        String timeStamp = new SimpleDateFormat(PATTERN, Locale.CHINA).format(new Date());

        String externalStorageState = Environment.getExternalStorageState();

        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera"
                    + "IMG_" + timeStamp + ".jpg");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            return file.getAbsolutePath();
        } else {
            String path = context.getFilesDir() + "IMG_" + timeStamp + ".jpg";
            return path;
        }

    }
}

package com.caij.emore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import static android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL;
import static android.media.ExifInterface.ORIENTATION_NORMAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static android.media.ExifInterface.ORIENTATION_TRANSPOSE;
import static android.media.ExifInterface.ORIENTATION_TRANSVERSE;
import static android.media.ExifInterface.TAG_ORIENTATION;

/**
 * Created by Caij on 2016/6/7.
 */
public class ImageUtil {

    private final static String PATTERN = "yyyyMMddHH_mmss";
    private static final int TARGET_WIDTH = 1080;
    private static final int TARGET_HEIGHT = 1920;
    private static final int QUALITY_MEDIUM = 40;
    private static final int QUALITY_LOW = 20;

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

    public static String createCameraImagePath(Context context) {
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
            return context.getFilesDir() + "IMG_" + timeStamp + ".jpg";
        }
    }

    public static String compressImage(String sourcePath, Context context) {
        String outPath = new File(CacheUtils.getCompressImageDir(context), MD5Util.string2MD5(sourcePath) + ".compress").getAbsolutePath();
        compressImage(sourcePath, outPath);
        return outPath;
    }

    public static void compressImage(String sourcePath, String outPath) {
        compressImage(sourcePath, outPath, TARGET_WIDTH, TARGET_HEIGHT);
    }

    public static void compressImage(String sourcePath, String outPath, int targetWidth, int targetHeight){
        File ourFile = new File(outPath);
        File sourceFile = new File(sourcePath);
        try {
            if (ourFile.exists()) {
                LogUtil.d("compressImage", "out file length %s", FileUtil.getFileSizeString(ourFile));
                return;
            }

            if ("gif".equals(getImageType(sourceFile))) {
                outPath = sourcePath;
                LogUtil.d("compressImage", "source File file is gif ");
                return;
            }

            LogUtil.d("compressImage", "source file length %s", FileUtil.getFileSizeString(sourceFile));

            if (!ourFile.getParentFile().exists()) {
                boolean isSuccess  = ourFile.getParentFile().mkdirs();
                if (!isSuccess) {
                    throw new FileNotFoundException("文件夹创建失败");
                }
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(sourcePath, options);

            int rotation = getFileExifRotation(Uri.fromFile(sourceFile));

            int inSampleSize;
            if (isLongImage(options.outWidth, options.outHeight)) {
                inSampleSize = 1;
            }else {
                inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, targetWidth, targetHeight, rotation);
            }

            LogUtil.d("compressImage", "rotation  %s, options.outWidth %s, options.outHeight %s", rotation, options.outWidth, options.outHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;

            LogUtil.d("compressImage", "inSampleSize  %s", inSampleSize);

            Bitmap bitmap =  BitmapFactory.decodeFile(sourcePath, options);
            FileOutputStream outputStream = new FileOutputStream(ourFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_MEDIUM, outputStream);

            if (rotation != 0) {
                int saveRotation;
                switch (rotation) {
                    case 90: {
                        saveRotation = ORIENTATION_ROTATE_90;
                        break;
                    }
                    case 180: {
                        saveRotation = ORIENTATION_ROTATE_180;
                        break;
                    }
                    case 270: {
                        saveRotation = ORIENTATION_ROTATE_270;
                        break;
                    }
                    default: {
                        saveRotation = ORIENTATION_NORMAL;
                    }
                }
                ExifInterface exifInterface = new ExifInterface(outPath);
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(saveRotation));
                exifInterface.saveAttributes();
            }

            LogUtil.d("compressImage", "out file length %s", FileUtil.getFileSizeString(ourFile));
        }catch (Exception e) {
            outPath = sourcePath;
            LogUtil.d("compressImage", "Exception 上传原图");
        }catch (OutOfMemoryError error) {
            outPath = sourcePath;
            LogUtil.d("compressImage", "OutOfMemoryError 上传原图");
        }
    }

    private static int calculateInSampleSize(int width, int height, int targetWidth,
                                             int targetHeight, int rotation) {

        if (rotation == 90 || rotation == 270) {
            int temp = width;
            width = height;
            height = temp;
        }

        int inSampleSize = 1;

        if (width < targetWidth && height < targetHeight) {
            return inSampleSize;
        }

        float widthRatio = width * 1f / targetWidth;
        float heightRatio = height * 1f / targetHeight;

        return (int) Math.max(widthRatio, heightRatio);
    }

    public static int getFileExifRotation(Uri uri) {
        int orientation = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(uri.getPath());
            orientation = exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int rotation;
        switch (orientation) {
            case ORIENTATION_ROTATE_90:
            case ORIENTATION_TRANSPOSE: {
                rotation = 90;
                break;
            }
            case ORIENTATION_ROTATE_180:
            case ORIENTATION_FLIP_VERTICAL: {
                rotation = 180;
                break;
            }
            case ORIENTATION_ROTATE_270:
            case ORIENTATION_TRANSVERSE: {
                rotation = 270;
                break;
            }
            default: {
                rotation = 0;
            }
        }
        return rotation;
    }

    public static boolean isLongImage(int width, int height) {
        return width * 1f / height > 3.0  ||  height * 1f / width > 3.0;
    }

}

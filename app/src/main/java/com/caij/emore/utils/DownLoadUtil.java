package com.caij.emore.utils;

import android.os.AsyncTask;

import com.caij.emore.utils.rxjava.RxUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Caij on 2016/10/20.
 */

public class DownLoadUtil {

    private static final long INTERVAL_PROGRESS_TIME  = 500;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(3000, TimeUnit.MILLISECONDS)
            .writeTimeout(3000, TimeUnit.MILLISECONDS).build();

    public static interface Callback {
        void onSuccess(File file);
        void onProgress(long total, long progress);
        void onError(Exception e);
    }

    public static void down(final String url, final String filePath, final Callback callback) {
        new AsyncTask<Object, Long, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                Request.Builder okHttpRequestBuilder = new Request.Builder();
                okHttpRequestBuilder.url(url);
                Request okHttpRequest = okHttpRequestBuilder.build();
                Call okHttpCall = okHttpClient.newCall(okHttpRequest);
                File file = new File(filePath);
                try {
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    ResponseBody responseBody = okHttpCall.execute().body();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    InputStream inputStream = responseBody.byteStream();
                    byte[] buf = new byte[4096];
                    int length;
                    long writeTotalLength = 0;
                    publishProgress(responseBody.contentLength(), 0L);
                    long preTime = System.currentTimeMillis();
                    while ((length = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, length);
                        writeTotalLength += length;
                        if (System.currentTimeMillis() - preTime > INTERVAL_PROGRESS_TIME) {
                            publishProgress(responseBody.contentLength(), writeTotalLength);
                        }
                    }
                    return file;
                } catch (IOException e) {
                    file.deleteOnExit();
                    return e;
                }
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                callback.onProgress(values[0], values[1]);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o instanceof Exception) {
                    callback.onError((Exception) o);
                }else {
                    callback.onSuccess((File) o);
                }
            }
        }.execute(ExecutorServicePool.DOWN_SERVICE);
    }
}

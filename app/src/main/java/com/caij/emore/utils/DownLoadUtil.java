package com.caij.emore.utils;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Caij on 2016/10/20.
 */

public class DownLoadUtil {

    private static final long INTERVAL_PROGRESS_TIME  = 500;

    private static final int BUF_LONG  = 4096;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(3, TimeUnit.SECONDS).build();

    private static Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static interface ProgressListener {
        void onProgress(long total, long progress);
    }

    public static Observable<File> down(final String url, final String filePath, final ProgressListener progressListener) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                Request.Builder okHttpRequestBuilder = new Request.Builder();
                okHttpRequestBuilder.url(url);
                Request okHttpRequest = okHttpRequestBuilder.build();
                Call okHttpCall = okHttpClient.newCall(okHttpRequest);
                File file = new File(filePath);
                try {
                    if (!file.getParentFile().exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            throw new IOException();
                        }
                    }
                    ResponseBody responseBody = okHttpCall.execute().body();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    InputStream inputStream = responseBody.byteStream();
                    byte[] buf = new byte[BUF_LONG];
                    int readLength;
                    long writeTotalLength = 0;
                    long preTime = 0;

                    while ((readLength = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, readLength);
                        writeTotalLength += readLength;
                        if (System.currentTimeMillis() - preTime > INTERVAL_PROGRESS_TIME) {
                            postProgress(responseBody.contentLength(), writeTotalLength, progressListener);
                            preTime = System.currentTimeMillis();
                        }
                    }
                    subscriber.onNext(file);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    file.deleteOnExit();
                    subscriber.onError(e);
                }
            }
        });
    }


    private static void postProgress(final long total, final long progress, final ProgressListener progressListener) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                progressListener.onProgress(total, progress);
            }
        });
    }
}

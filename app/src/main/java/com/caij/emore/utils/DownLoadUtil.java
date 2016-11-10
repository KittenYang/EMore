package com.caij.emore.utils;

import android.os.Handler;
import android.os.Looper;

import com.caij.emore.utils.okhttp.OkHttpClientProvider;

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

    private static OkHttpClient okHttpClient = OkHttpClientProvider.getDefaultOkHttpClient(false).newBuilder()
            .readTimeout(3, TimeUnit.SECONDS).writeTimeout(3, TimeUnit.SECONDS).build();

    public static Observable<Progress> down(final String url, final String filePath) {
        return Observable.create(new Observable.OnSubscribe<Progress>() {
            @Override
            public void call(Subscriber<? super Progress> subscriber) {
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

                    Progress progress = new Progress();
                    progress.total = responseBody.contentLength();

                    progress.read = 0;

                    subscriber.onNext(progress);

                    while ((readLength = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, readLength);
                        writeTotalLength += readLength;
                        if (System.currentTimeMillis() - preTime > INTERVAL_PROGRESS_TIME) {
                            progress.read = writeTotalLength;
                            subscriber.onNext(progress);
                            preTime = System.currentTimeMillis();
                        }
                    }

                    subscriber.onCompleted();
                } catch (Exception e) {
                    file.deleteOnExit();
                    subscriber.onError(e);
                }
            }
        });
    }

    public static class Progress {
        public long total;
        public long read;
    }
}

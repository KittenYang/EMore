package com.caij.emore.utils;

import android.os.AsyncTask;

import com.caij.emore.database.bean.LocalFile;
import com.caij.emore.database.dao.DBManager;
import com.caij.emore.database.dao.LocalFileDao;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Created by Caij on 2016/6/13.
 */
public class DownLoadUtil {

    public static final int INTERVAL = 3000;

    public static AsyncTask downFile(final String url, final String filePath, final DownLoadListener listener) {
        AsyncTask<Object, Long, File> asyncTask = new AsyncTask<Object, Long, File>() {
            @Override
            protected File doInBackground(Object... params) {
                LocalFileDao fileDao = DBManager.getDaoSession().getLocalFileDao();
                LocalFile localFile = fileDao.load(url);
                if (localFile != null && localFile.getStatus() == 2) {
                    File file = new File(localFile.getPath());
                    if (file.exists()) {
                        return file;
                    }
                }

                File file = new File(filePath);
                OkHttpClient okHttpClient = OkHttpClientProvider.getDefaultOkHttpClient();
                okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
                okHttpRequestBuilder.url(url);
                if (file.exists()) {
                    okHttpRequestBuilder.addHeader("RANGE", "bytes=" + file.length() + "-");
                }else {
                    File current = file.getParentFile();
                    if(current.exists() || current.mkdirs()) {
                    }
                }
                okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
                Call okHttpCall = okHttpClient.newCall(okHttpRequest);
                try {
                    ResponseBody responseBody = okHttpCall.execute().body();
                    long time = System.currentTimeMillis();
                    long current = 0l;
                    publishProgress(responseBody.contentLength(), current);
                    InputStream inputStream = responseBody.byteStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] bytes = new  byte[2048];
                    int length;
                    while ((length = inputStream.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, length);
                        current += length;
                        if (System.currentTimeMillis() - time > INTERVAL) {
                            publishProgress(responseBody.contentLength(), current);
                            time = System.currentTimeMillis();
                            LogUtil.d("DownLoadUtil", "on time %s progress %s", time, current);
                        }
                    }
                    publishProgress(responseBody.contentLength(), responseBody.contentLength());
                    responseBody.close();
                    fileOutputStream.close();
                    fileDao.insertOrReplace(new LocalFile(url, filePath, 2));
                    return file;
                } catch (IOException e) {
                    LogUtil.d("DownLoadUtil", "file down load error");
                    fileDao.insertOrReplace(new LocalFile(url, filePath, 1));
                }
                return null;
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);
                if (file == null) {
                    listener.onError();
                }else {
                    listener.onSuccess(file);
                }
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                listener.onProgerss(values[0], values[1]);
            }

        };

        ExecutorServiceUtil.executeAsyncTask(asyncTask);
        return asyncTask;
    }

    public interface DownLoadListener {
        public void onProgerss(long total, long current);
        public void onSuccess(File file);
        public void onError();
    }
}

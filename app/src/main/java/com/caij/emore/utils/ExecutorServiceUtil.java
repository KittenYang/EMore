package com.caij.emore.utils;

import android.os.AsyncTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Caij on 2016/6/13.
 */
public class ExecutorServiceUtil {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }) ;

    public static void submit(Runnable runnable) {
        EXECUTOR_SERVICE.submit(runnable);
    }

    public static <Params, Progress, Result> void executeAsyncTask(AsyncTask<Params, Progress, Result>  asyncTask) {
        asyncTask.executeOnExecutor(EXECUTOR_SERVICE);
    }
}

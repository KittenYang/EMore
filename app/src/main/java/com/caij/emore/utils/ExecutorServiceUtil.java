package com.caij.emore.utils;

import android.os.AsyncTask;
import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Caij on 2016/6/13.
 */
public class ExecutorServiceUtil {

    public static final ExecutorService LOCAL_EXECUTOR_SERVICE = Executors.newFixedThreadPool(4, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    }) ;

    public static final ExecutorService SEND_MESSAGE_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority( Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    });

    public static void submit(Runnable runnable) {
        LOCAL_EXECUTOR_SERVICE.submit(runnable);
    }

    public static <Params, Progress, Result> void executeAsyncTask(AsyncTask<Params, Progress, Result>  asyncTask) {
        asyncTask.executeOnExecutor(LOCAL_EXECUTOR_SERVICE);
    }
}

package com.caij.emore.utils;

import android.os.AsyncTask;
import android.os.Process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Caij on 2016/6/13.
 */
public class ExecutorServicePool {

    public static final java.util.concurrent.ExecutorService SEND_MESSAGE_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority( Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    });

}

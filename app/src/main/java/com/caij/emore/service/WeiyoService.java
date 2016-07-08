package com.caij.emore.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.caij.emore.service.manager.PublishWeiboManager;
import com.caij.emore.service.manager.UnReadMessageManager;

/**
 * Created by Caij on 2016/7/2.
 * 用于读取未读消息和发布微博
 */
public class WeiyoService extends Service {

    private static final int SERVICE_ID = 5587;

    private PublishWeiboManager mPublishWeiboManager;
    private UnReadMessageManager mUnReadMessageManager;

    public static void start(Context context) {
        Intent intent = new Intent(context, WeiyoService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, WeiyoService.class);
        context.stopService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT < 18) {
            startForeground(SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(SERVICE_ID, new Notification());
        }

        mPublishWeiboManager = PublishWeiboManager.getInstance();
        mUnReadMessageManager = UnReadMessageManager.getInstance();

        mPublishWeiboManager.onCreateManager(this);
        mUnReadMessageManager.onCreateManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPublishWeiboManager.reset();
        mUnReadMessageManager.reset();
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();
            startForeground(SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }
}

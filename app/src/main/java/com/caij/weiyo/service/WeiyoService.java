package com.caij.weiyo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.bean.PublishBean;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.source.PublishWeiboSource;
import com.caij.weiyo.source.server.ServerPublishWeiboSourceImp;
import com.caij.weiyo.utils.PublishWeiboUtil;
import com.caij.weiyo.utils.ToastUtil;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Caij on 2016/7/2.
 * 用于读取未读消息和发布微博
 */
public class WeiyoService extends Service {

    private static final int SERVICE_ID = 5587;
    private static final int PUBLISH_WEIBO_NOTIFICATION_ID = 1000;

    Observable<PublishBean> mPublishWeiboObservable;
    PublishWeiboSource mPublishWeiboSource;
    NotificationManager mNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(SERVICE_ID, new Notification());
        }

        mPublishWeiboSource = new ServerPublishWeiboSourceImp();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mPublishWeiboObservable = PublishWeiboUtil.registPublishEvent();
        mPublishWeiboObservable.subscribe(new Action1<PublishBean>() {
            @Override
            public void call(PublishBean publishBean) {
                publishWeibo(publishBean);
            }
        });

        //在未启动的状态下通过启动Intent赋值
        PublishBean publishBean = (PublishBean) intent.getSerializableExtra(Key.OBJ);
        if (publishBean != null) {
            publishWeibo(publishBean);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void publishWeibo(final PublishBean publishBean) {
        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.publish_backgroud));
        notificationBuilder.setContentText(publishBean.getText());
        notificationBuilder.setSmallIcon(R.mipmap.statusbar_ic_sending);
        Notification notification = notificationBuilder.getNotification();
        mNotificationManager.notify(PUBLISH_WEIBO_NOTIFICATION_ID, notification);
        ToastUtil.show(this, R.string.publish_backgroud);
        Account account = UserPrefs.get().getAccount();
        mPublishWeiboSource.publishWeiboOfMultiImage(account.getWeiyoToken().getAccess_token()
                , account.getWeicoToken().getAccess_token(),
                publishBean.getText(), publishBean.getPics())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Weibo>() {
            @Override
            public void onCompleted() {
                Notification.Builder notificationBuilder = new Notification.Builder(WeiyoService.this);
                notificationBuilder.setContentTitle(getString(R.string.publish_success));
                notificationBuilder.setContentText(getString(R.string.publish_success_hint));
                notificationBuilder.setSmallIcon(R.mipmap.statusbar_ic_send_success);
                Notification notification = notificationBuilder.getNotification();
                mNotificationManager.notify(PUBLISH_WEIBO_NOTIFICATION_ID, notification);
            }

            @Override
            public void onError(Throwable e) {
                Notification.Builder notificationBuilder = new Notification.Builder(WeiyoService.this);
                notificationBuilder.setContentTitle(getString(R.string.publish_fail));
                notificationBuilder.setContentText(getString(R.string.publish_fail_hint));
                notificationBuilder.setSmallIcon(R.mipmap.statusbar_ic_send_fail);
                Notification notification = notificationBuilder.getNotification();
                mNotificationManager.notify(PUBLISH_WEIBO_NOTIFICATION_ID, notification);
            }

            @Override
            public void onNext(Weibo weibo) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        PublishWeiboUtil.unregistPublishEvent(mPublishWeiboObservable);
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

    }
}

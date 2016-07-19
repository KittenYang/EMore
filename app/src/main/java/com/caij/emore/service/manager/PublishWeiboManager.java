package com.caij.emore.service.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.PublishWeiboManagerPresent;
import com.caij.emore.present.imp.PublishWeiboManagerPresentImp;
import com.caij.emore.present.view.PublishServiceView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.utils.CacheUtils;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/7.
 */
public class PublishWeiboManager extends IManager implements PublishServiceView {

    private static final int PUBLISH_WEIBO_NOTIFICATION_ID = 1000;

    private static PublishWeiboManager inst = new PublishWeiboManager();


    NotificationManager mNotificationManager;
    PublishWeiboManagerPresent mPublishWeiboManagerPresent;

    public static PublishWeiboManager getInstance() {
        return inst;
    }

    private PublishWeiboManager(){

    }

    @Override
    protected void doOnCreate() {
        mPublishWeiboManagerPresent = new PublishWeiboManagerPresentImp(new ServerWeiboSource(), this);
        mPublishWeiboManagerPresent.onCreate();
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void reset() {
        mPublishWeiboManagerPresent.onDestroy();
    }

    private void notifyPublishNotification(PublishBean publishBean) {
        Notification.Builder notificationBuilder = new Notification.Builder(ctx);
        notificationBuilder.setContentTitle(ctx.getString(R.string.publish_backgroud));
        notificationBuilder.setContentText(publishBean.getText());
        notificationBuilder.setSmallIcon(R.mipmap.statusbar_ic_sending);
        Notification notification = notificationBuilder.getNotification();
        mNotificationManager.notify(PUBLISH_WEIBO_NOTIFICATION_ID, notification);
        ToastUtil.show(ctx, R.string.publish_backgroud);
    }

    private void notifyPublishSuccessNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(ctx);
        notificationBuilder.setContentTitle(ctx.getString(R.string.publish_success));
        notificationBuilder.setContentText(ctx.getString(R.string.publish_success_hint));
        notificationBuilder.setSmallIcon(R.mipmap.statusbar_ic_send_success);
        Notification notification = notificationBuilder.getNotification();
        mNotificationManager.notify(PUBLISH_WEIBO_NOTIFICATION_ID, notification);
    }

    private void notifyPublishFailNotification() {
        Notification.Builder notificationBuilder = new Notification.Builder(ctx);
        notificationBuilder.setContentTitle(ctx.getString(R.string.publish_fail));
        notificationBuilder.setContentText(ctx.getString(R.string.publish_fail_hint));
        notificationBuilder.setSmallIcon(R.mipmap.statusbar_ic_send_fail);
        Notification notification = notificationBuilder.getNotification();
        mNotificationManager.notify(PUBLISH_WEIBO_NOTIFICATION_ID, notification);
    }

    @Override
    public void onPublishStart(PublishBean publishBean) {
        notifyPublishNotification(publishBean);
    }

    @Override
    public void onPublishFail() {
        notifyPublishFailNotification();
    }

    @Override
    public void onPublishSuccess(Weibo weibo) {
        notifyPublishSuccessNotification();
    }

    @Override
    public Context getContent() {
        return ctx;
    }
}

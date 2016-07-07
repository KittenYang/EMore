package com.caij.weiyo.service.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.bean.PublishBean;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.utils.ServerEventUtil;
import com.caij.weiyo.utils.ToastUtil;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/7.
 */
public class PublishWeiboManager extends IManager {

    private static final int PUBLISH_WEIBO_NOTIFICATION_ID = 1000;

    private static PublishWeiboManager inst = new PublishWeiboManager();

    private ServerWeiboSource mPublishWeiboSource;
    private CompositeSubscription mCompositeSubscription;
    NotificationManager mNotificationManager;
    Observable<PublishBean> mPublishWeiboObservable;

    public static PublishWeiboManager getInstance() {
        return inst;
    }

    private PublishWeiboManager(){

    }

    @Override
    protected void doOnCreate() {
        mPublishWeiboSource = new ServerWeiboSource();
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mCompositeSubscription = new CompositeSubscription();
        mPublishWeiboObservable = ServerEventUtil.registPublishEvent();
        mPublishWeiboObservable.subscribe(new Action1<PublishBean>() {
            @Override
            public void call(PublishBean publishBean) {
                publishWeibo(publishBean);
            }
        });
    }

    @Override
    public void reset() {
        ServerEventUtil.unregistPublishEvent(mPublishWeiboObservable);
        mCompositeSubscription.clear();
    }

    private void publishWeibo(final PublishBean publishBean) {
        if (publishBean.getPics().size() == 1) {
            publishWeiboOngeImage(publishBean);
        }else {
            publishWeiboMuImage(publishBean);
        }
    }
    private void publishWeiboOngeImage(final PublishBean publishBean) {
        notifyPublishNotification(publishBean);
        Account account = UserPrefs.get().getAccount();
        Subscription subscription = mPublishWeiboSource.publishWeiboOfOneImage(account.getWeiyoToken().getAccess_token(),
                publishBean.getText(), publishBean.getPics().get(0))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {
                        notifyPublishSuccessNotification();
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyPublishFailNotification();
                    }

                    @Override
                    public void onNext(Weibo weibo) {

                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private void publishWeiboMuImage(final PublishBean publishBean) {
        notifyPublishNotification(publishBean);
        Account account = UserPrefs.get().getAccount();
        Subscription subscription = mPublishWeiboSource.publishWeiboOfMultiImage(account.getWeiyoToken().getAccess_token()
                , account.getWeicoToken().getAccess_token(),
                publishBean.getText(), publishBean.getPics())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weibo>() {
                    @Override
                    public void onCompleted() {
                        notifyPublishSuccessNotification();
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyPublishFailNotification();
                    }

                    @Override
                    public void onNext(Weibo weibo) {

                    }
                });
        mCompositeSubscription.add(subscription);
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
}

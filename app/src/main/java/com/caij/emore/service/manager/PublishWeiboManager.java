package com.caij.emore.service.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.PublishBean;
import com.caij.emore.manager.imp.DraftManagerImp;
import com.caij.emore.manager.imp.StatusManagerImp;
import com.caij.emore.manager.imp.StatusUploadImageManagerImp;
import com.caij.emore.database.bean.Status;
import com.caij.emore.present.PublishWeiboManagerPresent;
import com.caij.emore.present.imp.PublishStatusManagerPresentImp;
import com.caij.emore.remote.imp.StatusApiImp;
import com.caij.emore.ui.view.PublishServiceView;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.ui.fragment.DraftFragment;
import com.caij.emore.utils.ToastUtil;

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
        mPublishWeiboManagerPresent = new PublishStatusManagerPresentImp(new StatusApiImp(),
                new StatusManagerImp(), new DraftManagerImp(), new StatusUploadImageManagerImp(), this);
        mPublishWeiboManagerPresent.onCreate();
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void reset() {
        mPublishWeiboManagerPresent.onDestroy();
        mNotificationManager.cancelAll();
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
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeMainActivity(new ComponentName(ctx, MainActivity.class));
        intents[1] = DefaultFragmentActivity.starFragmentV4(ctx, ctx.getString(R.string.draft_box),
                DraftFragment.class, null);
        PendingIntent pendingIntent = PendingIntent.getActivities(ctx,  -1, intents, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setContentText(ctx.getString(R.string.publish_fail_hint));
        notificationBuilder.setSmallIcon(R.mipmap.statusbar_ic_send_fail);
        notificationBuilder.setAutoCancel(true);
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
    public void onPublishSuccess(Status weibo) {
        notifyPublishSuccessNotification();
    }

    @Override
    public Context getContent() {
        return ctx;
    }

    @Override
    public void onAuthenticationError() {

    }

    @Override
    public void onDefaultLoadError() {

    }

    @Override
    public void showHint(int stringId) {

    }

    @Override
    public void showHint(String stringId) {

    }

    @Override
    public void showDialogLoading(boolean isShow, int hintStringId) {

    }

    @Override
    public void showDialogLoading(boolean isShow) {

    }
}

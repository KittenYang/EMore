package com.caij.emore.service.manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.caij.emore.AppSettings;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.present.UnReadMessageManagerPresent;
import com.caij.emore.present.imp.UnReadMessageManagerPresentImp;
import com.caij.emore.present.view.UnReadMessageManagerPresentView;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.local.LocalMessageSource;
import com.caij.emore.source.server.ServerMessageSource;
import com.caij.emore.ui.activity.FriendshipActivity;
import com.caij.emore.ui.activity.UserInfoActivity;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.EventUtil;
import com.caij.emore.utils.SystemUtil;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Caij on 2016/7/7.
 */
public class UnReadMessageManager extends IManager implements UnReadMessageManagerPresentView {

    private static final String ACTION_SENDING_HEARTBEAT = "com.caij.weiyo.schedule.action";
    private static final int WEIBI_MENTION_NOTIFICATION_ID = 2000;
    private static final int FOLLOWER_NOTIFICATION_ID = 2001;
    private static final int COMMENT_NOTIFICATION_ID = 2002;
    private static final int MESSAGE_NOTIFICATION_ID = 2003;
    private static final int COMMENT_MENTION_NOTIFICATION_ID = 2004;
    private static final int ATTITUDE_NOTIFICATION_ID = 2005;

    private static UnReadMessageManager inst = new UnReadMessageManager();

    private AlarmManager mAlarmManager;
    private PendingIntent pendingIntent;
    private NotificationManager mNotificationManager;

    private UnReadMessageManagerPresent mUnReadMessageManagerPresent;

    public static UnReadMessageManager getInstance() {
        return inst;
    }

    @Override
    protected void doOnCreate() {
        mAlarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        mUnReadMessageManagerPresent = new UnReadMessageManagerPresentImp(new ServerMessageSource(), new LocalMessageSource(), this);
        mUnReadMessageManagerPresent.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SENDING_HEARTBEAT);
        ctx.registerReceiver(scheduleReceiver, intentFilter);
        if (UserPrefs.get().getEMoreToken() != null) { //只有当前用户不为空的时候才定时拉取数据
            scheduleHeartbeat(AppSettings.getMessageIntervalValue(ctx));
        }
    }

    @Override
    public void reset() {
        ctx.unregisterReceiver(scheduleReceiver);
        cancelHeartbeatTimer();
        mUnReadMessageManagerPresent.onDestroy();
    }

    @Override
    public void onIntervalMillisUpdate() {
        scheduleHeartbeat(AppSettings.getMessageIntervalValue(ctx));
    }

    @Override
    public void notifyMessage(UnReadMessage serverUnReadMessage, UnReadMessage localUnReadMessage) {
        if (AppSettings.isNotifyEnable(ctx)) {

            if (AppSettings.isNotifyFollowerEnable(ctx)
                    && serverUnReadMessage.getFollower() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getFollower() - localUnReadMessage.getFollower() > 0)) {
                String text = serverUnReadMessage.getFollower() + "新粉丝";
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getFollower(),
                        R.mipmap.statusbar_ic_follower_small, FOLLOWER_NOTIFICATION_ID);
            }

            if (AppSettings.isNotifyCommentEnable(ctx) && serverUnReadMessage.getCmt() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getCmt() - localUnReadMessage.getCmt() > 0)) {
                String text = serverUnReadMessage.getCmt() + "条新评论";
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getCmt(),
                        R.mipmap.statusbar_ic_comment_small, COMMENT_NOTIFICATION_ID);
            }

            if (AppSettings.isNotifyDmEnable(ctx) && serverUnReadMessage.getDm_single() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getDm_single() - localUnReadMessage.getDm_single() > 0)) {
                String text = serverUnReadMessage.getDm_single() + "条私信";
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getDm_single(),
                        R.mipmap.statusbar_ic_dm_small, MESSAGE_NOTIFICATION_ID);
            }

            if (AppSettings.isNotifyWeiboMentionEnable(ctx) && serverUnReadMessage.getMention_status() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getMention_status() - localUnReadMessage.getMention_status() > 0)) {
                String text = serverUnReadMessage.getMention_status() + ctx.getString(R.string.weibo_mention_count_hint);
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getMention_status(),
                        R.mipmap.statusbar_ic_mention_small, WEIBI_MENTION_NOTIFICATION_ID);
            }

            if (AppSettings.isNotifyWeiboMentionEnable(ctx) && serverUnReadMessage.getMention_cmt() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getMention_cmt() - localUnReadMessage.getMention_cmt() > 0)) {
                String text = serverUnReadMessage.getMention_cmt() + ctx.getString(R.string.comment_mention_count_hint);
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getMention_status(),
                        R.mipmap.statusbar_ic_mention_small, COMMENT_MENTION_NOTIFICATION_ID);
            }

            if (AppSettings.isNotifyAttitudeEnable(ctx) && serverUnReadMessage.getAttitude() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getAttitude() - localUnReadMessage.getAttitude() > 0)) {
                String text = serverUnReadMessage.getAttitude() + "新点赞";
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getMention_status(),
                        R.mipmap.timeline_icon_unlike, ATTITUDE_NOTIFICATION_ID);
            }

        }
    }

    private void scheduleHeartbeat(long seconds){
        seconds = 3 * 1000;
        cancelHeartbeatTimer();

        if (pendingIntent == null) {
            Intent intent = new Intent(ACTION_SENDING_HEARTBEAT);
            pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
            if (pendingIntent == null) {
                return;
            }
        }
        //这里用setInexactRepeating 置一个重复闹钟的不精确版本，它相对而言更节能一些，
        // 因为系统可能会将几个差不多的闹钟合并为一个来执行，减少设备的唤醒次数。

        //这里用setInexactRepeating需要多一步操作， 在设置里面更改消息时间就要重新设置 intervalMillis ，需要通过事件总线通知
        // 如果用set 只需要在scheduleReceiver中重新设置定时闹钟就行
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + seconds, seconds, pendingIntent);
    }

    private void cancelHeartbeatTimer() {
        if (pendingIntent == null) {
            return;
        }
        mAlarmManager.cancel(pendingIntent);
    }

    private BroadcastReceiver scheduleReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_SENDING_HEARTBEAT)) {
                LogUtil.d(UnReadMessageManager.this, "scheduleReceiver onReceive");
                mUnReadMessageManagerPresent.loadUnReadMessage();
            }
        }
    };

    private void notifyNotification(String title, String contentText, int num, int drawable, int id) {
        Notification.Builder notificationBuilder = new Notification.Builder(ctx);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(contentText);
        notificationBuilder.setSmallIcon(drawable);
        notificationBuilder.setNumber(num);
//        notificationBuilder.setContentIntent(intent);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher));
        Notification notification = notificationBuilder.getNotification();
        mNotificationManager.notify(id, notification);
    }

}

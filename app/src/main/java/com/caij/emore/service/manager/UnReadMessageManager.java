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

import com.caij.emore.AppSettings;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.UnreadMessageCount;
import com.caij.emore.source.MessageSource;
import com.caij.emore.source.server.ServerMessageSource;
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
public class UnReadMessageManager extends IManager {

    private static final String ACTION_SENDING_HEARTBEAT = "com.caij.weiyo.schedule.action";
    private static final int MENTION_NOTIFICATION_ID = 2000;
    private static final int FOLLOWER_NOTIFICATION_ID = 2001;
    private static final int COMMENT_NOTIFICATION_ID = 2002;
    private static final int MESSAGE_NOTIFICATION_ID = 2003;

    private static UnReadMessageManager inst = new UnReadMessageManager();

    private AlarmManager mAlarmManager;
    private PendingIntent pendingIntent;
    private Observable<Object> mIntervalMillisUpdateObservable;
    private MessageSource mServerMessageSource;
    private NotificationManager mNotificationManager;
    private CompositeSubscription mCompositeSubscription;

    public static UnReadMessageManager getInstance() {
        return inst;
    }

    @Override
    protected void doOnCreate() {
        mAlarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mCompositeSubscription = new CompositeSubscription();
        mIntervalMillisUpdateObservable = EventUtil.registIntervalMillisUpdateEvent();
        mServerMessageSource = new ServerMessageSource();
        mIntervalMillisUpdateObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        onIntervalMillisUpdate();
                    }
                });
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
        EventUtil.unregistIntervalMillisUpdateEvent(mIntervalMillisUpdateObservable);
        mCompositeSubscription.clear();
    }

    public void onIntervalMillisUpdate() {
        scheduleHeartbeat(AppSettings.getMessageIntervalValue(ctx));
    }

    private void scheduleHeartbeat(long seconds){
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
                loadUnReadMessage();
            }
        }
    };

    private void loadUnReadMessage() {
        if (!SystemUtil.isNetworkAvailable(ctx)) {
            return;
        }
        AccessToken accessToken = UserPrefs.get().getEMoreToken();
        if (accessToken != null) {
            mServerMessageSource.getUnReadMessage(accessToken.getAccess_token(), Long.parseLong(accessToken.getUid()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UnreadMessageCount>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(UnreadMessageCount unreadMessage) {
                            notifyMessage(unreadMessage);
                        }
                    });
        }
    }

    private void notifyMessage(UnreadMessageCount unreadMessage) {
        if (AppSettings.isNotifyEnable(ctx)) {
            notifyMention(unreadMessage);

            if (AppSettings.isNotifyFollowerEnable(ctx) && unreadMessage.getFollower() > 0) {
                String text = unreadMessage.getFollower() + "新粉丝";
                notifyNotification(ctx.getString(R.string.app_name), text, unreadMessage.getFollower(),
                        R.mipmap.statusbar_ic_follower_small, FOLLOWER_NOTIFICATION_ID);
            }

            if (AppSettings.isNotifyCommentEnable(ctx) && unreadMessage.getCmt() > 0) {
                String text = unreadMessage.getCmt() + "条新评论";
                notifyNotification(ctx.getString(R.string.app_name), text, unreadMessage.getCmt(),
                        R.mipmap.statusbar_ic_comment_small, COMMENT_NOTIFICATION_ID);
            }

            if (AppSettings.isNotifyDmEnable(ctx) && unreadMessage.getDm() > 0) {
                String text = unreadMessage.getDm() + "条私信";
                notifyNotification(ctx.getString(R.string.app_name), text, unreadMessage.getDm(),
                        R.mipmap.statusbar_ic_dm_small, MENTION_NOTIFICATION_ID);
            }
        }
    }

    private void notifyMention(UnreadMessageCount unreadMessage) {
        if ((AppSettings.isNotifyWeiboMentionEnable(ctx) || AppSettings.isNotifyCommentMentionEnable(ctx))
                && (unreadMessage.getMention_status() > 0 || unreadMessage.getMention_cmt() > 0)) {
            StringBuilder stringBuilder = new StringBuilder();
            if (AppSettings.isNotifyWeiboMentionEnable(ctx) && unreadMessage.getMention_status() > 0) {
                stringBuilder.append(unreadMessage.getMention_status()).
                        append(ctx.getString(R.string.weibo_mention_count_hint));
            }

            if (AppSettings.isNotifyWeiboMentionEnable(ctx) && unreadMessage.getMention_cmt() > 0) {
                stringBuilder.append(unreadMessage.getMention_cmt()).
                        append(ctx.getString(R.string.comment_mention_count_hint));
            }

            notifyNotification(ctx.getString(R.string.app_name), stringBuilder.toString(),
                    unreadMessage.getMention_cmt() + unreadMessage.getMention_status(),
                    R.mipmap.statusbar_ic_mention_small, MESSAGE_NOTIFICATION_ID);
        }
    }

    private void notifyNotification(String title, String contentText, int num, int drawable, int id) {
        Notification.Builder notificationBuilder = new Notification.Builder(ctx);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(contentText);
        notificationBuilder.setSmallIcon(drawable);
        notificationBuilder.setNumber(num);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher));
        Notification notification = notificationBuilder.getNotification();
        mNotificationManager.notify(id, notification);
    }

}

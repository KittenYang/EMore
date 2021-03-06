package com.caij.emore.service.manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;

import com.caij.emore.AppSettings;
import com.caij.emore.EventTag;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.manager.imp.NotifyManagerImp;
import com.caij.emore.present.UnReadMessageManagerPresent;
import com.caij.emore.present.imp.UnReadMessageManagerPresentImp;
import com.caij.emore.remote.imp.NotifyApiImp;
import com.caij.emore.ui.view.UnReadMessageManagerPresentView;
import com.caij.emore.service.EMoreService;
import com.caij.emore.ui.activity.CommentsActivity;
import com.caij.emore.ui.activity.DefaultFragmentActivity;
import com.caij.emore.ui.activity.FriendshipActivity;
import com.caij.emore.ui.activity.MainActivity;
import com.caij.emore.ui.activity.MentionActivity;
import com.caij.emore.ui.fragment.AttitudesToMeFragment;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;

/**
 * Created by Caij on 2016/7/7.
 */
public class UnReadMessageManager extends IManager implements UnReadMessageManagerPresentView {

    private static final String ACTION_SENDING_HEARTBEAT = "com.caij.emore.schedule.action";
    private static final int STATUS_MENTION_NOTIFICATION_ID = 2000;
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
        mUnReadMessageManagerPresent = new UnReadMessageManagerPresentImp(UserPrefs.get(ctx).getToken(),
                new NotifyApiImp(), new NotifyManagerImp(), this);
        mUnReadMessageManagerPresent.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SENDING_HEARTBEAT);
        ctx.registerReceiver(scheduleReceiver, intentFilter);

        if (UserPrefs.get(ctx).getToken() != null) {
            scheduleHeartbeat(AppSettings.getMessageIntervalValue(ctx));
        }
    }

    @Override
    public void reset() {
        ctx.unregisterReceiver(scheduleReceiver);
        cancelHeartbeatTimer();
        mUnReadMessageManagerPresent.onDestroy();
        mNotificationManager.cancelAll();
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
                String text = serverUnReadMessage.getFollower() + ctx.getString(R.string.new_followers);
                Intent[] intents = new Intent[2];
                intents[0] = Intent.makeMainActivity(new ComponentName(ctx, MainActivity.class));
                intents[1] = FriendshipActivity.newIntent(ctx, Long.parseLong(UserPrefs.get(ctx).getToken().getUid()));
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getFollower(),
                        R.mipmap.statusbar_ic_follower_small, FOLLOWER_NOTIFICATION_ID, intents);
            }

            if (AppSettings.isNotifyCommentEnable(ctx) && serverUnReadMessage.getCmt() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getCmt() - localUnReadMessage.getCmt() > 0)) {
                String text = serverUnReadMessage.getCmt() + ctx.getString(R.string.new_comment);
                Intent[] intents = new Intent[2];
                intents[0] = Intent.makeMainActivity(new ComponentName(ctx, MainActivity.class));
                intents[1] = new Intent(ctx, CommentsActivity.class);
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getCmt(),
                        R.mipmap.statusbar_ic_comment_small, COMMENT_NOTIFICATION_ID, intents);
            }

            if (AppSettings.isNotifyDmEnable(ctx)
                    && ((serverUnReadMessage.getDm_single() > 0 && (localUnReadMessage == null || serverUnReadMessage.getDm_single() - localUnReadMessage.getDm_single() > 0))
                    || (serverUnReadMessage.getMsgbox() > 0 && (localUnReadMessage == null || serverUnReadMessage.getMsgbox() - localUnReadMessage.getMsgbox() > 0)))) {
                String text = (serverUnReadMessage.getDm_single() + serverUnReadMessage.getMsgbox()) + ctx.getString(R.string.new_dm);
                Intent[] intents = new Intent[1];
                Intent intent = Intent.makeMainActivity(new ComponentName(ctx, MainActivity.class));
                intent.putExtra(Key.ID, Key.MESSAGE_FRAGMENT_TAG);
                intents[0] = intent;
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getDm_single(),
                        R.mipmap.statusbar_ic_dm_small, MESSAGE_NOTIFICATION_ID, intents);
            }

            if (AppSettings.isNotifyWeiboMentionEnable(ctx) && serverUnReadMessage.getMention_status() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getMention_status() - localUnReadMessage.getMention_status() > 0)) {
                String text = serverUnReadMessage.getMention_status() + ctx.getString(R.string.weibo_mention_count_hint);
                Intent[] intents = new Intent[2];
                intents[0] = Intent.makeMainActivity(new ComponentName(ctx, MainActivity.class));
                intents[1] =  new Intent(ctx, MentionActivity.class);
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getMention_status(),
                        R.mipmap.statusbar_ic_mention_small, STATUS_MENTION_NOTIFICATION_ID, intents);
            }

            if (AppSettings.isNotifyCommentMentionEnable(ctx) && serverUnReadMessage.getMention_cmt() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getMention_cmt() - localUnReadMessage.getMention_cmt() > 0)) {
                String text = serverUnReadMessage.getMention_cmt() + ctx.getString(R.string.comment_mention_count_hint);
                Intent[] intents = new Intent[2];
                intents[0] = Intent.makeMainActivity(new ComponentName(ctx, MainActivity.class));
                intents[1] =  new Intent(ctx, MentionActivity.class);
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getMention_status(),
                        R.mipmap.statusbar_ic_mention_small, COMMENT_MENTION_NOTIFICATION_ID, intents);
            }

            if (AppSettings.isNotifyAttitudeEnable(ctx) && serverUnReadMessage.getAttitude() > 0
                    && (localUnReadMessage == null || serverUnReadMessage.getAttitude() - localUnReadMessage.getAttitude() > 0)) {
                String text = serverUnReadMessage.getAttitude() + ctx.getString(R.string.new_attitude);
                Intent[] intents = new Intent[2];
                intents[0] = Intent.makeMainActivity(new ComponentName(ctx, MainActivity.class));
                intents[1] =  DefaultFragmentActivity.starFragmentV4(ctx, ctx.getString(R.string.attitude),
                        AttitudesToMeFragment.class, null);
                notifyNotification(ctx.getString(R.string.app_name), text, serverUnReadMessage.getMention_status(),
                        R.mipmap.timeline_icon_unlike, ATTITUDE_NOTIFICATION_ID, intents);
            }
        }
    }

    private void scheduleHeartbeat(long seconds){
        LogUtil.d(this, "scheduleHeartbeat in time %s ", seconds);
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
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), seconds, pendingIntent);
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

    private void notifyNotification(String title, String contentText, int num, int drawable, int id, Intent[] intents) {
        Notification.Builder notificationBuilder = new Notification.Builder(ctx);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(contentText);
        notificationBuilder.setSmallIcon(drawable);
        notificationBuilder.setNumber(num);
        PendingIntent pendingIntent = PendingIntent.getActivities(ctx,  id, intents, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher));
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        Notification notification = notificationBuilder.getNotification();
        mNotificationManager.notify(id, notification);
    }

    @Override
    public void onAuthenticationError() {
        UserPrefs.get(ctx).deleteUsingAccount();

        RxBus.getDefault().post(EventTag.EVENT_TOKEN_EXPIRED, null);

        EMoreService.stop(ctx);
    }

    @Override
    public void onDefaultLoadError() {

    }

    @Override
    public void showHint(int stringId) {

    }

    @Override
    public void showHint(String string) {

    }

    @Override
    public void showDialogLoading(boolean isShow, int hintStringId) {

    }

    @Override
    public void showDialogLoading(boolean isShow) {

    }
}

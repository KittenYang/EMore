package com.caij.emore.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.caij.emore.AppApplication;
import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.service.manager.ChatManager;
import com.caij.emore.service.manager.PublishWeiboManager;
import com.caij.emore.service.manager.UnReadMessageManager;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxbus.RxBusInterface;

/**
 * Created by Caij on 2016/7/2.
 * 用于读取未读消息和发布微博
 */
public class EMoreService extends Service {

    private static final int SERVICE_ID = 5587;

    private PublishWeiboManager mPublishWeiboManager;
    private UnReadMessageManager mUnReadMessageManager;
    private ChatManager mChatManager;

    private Messenger mServerMessenger;
    private PipeHandler mPipeHandler;

    public static void start(Context context) {
        Intent intent = new Intent(context, EMoreService.class);
        context.startService(intent);
    }

    public static void stop(Context context) {
        Intent intent = new Intent(context, EMoreService.class);
        context.stopService(intent);
    }

    public static void bind(Context context, ServiceConnection serviceConnection) {
        Intent intent = new Intent(context, EMoreService.class);
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public static void unbind(Context context,ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(this, "onBind");
        return mServerMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(this, "onCreate");
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(SERVICE_ID, new Notification());
        }

        mPipeHandler = new PipeHandler();
        mServerMessenger = new Messenger(mPipeHandler);

        mPublishWeiboManager = PublishWeiboManager.getInstance();
        mUnReadMessageManager = UnReadMessageManager.getInstance();
        mChatManager = ChatManager.getInstance();

        mPublishWeiboManager.onCreateManager(this);
        mUnReadMessageManager.onCreateManager(this);
        mChatManager.onCreateManager(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(this, "onDestroy");
        mServerMessenger = null;
        mPipeHandler.onDestroy();
        super.onDestroy();
        mPublishWeiboManager.reset();
        mUnReadMessageManager.reset();
        mChatManager.reset();
        Process.killProcess(Process.myPid());
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

    private static class PipeHandler extends Handler {

        private Messenger mClientMessenger;
        private RxBusInterface.Listener mRxBusListener;

        public PipeHandler() {
            mRxBusListener = new RxBus.ListenerAdapter() {
                @Override
                public void onPost(Object tag, @NonNull Object content) {
                    try {
                        if (tag.equals(Event.EVENT_DRAFT_UPDATE)
                                || tag.equals(Event.EVENT_PUBLISH_WEIBO_SUCCESS)
                                || tag.equals(Event.EVENT_UNREAD_MESSAGE_COMPLETE)
                                || tag.equals(Event.EVENT_HAS_NEW_DM)
                                || tag.equals(Event.EVENT_SEND_MESSAGE_RESULT)) {
                            LogUtil.d(PipeHandler.this,"EMoreService send event " + tag.toString() + " to other event");

                            if (mClientMessenger != null) {
                                PipeEvent pipeEvent = new PipeEvent(tag, content);
                                Message message = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Key.OBJ, pipeEvent);
                                message.setData(bundle);

                                mClientMessenger.send(message);
                            }else {
                                LogUtil.d(PipeHandler.this, "mClientMessenger is null");
                            }
                        }
                    }catch (RemoteException re) {
                        LogUtil.d(PipeHandler.this, "进程通讯异常: RemoteException" + re.getMessage());
                        mClientMessenger = null;
                    } catch (Exception e) {
                        LogUtil.d(PipeHandler.this, "进程通讯异常: Exception" + e.getMessage());
                    }
                }
            };
            RxBus.getDefault().addEventListener(mRxBusListener);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mClientMessenger = msg.replyTo;
            try {
                Bundle bundle = msg.getData();
                PipeEvent pipeEvent = (PipeEvent) bundle.getSerializable(Key.OBJ);
                if (pipeEvent != null) {
                    LogUtil.d(PipeHandler.this, "Service accept event" + pipeEvent.tag.toString());
                    RxBus.getDefault().post(pipeEvent.tag, pipeEvent.content);
                }
            }catch (Exception e) {
                LogUtil.d(this, "handleMessage Exception :" + e.getMessage());
            }
        }

        public void onDestroy() {
            RxBus.getDefault().removeEventListener(mRxBusListener);
            mClientMessenger = null;
        }

    }


}

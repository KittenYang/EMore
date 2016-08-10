package com.caij.emore.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.caij.emore.AppApplication;
import com.caij.emore.BuildConfig;
import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.service.EMoreService;
import com.caij.emore.service.PipeEvent;
import com.caij.emore.utils.db.DBManager;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxbus.RxBusInterface;

import java.lang.reflect.Method;
import java.nio.channels.Pipe;

/**
 * Created by Caij on 2016/7/15.
 */
public class AppUtil {

    public static void resetConfig(Context context, long uid) {
        stop(context);
        start(context, uid);
    }

    public static void stop(Context context) {
        if (DBManager.getDaoSession() != null && DBManager.getDaoSession().getDatabase().isOpen()) {
            DBManager.getDaoSession().getDatabase().close();
        }
        EMoreService.stop(context);
    }

    public static void start(Context context, long uid) {
        DBManager.initDB(context, Key.DB_NAME + uid, BuildConfig.DEBUG);
        if (SystemUtil.isMainProcess(context)) {
            EMoreService.start(context);
            EMoreService.bind(context.getApplicationContext(), new PipeServiceConnection());
        }
    }

    private static class PipeServiceConnection implements ServiceConnection {

        private Messenger mClientMessenger;
        private Messenger mServerMessenger;
        private RxBusInterface.Listener mRxBusListener;

        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            mServerMessenger = new Messenger(service);
            mClientMessenger = new Messenger(new PipeHandler());

            //这里需要发送一个空的message service 否则 service没有客户端的代理 调用不了客户端
            Message message = Message.obtain();
            senMessage(message);

            mRxBusListener = new RxBus.ListenerAdapter() {
                @Override
                public void onPost(Object tag, @NonNull Object content) {
                    if (tag.equals(Event.PUBLISH_WEIBO)
                            || tag.equals(Event.SEND_MESSAGE_EVENT)) {
                        LogUtil.d(PipeServiceConnection.this, "Client post event " + tag.toString() + " to other process");
                        PipeEvent pipeEvent = new PipeEvent(tag, content);
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Key.OBJ, pipeEvent);
                        message.setData(bundle);
                        senMessage(message);
                    }
                }
            };
            RxBus.getDefault().addEventListener(mRxBusListener);
        }

        private void senMessage(Message message) {
            try {
                message.replyTo = mClientMessenger;
                mServerMessenger.send(message);
            }catch (RemoteException re) {
                LogUtil.d(PipeServiceConnection.this, "进程通讯异常:" + re.getMessage());
                EMoreService.bind(AppApplication.getInstance(), new PipeServiceConnection());
            }catch (Exception e) {
                LogUtil.d(PipeServiceConnection.this, "进程通讯异常:" + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            RxBus.getDefault().removeEventListener(mRxBusListener);
            mClientMessenger = null;
            mServerMessenger = null;
        }
    }

    private static class PipeHandler extends Handler {

        public PipeHandler() {

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Bundle bundle = msg.getData();
                PipeEvent pipeEvent = (PipeEvent) bundle.getSerializable(Key.OBJ);
                if (pipeEvent != null) {
                    LogUtil.d(PipeHandler.this, "Client accept event " + pipeEvent.tag.toString());
                    RxBus.getDefault().post(pipeEvent.tag, pipeEvent.content);
                }
            }catch (Exception e) {
                LogUtil.d(this, "handleMessage Exception :" + e.getMessage());
            }
        }

    }

}

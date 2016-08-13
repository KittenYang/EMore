package com.caij.emore.utils;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.caij.emore.AppApplication;
import com.caij.emore.BuildConfig;
import com.caij.emore.Event;
import com.caij.emore.Key;
import com.caij.emore.database.dao.DaoSession;
import com.caij.emore.service.EMoreService;
import com.caij.emore.service.Pipe;
import com.caij.emore.service.PipeEvent;
import com.caij.emore.utils.db.DBManager;
import com.caij.emore.utils.rxbus.RxBus;
import com.caij.emore.utils.rxbus.RxBusInterface;

import java.util.ArrayDeque;

/**
 * Created by Caij on 2016/7/15.
 */
public class Init {

    private static Init instance = new Init();

    private Pipe mEMoreServicePipe;

    private RxBusInterface.Listener mRxBusListener;

    private ArrayDeque<PipeEvent> mRestoreArrayDeque; //用于发送异常重新连接保存的PipeEvent

    private Init(){
    }

    public static Init getInstance() {
        return instance;
    }

    public void reset(Context context, long uid) {
        stop(context);
        start(context, uid);
    }

    public void stop(Context context) {
        DBManager.close();
        if (SystemUtil.isMainProcess(context)) {
            stopOtherForMainProcess(context);
        }
    }

    private void stopOtherForMainProcess(Context context) {
        if (mEMoreServicePipe != null) {
            mEMoreServicePipe.close(context);
        }
        EMoreService.stop(context);
        RxBus.getDefault().removeEventListener(mRxBusListener);
    }

    private void start(Context context, long uid) {
        DBManager.initDB(context, Key.DB_NAME + uid, BuildConfig.DEBUG);
        if (SystemUtil.isMainProcess(context)) {
            initOtherForMainProcess(context);
        }
    }

    private void initOtherForMainProcess(Context context) {
        EMoreService.start(context);
        initEMoreServicePipe(context);
        mRestoreArrayDeque = new ArrayDeque<>();

        mRxBusListener = new RxBus.ListenerAdapter() {
            @Override
            public void onPost(Object tag, @NonNull Object content) {
                if (tag.equals(Event.PUBLISH_WEIBO)
                        || tag.equals(Event.SEND_MESSAGE_EVENT)
                        || tag.equals(Event.INTERVAL_MILLIS_UPDATE)) {
                    LogUtil.d(Init.this, "Client post event " + tag.toString() + " to other process");
                    senEventToEMoreServiceProcess(tag, content);
                }
            }
        };
        RxBus.getDefault().addEventListener(mRxBusListener);
    }

    private void initEMoreServicePipe(Context context) {
        if (mEMoreServicePipe != null) {
            mEMoreServicePipe.close(context);
        }
        mEMoreServicePipe = new Pipe();
        mEMoreServicePipe.open(context, new Pipe.PipeListener() {
            @Override
            public void onAcceptEvent(PipeEvent pipeEvent) {
                RxBus.getDefault().post(pipeEvent.tag, pipeEvent.content);
            }

            @Override
            public void onOpen() {
                PipeEvent pipeEvent;
                while ((pipeEvent = mRestoreArrayDeque.poll()) != null) {
                    LogUtil.d(Init.this, "has Restore event %s", pipeEvent.tag);
                    senEventToEMoreServiceProcess(pipeEvent);
                }
            }

            @Override
            public void onClose() {

            }
        });
    }

    private void senEventToEMoreServiceProcess(PipeEvent pipeEvent) {
        if (mEMoreServicePipe != null) {
            try {
                mEMoreServicePipe.senEvent(pipeEvent);
            }catch (RemoteException re) {
                mRestoreArrayDeque.offer(pipeEvent);
                LogUtil.d(Init.this, "进程通讯异常: RemoteException" + re.getMessage());
                initEMoreServicePipe(AppApplication.getInstance());
            }catch (Exception e) {
                LogUtil.d(Init.this, "进程通讯异常: Exception" + e.getMessage());
            }
        }
    }

    private void senEventToEMoreServiceProcess(Object tag, @NonNull Object content) {
        PipeEvent pipeEvent = new PipeEvent(tag, content);
        senEventToEMoreServiceProcess(pipeEvent);
    }

}

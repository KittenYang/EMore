package com.caij.emore.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.caij.emore.Key;
import com.caij.emore.utils.LogUtil;


/**
 * Created by Caij on 2016/8/13.
 */
public class Pipe {

    private PipeServiceConnection mEMoreServiceConnection;

    public Pipe() {

    }

    public void open(Context context) {
        open(context, null);
    }

    public void open(Context context, PipeListener pipeListener) {
        Messenger clientMessenger = new Messenger(new PipeHandler(pipeListener));
        mEMoreServiceConnection =  new PipeServiceConnection(clientMessenger, pipeListener);
        EMoreService.bind(context.getApplicationContext(), mEMoreServiceConnection);
    }

    public void senEvent(PipeEvent pipeEvent) throws RemoteException {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Key.OBJ, pipeEvent);
        message.setData(bundle);
        mEMoreServiceConnection.senMessage(message);
    }

    public void close(Context context) {
        if (mEMoreServiceConnection != null) {
            EMoreService.unbind(context.getApplicationContext(), mEMoreServiceConnection);
        }
        mEMoreServiceConnection = null;
    }

    private static class PipeServiceConnection implements ServiceConnection {

        private Messenger mClientMessenger;
        private Messenger mServerMessenger;
        private PipeListener mPipeListener;

        private PipeServiceConnection(Messenger clientMessenger, PipeListener pipeListener) {
            mClientMessenger = clientMessenger;
            mPipeListener = pipeListener;
        }

        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            LogUtil.d(this, "onServiceConnected");
            mServerMessenger = new Messenger(service);

            //这里需要发送一个空的message service 否则 service没有客户端的代理 调用不了客户端
            Message message = Message.obtain();
            try {
                senMessage(message);
            } catch (RemoteException e) {
                LogUtil.d(PipeServiceConnection.this, "onServiceConnected sen init message error");
            }

            if (mPipeListener != null) {
                mPipeListener.onOpen();
            }
        }

        private void senMessage(Message message) throws RemoteException  {
            message.replyTo = mClientMessenger;
            mServerMessenger.send(message);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(this, "onServiceDisconnected");
            mPipeListener.onClose();
        }
    }


    private static class PipeHandler extends Handler {

        private PipeListener mAcceptEventListener;

        public PipeHandler(PipeListener acceptEventListener) {
            mAcceptEventListener = acceptEventListener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Bundle bundle = msg.getData();
                PipeEvent pipeEvent = (PipeEvent) bundle.getSerializable(Key.OBJ);
                if (pipeEvent != null && mAcceptEventListener!= null) {
                    LogUtil.d(PipeHandler.this, "Client accept event " + pipeEvent.tag.toString());
                    mAcceptEventListener.onAcceptEvent(pipeEvent);
                }
            }catch (Exception e) {
                LogUtil.d(this, "handleMessage Exception :" + e.getMessage());
            }
        }
    }

    public static interface PipeListener {
        void onAcceptEvent(PipeEvent pipeEvent);
        void onOpen();
        void onClose();
    }

}

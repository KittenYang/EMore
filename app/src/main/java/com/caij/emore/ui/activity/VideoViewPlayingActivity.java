package com.caij.emore.ui.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baidu.cyberplayer.core.BMediaController;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionWithParamListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.baidu.cyberplayer.subtitle.SubtitleManager;
import com.baidu.cyberplayer.subtitle.utils.SubtitleError;
import com.baidu.cyberplayer.subtitle.utils.SubtitleErrorCallback;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.VideoInfo;
import com.caij.emore.present.VideoPlayPresent;
import com.caij.emore.present.imp.VideoPlayPresentImp;
import com.caij.emore.present.view.VideoPlayView;
import com.caij.emore.source.local.LocalVideoSource;
import com.caij.emore.source.server.ServerVideoSource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class VideoViewPlayingActivity extends BaseActivity implements OnPreparedListener,
        OnCompletionListener,
        OnErrorListener,
        OnInfoListener,
        OnPlayingBufferCacheListener,
        OnCompletionWithParamListener, VideoPlayView {
    private final String TAG = "VideoViewPlayingActivity";

    /**
     * 您的AK
     * 请到http://console.bce.baidu.com/iam/#/iam/accesslist获取
     */
    public static final String AK = "6770e0af7eb94cd2a4e7247a51b9522d";
    public static final String SK = "8cfa8441afde4170811317445bb11d53";

    private String mVideoSource = null;

    private BVideoView mVV = null;

    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;

    private final Object SYNC_Playing = new Object();

    private static final int UI_EVENT_PLAY = 0;

    private WakeLock mWakeLock = null;
    private static final String POWER_LOCK = "VideoViewPlayingActivity";
    private RelativeLayout mViewHolder;

    private VideoPlayPresent mVideoPlayPresent;

    /**
     * 播放状态
     */
    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

    public static Intent newIntent(Context context, long weiboId) {
        Intent intent = new Intent(context, VideoViewPlayingActivity.class);
        intent.putExtra(Key.ID, weiboId);
        return intent;
    }

    class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UI_EVENT_PLAY:
                    /**
                     * 如果已经播放了，等待上一次播放结束
                     */
                    if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                        synchronized (SYNC_Playing) {
                            try {
                                SYNC_Playing.wait();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    /**
                     * 设置播放url
                     */

//                    mVV.setLogLevel(4);
                    mVV.setVideoPath(mVideoSource);
                    // mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);

                    /**
                     * 显示或者隐藏缓冲提示 
                     */
                    mVV.showCacheInfo(true);
                    /**
                     * 开始播放
                     */
                    mVV.start();

                    mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
                    break;

                default:
                    break;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_play);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);

//        mVideoSource = getIntent().getStringExtra(Key.ID);
        mVideoSource = "http://us.sinaimg.cn/004bLkEAjx073zAUlTBu05040100fLWS0k01.mp4?KID=unistore,video&Expires=1469621642&ssig=SNbTmlPo%2B2";

        initUI();
        /**
         * 开启后台事件处理线程
         */
        mHandlerThread = new HandlerThread("event handler thread",
                Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());

        long weiboId = getIntent().getLongExtra(Key.ID, -1);
        mVideoPlayPresent = new VideoPlayPresentImp(weiboId, new ServerVideoSource(), new LocalVideoSource(), this);
        mVideoPlayPresent.onCreate();
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        mViewHolder = (RelativeLayout) findViewById(R.id.view_holder);

        /**
         * 设置ak
         */
        BVideoView.setAK(AK);
        mVV = new BVideoView(this);
        mVV.setLogLevel(4);
        mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mVV.setRetainLastFrame(true);

        mViewHolder.addView(mVV);

        /**
         * 注册listener
         */
        mVV.setOnPreparedListener(this);
        mVV.setOnCompletionListener(this);
        mVV.setOnCompletionWithParamListener(this);
        mVV.setOnErrorListener(this);
        mVV.setOnInfoListener(this);


        /**
         * 设置解码模式
         */
        mVV.setDecodeMode(BVideoView.DECODE_SW);
        mVV.selectResolutionType(BVideoView.RESOLUTION_TYPE_AUTO);
    }


    @Override
    public void onGetVideoInfoSuccess(VideoInfo videoInfo) {
        mEventHandler.sendEmptyMessage(UI_EVENT_PLAY);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        /**
         * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
         */
        if (mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            //when scree lock,paus is good select than stop
            // don't stop pause
            // mVV.stopPlayback();
            mVV.pause();
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (null != mWakeLock && (!mWakeLock.isHeld())) {
            mWakeLock.acquire();
        }
        // 发起一次播放任务,当然您不一定要在这发起
        if (!mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            mVV.resume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
        if (mVV.isPlaying() && (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            // don't stop pause
            // mVV.stopPlayback();
            mVV.pause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE)) {
            mVV.stopPlayback();
        }

        /**
         * 结束后台事件处理线程
         */
        mHandlerThread.quit();
        mVideoPlayPresent.onDestroy();
    }

    @Override
    public boolean onInfo(int what, int extra) {
        // TODO Auto-generated method stub
        switch (what) {
            /**
             * 开始缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_START:


                break;
            /**
             * 结束缓冲
             */
            case BVideoView.MEDIA_INFO_BUFFERING_END:

                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
     */
    @Override
    public void onPlayingBufferCache(int percent) {
        // TODO Auto-generated method stub

    }

    /**
     * 播放出错
     */
    @Override
    public boolean onError(int what, int extra) {
        // TODO Auto-generated method stub
        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
        return true;
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion() {
        // TODO Auto-generated method stub

        synchronized (SYNC_Playing) {
            SYNC_Playing.notify();
        }
        mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
    }

    /**
     * 播放准备就绪
     */
    @Override
    public void onPrepared() {
        // TODO Auto-generated method stub
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
    }

    @Override
    public void OnCompletionWithParam(int param) {
//param = 307 is end of stream
        // TODO Auto-generated method stub
    }


}

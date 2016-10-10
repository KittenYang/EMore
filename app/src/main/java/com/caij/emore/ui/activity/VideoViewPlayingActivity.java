package com.caij.emore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionWithParamListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.VideoInfo;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.view.VideoPlayView;
import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.weibo.ThemeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoViewPlayingActivity extends BaseActivity implements OnPreparedListener,
        OnCompletionListener,
        OnErrorListener,
        OnInfoListener,
        OnPlayingBufferCacheListener,
        OnCompletionWithParamListener, BVideoView.OnPositionUpdateListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, VideoPlayView {
    private final String TAG = "VideoViewPlayingActivity";

    public static final int EVENT_UPDATE_POSITION = 100;
    public static final int EVENT_VIDEO_PREPARE = 101;
    public static final int EVENT_VIDEO_COMOLETION = 102;
    public static final int EVENT_VIDEO_ERROR = 103;

    /**
     * 您的AK
     * 请到http://console.bce.baidu.com/iam/#/iam/accesslist获取
     */
    public static final String AK = "6770e0af7eb94cd2a4e7247a51b9522d";
    public static final String SK = "8cfa8441afde4170811317445bb11d53";
    @BindView(R.id.view_holder)
    FrameLayout viewHolder;
    @BindView(R.id.pause)
    ImageView pause;
    @BindView(R.id.watch_time)
    TextView watchTime;
    @BindView(R.id.sb)
    SeekBar sb;
    @BindView(R.id.video_time)
    TextView videoTime;
    @BindView(R.id.seek_time)
    LinearLayout seekTime;

    private String mVideoSource = null;

    private BVideoView mVV = null;

    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;
    private MainHandler mMainHandler;

    private final Object SYNC_Playing = new Object();

    private static final int UI_EVENT_PLAY = 0;

    private WakeLock mWakeLock = null;
    private static final String POWER_LOCK = "VideoViewPlayingActivity";


    /**
     * 播放状态
     */
    private enum PLAYER_STATUS {
        PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
    }

    private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;

    public static Intent newIntent(Context context, long weibiId) {
        Intent intent = new Intent(context, VideoViewPlayingActivity.class);
        intent.putExtra(Key.ID, weibiId);
        return intent;
    }

    public static Intent newIntent(Context context, String path) {
        Intent intent = new Intent(context, VideoViewPlayingActivity.class);
        intent.putExtra(Key.PATH, path);
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

    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == EVENT_UPDATE_POSITION) {
                if (mVV.getDuration() != 0) {
                    sb.setMax(mVV.getDuration());
                    sb.setProgress(mVV.getCurrentPosition());
                    watchTime.setText(DateUtil.formatSeconds(mVV.getCurrentPosition()));
                    videoTime.setText(DateUtil.formatSeconds(mVV.getDuration()));
                }
            }else if (msg.what == EVENT_VIDEO_PREPARE) {
                seekTime.setVisibility(View.VISIBLE);
                pause.setImageResource(R.drawable.video_play_btn_pause);
            }else if (msg.what == EVENT_VIDEO_COMOLETION) {
                pause.setImageResource(R.drawable.video_play_btn_play2);
            }else if (msg.what == EVENT_VIDEO_ERROR) {
                pause.setImageResource(R.drawable.video_play_btn_play2);
                showHint(getString(R.string.video_load_error));
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);

        LogUtil.d(this, "mVideoSource %s", mVideoSource);
        initUI();
        /**
         * 开启后台事件处理线程
         */
        mHandlerThread = new HandlerThread("event handler thread",
                Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());

        mMainHandler = new MainHandler();

        mVideoSource = getIntent().getStringExtra(Key.PATH);

    }

    @Override
    protected void setTheme() {
        int themePosition = ThemeUtils.getThemePosition(this);
        setTheme(ThemeUtils.THEME_ARR[themePosition][ThemeUtils.APP_VIDEO_PLAY_THEME_POSITION]);
    }

    @Override
    protected BasePresent createPresent() {
        return null;
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        /**
         * 设置ak
         */
        BVideoView.setAK(AK);
        mVV = new BVideoView(this);
        mVV.setLogLevel(4);
        mVV.setVideoScalingMode(BVideoView.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mVV.setRetainLastFrame(true);

        viewHolder.addView(mVV);

        mVV.setOnPositionUpdateListener(this);

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


        pause.setOnClickListener(this);
        sb.setOnSeekBarChangeListener(this);
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
        } else {
            if (mVideoSource != null) {
                mEventHandler.sendEmptyMessage(UI_EVENT_PLAY);
            }
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

        mMainHandler.removeCallbacksAndMessages(null);
        mEventHandler.removeCallbacksAndMessages(null);

        mVV.removeAllViews();

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
        mMainHandler.sendEmptyMessage(EVENT_VIDEO_ERROR);
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
        mMainHandler.sendEmptyMessage(EVENT_VIDEO_COMOLETION);
    }

    /**
     * 播放准备就绪
     */
    @Override
    public void onPrepared() {
        // TODO Auto-generated method stub
        mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
        mMainHandler.sendEmptyMessage(EVENT_VIDEO_PREPARE);
    }

    @Override
    public void OnCompletionWithParam(int param) {
        // TODO Auto-generated method stub
    }


    @Override
    public boolean onPositionUpdate(long l) {
        mMainHandler.sendEmptyMessage(EVENT_UPDATE_POSITION);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pause) {
            if (mVV.isPlaying()) {
                mVV.pause();
                pause.setImageResource(R.drawable.video_play_btn_play2);
            }else {
                if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
                    mVV.resume();
                    pause.setImageResource(R.drawable.video_play_btn_pause);
                }else {
                    mEventHandler.sendEmptyMessage(UI_EVENT_PLAY);
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            watchTime.setText(DateUtil.formatSeconds(progress));
            mVV.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onGetVideoInfoSuccess(VideoInfo videoInfo) {
        mVideoSource = videoInfo.getData();
        mEventHandler.sendEmptyMessage(UI_EVENT_PLAY);
    }

}

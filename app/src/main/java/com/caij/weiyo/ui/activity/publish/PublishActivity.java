package com.caij.weiyo.ui.activity.publish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.caij.weiyo.R;
import com.caij.weiyo.ui.activity.BaseToolBarActivity;
import com.caij.weiyo.ui.activity.SelectImageActivity;
import com.caij.weiyo.ui.fragment.EmotionFragment;
import com.caij.weiyo.utils.NavigationUtil;
import com.caij.weiyo.utils.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Caij on 2016/6/21.
 */
public abstract class PublishActivity extends BaseToolBarActivity {

    @BindView(R.id.btnCamera)
    LinearLayout btnCamera;
    @BindView(R.id.btn_emotion)
    LinearLayout btnEmotion;
    @BindView(R.id.btn_mention)
    LinearLayout btnMention;
    @BindView(R.id.btn_trends)
    LinearLayout btnTrends;
    @BindView(R.id.btn_overflow)
    LinearLayout btnOverflow;
    @BindView(R.id.btn_send)
    LinearLayout btnSend;
    @BindView(R.id.layBtns)
    LinearLayout layBtns;
    @BindView(R.id.fl_emotion)
    FrameLayout flEmotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout flPublishContent = (FrameLayout) findViewById(R.id.fl_publish_content);
        getLayoutInflater().inflate(getWeiboContentLayoutId(), flPublishContent, true);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fl_emotion, new EmotionFragment()).commit();
    }

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_publish_weibo;
    }

    public abstract int getWeiboContentLayoutId();

    @OnClick({R.id.btnCamera, R.id.btn_emotion, R.id.btn_mention, R.id.btn_trends, R.id.btn_overflow, R.id.btn_send, R.id.et_content})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCamera:
                Intent intent = NavigationUtil.getSelectImageActivity(this);
                startActivity(intent);
                break;
            case R.id.btn_emotion:
                SystemUtil.hideKeyBoard(this);
                if (flEmotion.getVisibility() == View.VISIBLE) {
                    flEmotion.setVisibility(View.GONE);
                } else {
                    if (SystemUtil.isKeyBoardShow(this)) {
                        flEmotion.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flEmotion.setVisibility(View.VISIBLE);
                            }
                        }, 200);
                    }else {
                        flEmotion.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.btn_mention:

                break;
            case R.id.btn_trends:

                break;
            case R.id.btn_overflow:

                break;
            case R.id.btn_send:
                onSendClick();
                break;

            case R.id.et_content:
                flEmotion.setVisibility(View.GONE);
                break;
        }
    }

    protected abstract void onSendClick();

    @Override
    public void onBackPressed() {
        if (flEmotion.getVisibility() == View.VISIBLE) {
            flEmotion.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }
}

package com.caij.emore.ui.activity.publish;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.caij.emore.EventTag;
import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.bean.Emotion;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.activity.BaseToolBarActivity;
import com.caij.emore.ui.activity.MentionSelectActivity;
import com.caij.emore.ui.fragment.EmotionFragment;
import com.caij.emore.utils.NavigationUtil;
import com.caij.emore.utils.SystemUtil;
import com.caij.emore.utils.rxbus.RxBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Caij on 2016/6/21.
 */
public abstract class PublishActivity<P extends BasePresent> extends BaseToolBarActivity<P> {

    public static final int REQUEST_CODE_SELECT_IMAGES = 10;
    public static final int REQUEST_CODE_SELECT_MENTION = 11;

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

    Observable<Emotion> mEmotionObservable;
    Observable<Object> mEmotionDeleteObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout flPublishContent = (FrameLayout) findViewById(R.id.fl_publish_content);
        getLayoutInflater().inflate(getWeiboContentLayoutId(), flPublishContent, true);
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fl_emotion, new EmotionFragment()).commit();
        mEmotionObservable = RxBus.getDefault().register(EventTag.ON_EMOTION_CLICK);
        mEmotionDeleteObservable = RxBus.getDefault().register(EventTag.ON_EMOTION_DELETE_CLICK);

        mEmotionObservable.subscribe(new Action1<Emotion>() {
            @Override
            public void call(Emotion emotion) {
                onEmotionClick(emotion);
            }
        });

        mEmotionDeleteObservable.subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                onEmotionDeleteClick();
            }
        });
    }

    protected abstract void onEmotionDeleteClick();

    protected abstract void onEmotionClick(Emotion emotion);

    @Override
    public int getAttachLayoutId() {
        return R.layout.activity_publish_weibo;
    }

    public abstract int getWeiboContentLayoutId();

    @OnClick({R.id.btnCamera, R.id.btn_emotion, R.id.btn_mention, R.id.btn_trends, R.id.btn_overflow, R.id.btn_send, R.id.et_content})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCamera:
                Intent intent = NavigationUtil.newSelectImageActivityIntent(this, 9);
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES);
                break;
            case R.id.btn_emotion:
                if (flEmotion.getVisibility() == View.VISIBLE) {
                    flEmotion.setVisibility(View.GONE);
                    SystemUtil.showKeyBoard(this);
                } else {
                    if (SystemUtil.isKeyBoardShow(this)) {
                        flEmotion.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                flEmotion.setVisibility(View.VISIBLE);
                            }
                        }, 200);
                        SystemUtil.hideKeyBoard(this);
                    }else {
                        flEmotion.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.btn_mention:
                intent = new Intent(this, MentionSelectActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_MENTION);
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
      if (!checkflEmotion()) {
          super.onBackPressed();
      }
    }

    protected boolean checkflEmotion() {
        if (flEmotion.getVisibility() == View.VISIBLE) {
            flEmotion.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGES) {
                ArrayList<String> paths = data.getStringArrayListExtra(Key.IMAGE_PATHS);
                onSelectImageSuccess(paths);
            }else if (requestCode == REQUEST_CODE_SELECT_MENTION) {
                String name = data.getStringExtra(Key.USERNAME);
                onSelectMentionSuccess(name);
            }
        }
    }

    protected abstract void onSelectImageSuccess(ArrayList<String> paths);
    protected abstract void onSelectMentionSuccess(String name);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(EventTag.ON_EMOTION_CLICK, mEmotionObservable);
        RxBus.getDefault().unregister(EventTag.ON_EMOTION_DELETE_CLICK, mEmotionDeleteObservable);
    }
}

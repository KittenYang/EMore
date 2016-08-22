package com.caij.emore.ui.activity.publish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.Emotion;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.present.RepostWeiboPresent;
import com.caij.emore.present.imp.RepostWeiboPresentImp;
import com.caij.emore.ui.view.RepostWeiboView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ImageLoader;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.widget.emotion.EmotionEditText;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Caij on 2016/6/27.
 */
public class RepostWeiboActivity extends PublishActivity<RepostWeiboPresent> implements RepostWeiboView {

    @BindView(R.id.et_content)
    EmotionEditText etContent;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_weibo)
    TextView tvDesc;

    private Dialog mRepostDialog;

    public static Intent newIntent(Context context, Weibo weibo) {
        Intent intent = new Intent(context, RepostWeiboActivity.class);
        intent.putExtra(Key.OBJ, weibo);
        return intent;
    }

    public static Intent newIntent(Context context, Weibo weibo, Comment comment) {
        Intent intent = new Intent(context, RepostWeiboActivity.class);
        intent.putExtra(Key.OBJ, weibo);
        intent.putExtra(Key.COMMENT, comment);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.repost);
        Weibo weibo = (Weibo) getIntent().getSerializableExtra(Key.OBJ);
        Comment comment = (Comment) getIntent().getSerializableExtra(Key.COMMENT);
        setWeibo(weibo, comment);
    }

    @Override
    protected RepostWeiboPresent createPresent() {
        Token accessToken = UserPrefs.get(this).getEMoreToken();
        Weibo weibo = (Weibo) getIntent().getSerializableExtra(Key.OBJ);
        return new RepostWeiboPresentImp(accessToken.getAccess_token(), weibo.getId(),
                new ServerWeiboSource(), this);
    }

    @Override
    protected void onEmotionDeleteClick() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        etContent.dispatchKeyEvent(event);
    }

    @Override
    protected void onEmotionClick(Emotion emotion) {
        etContent.append(emotion.key);
    }

    @Override
    public int getWeiboContentLayoutId() {
        return R.layout.include_repost_weibo;
    }

    @Override
    protected void onSendClick() {
        mPresent.repostWeibo(etContent.getText().toString());
    }

    @Override
    protected void onSelectImageSuccess(ArrayList<String> paths) {

    }

    @Override
    protected void onSelectMentionSuccess(String name) {
        etContent.append("@" + name + " ");
    }

    @Override
    public void onRepostSuccess(Weibo weibo) {
        ToastUtil.show(this, getString(R.string.repost_success));
        finish();
    }

    @Override
    public void showDialogLoading(boolean isShow) {
        if (isShow) {
            if (mRepostDialog == null) {
                mRepostDialog = DialogUtil.showProgressDialog(this, null, getString(R.string.requesting));
            }else {
                mRepostDialog.show();
            }
        }else {
            if (mRepostDialog != null) {
                mRepostDialog.dismiss();
            }
        }
    }

    public void setWeibo(Weibo weibo, Comment comment) {
        if (TextUtils.isEmpty(weibo.getBmiddle_pic())) {
            ImageLoader.load(this, imageView, weibo.getUser().getAvatar_large(), R.drawable.weibo_image_placeholder);
        }else {
            ImageLoader.load(this, imageView, weibo.getBmiddle_pic(), R.drawable.weibo_image_placeholder);
        }

        if (comment != null) {
            etContent.setText("//@" + comment.getUser().getName() + ":" + comment.getText());
        }else {
            if (weibo.getRetweeted_status() != null) {
                etContent.setText("//@" + weibo.getUser().getName() + ":" + weibo.getText());
            }
        }

        if (weibo.getRetweeted_status() == null) {
            tvName.setText("@" + weibo.getUser().getName());
            tvDesc.setText(weibo.getText());
        }else {
            tvName.setText("@" + weibo.getRetweeted_status().getUser().getName());
            tvDesc.setText(weibo.getRetweeted_status().getText());
        }
    }
}

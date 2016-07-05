package com.caij.weiyo.ui.activity.publish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.Emotion;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.present.RepostWeiboPresent;
import com.caij.weiyo.present.imp.RepostWeiboPresentImp;
import com.caij.weiyo.present.view.RepostWeiboView;
import com.caij.weiyo.source.server.ServerWeiboSource;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.utils.ImageLoader;
import com.caij.weiyo.utils.ToastUtil;
import com.caij.weiyo.view.emotion.EmotionEditText;
import com.caij.weiyo.view.emotion.EmotionTextView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Caij on 2016/6/27.
 */
public class RepostWeiboActivity extends PublishActivity implements RepostWeiboView {

    @BindView(R.id.et_content)
    EmotionEditText etContent;
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_weibo)
    EmotionTextView tvDesc;

    private RepostWeiboPresent mRepostWeiboPresent;
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
        AccessToken accessToken = UserPrefs.get().getWeiYoToken();
        Weibo weibo = (Weibo) getIntent().getSerializableExtra(Key.OBJ);
        Comment comment = (Comment) getIntent().getSerializableExtra(Key.COMMENT);
        mRepostWeiboPresent = new RepostWeiboPresentImp(accessToken.getAccess_token(), weibo.getId(),
                new ServerWeiboSource(), this);

        setWeibo(weibo, comment);
    }

    @Override
    protected void onEmotionDeleteClick() {

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
        mRepostWeiboPresent.repostWeibo(etContent.getText().toString());
    }

    @Override
    protected void onSelectSuccess(ArrayList<String> paths) {

    }

    @Override
    public void onRepostSuccess(Weibo weibo) {
        ToastUtil.show(this, getString(R.string.repost_success));
        Intent intent = new Intent();
        intent.putExtra(Key.OBJ, weibo);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showLoading(boolean isShow) {
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

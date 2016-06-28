package com.caij.weiyo.ui.activity.publish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.caij.weiyo.Key;
import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.Emotion;
import com.caij.weiyo.present.CommentWeiboPresent;
import com.caij.weiyo.present.imp.CommentWeiboPresentImp;
import com.caij.weiyo.present.view.CommentWeiboView;
import com.caij.weiyo.source.server.ServerCommentSource;
import com.caij.weiyo.utils.DialogUtil;
import com.caij.weiyo.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Caij on 2016/6/27.
 */
public class CommentWeiboActivity extends PublishActivity implements CommentWeiboView {

    @BindView(R.id.et_content)
    EditText etContent;

    private CommentWeiboPresent mCommentWeiboPresent;
    private Dialog mCommentDialog;

    public static Intent newIntent(Context context, long weiboId) {
        Intent intent = new Intent(context, CommentWeiboActivity.class);
        intent.putExtra(Key.ID, weiboId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnCamera.setVisibility(View.GONE);
        AccessToken token = UserPrefs.get().getWeiYoToken();
        long weiboId = getIntent().getLongExtra(Key.ID, -1);
        mCommentWeiboPresent = new CommentWeiboPresentImp(token.getAccess_token(),
                weiboId ,new ServerCommentSource(), this);
        mCommentWeiboPresent.onCreate();
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
        return R.layout.include_comment_weibo;
    }

    @Override
    protected void onSendClick() {
        mCommentWeiboPresent.toCommentWeibo(etContent.getText().toString());
    }

    @Override
    protected void onSelectSuccess(ArrayList<String> paths) {

    }

    @Override
    public void onCommentSuccess(Comment comment) {
        ToastUtil.show(this, getString(R.string.comment_success));
        Intent intent = new Intent();
        intent.putExtra(Key.OBJ, comment);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showLoading(boolean isShow) {
        if (isShow) {
            if (mCommentDialog  == null) {
                mCommentDialog = DialogUtil.showProgressDialog(this, null, getString(R.string.wait_comment));
            }else {
                mCommentDialog.show();
            }
        }else {
            if (mCommentDialog != null) {
                mCommentDialog.dismiss();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCommentWeiboPresent.onDestroy();
    }
}

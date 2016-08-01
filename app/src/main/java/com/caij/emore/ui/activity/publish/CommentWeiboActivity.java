package com.caij.emore.ui.activity.publish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.caij.emore.Key;
import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.Emotion;
import com.caij.emore.present.CommentWeiboPresent;
import com.caij.emore.present.imp.CommentWeiboPresentImp;
import com.caij.emore.present.view.CommentWeiboView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;

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
        setTitle(R.string.comment);
        btnCamera.setVisibility(View.GONE);
        AccessToken token = UserPrefs.get().getEMoreToken();
        long weiboId = getIntent().getLongExtra(Key.ID, -1);
        mCommentWeiboPresent = new CommentWeiboPresentImp(token.getAccess_token(),
                weiboId ,new ServerWeiboSource(), this);
        mCommentWeiboPresent.onCreate();
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
        return R.layout.include_comment_weibo;
    }

    @Override
    protected void onSendClick() {
        mCommentWeiboPresent.toCommentWeibo(etContent.getText().toString());
    }

    @Override
    protected void onSelectImageSuccess(ArrayList<String> paths) {

    }

    @Override
    protected void onSelectMentionSuccess(String name) {
        etContent.append("@" + name + " ");
    }

    @Override
    public void onCommentSuccess(Comment comment) {
        ToastUtil.show(this, getString(R.string.comment_success));
        finish();
    }

    @Override
    public void showDialogLoading(boolean isShow) {
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

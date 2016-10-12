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
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.Emotion;
import com.caij.emore.present.ReplyCommentWeiboPresent;
import com.caij.emore.present.imp.ReplyCommentPresentImp;
import com.caij.emore.remote.imp.CommentApiImp;
import com.caij.emore.ui.view.CommentStatusView;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Caij on 2016/7/2.
 */
public class ReplyCommentActivity extends PublishActivity<ReplyCommentWeiboPresent> implements CommentStatusView {

    @BindView(R.id.et_content)
    EditText etContent;
    private Dialog mCommentDialog;

    public static Intent newIntent(Context context, long weibiId, long cid) {
        Intent intent = new Intent(context, ReplyCommentActivity.class);
        intent.putExtra(Key.ID, weibiId);
        intent.putExtra(Key.CID, cid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("回复评论");
        btnCamera.setVisibility(View.GONE);
    }

    @Override
    protected ReplyCommentWeiboPresent createPresent() {
        Token token = UserPrefs.get(this).getToken();
        long weiboId = getIntent().getLongExtra(Key.ID, -1);
        long cid = getIntent().getLongExtra(Key.CID, -1);
        return new ReplyCommentPresentImp(weiboId , cid, new CommentApiImp(), this);
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
        mPresent.toReplyComment(etContent.getText().toString());
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
        ToastUtil.show(this, getString(R.string.replay_success));
        Intent intent = new Intent();
        intent.putExtra(Key.OBJ, comment);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void showDialogLoading(boolean isShow) {
        if (isShow) {
            if (mCommentDialog  == null) {
                mCommentDialog = DialogUtil.showProgressDialog(this, null, getString(R.string.requesting));
            }else {
                mCommentDialog.show();
            }
        }else {
            if (mCommentDialog != null) {
                mCommentDialog.dismiss();
            }
        }
    }
}

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
import com.caij.emore.present.ReplyCommentWeiboPresent;
import com.caij.emore.present.imp.ReplyCommentPresentImp;
import com.caij.emore.present.view.CommentWeiboView;
import com.caij.emore.source.server.ServerWeiboSource;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Caij on 2016/7/2.
 */
public class ReplyCommentActivity extends PublishActivity implements CommentWeiboView {

    @BindView(R.id.et_content)
    EditText etContent;
    private ReplyCommentWeiboPresent mCommentWeiboPresent;
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
        AccessToken token = UserPrefs.get().getEMoreToken();
        long weiboId = getIntent().getLongExtra(Key.ID, -1);
        long cid = getIntent().getLongExtra(Key.CID, -1);
        mCommentWeiboPresent = new ReplyCommentPresentImp(token.getAccess_token(),
                weiboId , cid, new ServerWeiboSource(), this);
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
        mCommentWeiboPresent.toReplyComment(etContent.getText().toString());
    }

    @Override
    protected void onSelectSuccess(ArrayList<String> paths) {

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

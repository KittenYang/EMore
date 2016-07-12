package com.caij.emore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.caij.emore.R;
import com.caij.emore.present.view.BaseView;
import com.caij.emore.ui.activity.login.AbsLoginActivity;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;

/**
 * Created by Caij on 2016/5/27.
 */
public class BaseActivity extends AppCompatActivity implements BaseView{

    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().push(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    protected void showToast(String msg) {
        ToastUtil.show(this, msg);
    }

    protected void showToast(int msgId) {
        ToastUtil.show(this, msgId);
    }

    @Override
    public void onAuthenticationError() {
        ActivityStack.getInstance().remove(this);
        ActivityStack.getInstance().finishAllActivity();
        Intent intent = new Intent(this, EMoreLoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDefaultLoadError() {
        showToast(R.string.net_request_error);
    }

    @Override
    public void showHint(int stringId) {
        showToast(stringId);
    }

    @Override
    public void showDialogLoading(boolean isShow, int hintStringId) {
        if (isShow) {
            mLoadingDialog = DialogUtil.showProgressDialog(this, null, getString(hintStringId));
        }else {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showDialogLoading(boolean isShow) {
        showDialogLoading(isShow, R.string.loading);
    }
}

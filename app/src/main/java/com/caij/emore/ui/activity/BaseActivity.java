package com.caij.emore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.view.BaseView;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.Init;
import com.caij.emore.utils.ToastUtil;

/**
 * Created by Caij on 2016/5/27.
 */
public abstract class BaseActivity<P extends BasePresent> extends AppCompatActivity implements BaseView{

    private Dialog mLoadingDialog;
    protected P mPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().push(this);
        mPresent = createPresent();
        if (mPresent != null) {
            mPresent.onCreate();
        }
    }

    protected abstract P createPresent();

    protected P getPresent() {
        return mPresent;
    }

    @Override
    public void onAuthenticationError() {
        UserPrefs userPrefs = UserPrefs.get(this);
        showHint(R.string.auth_invalid_hint);
        ActivityStack.getInstance().remove(this);
        ActivityStack.getInstance().finishAllActivity();
        Intent intent = EMoreLoginActivity.newEMoreLoginIntent(this, userPrefs.getAccount().getUsername(),
                userPrefs.getAccount().getPwd());
        startActivity(intent);

        Init.getInstance().stop(this);
        UserPrefs.get(this).deleteAccount(userPrefs.getAccount());

        finish();
    }

    @Override
    public void onDefaultLoadError() {
        showHint(R.string.net_request_error);
    }

    @Override
    public void showHint(int stringId) {
        showHint(getString(stringId));
    }

    @Override
    public void showHint(String string) {
        ToastUtil.show(this, string);
    }

    @Override
    public void showDialogLoading(boolean isShow, int hintStringId) {
        if (isShow) {
            mLoadingDialog = DialogUtil.showProgressDialog(this, null, getString(hintStringId));
        }else {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        }
    }

    @Override
    public void showDialogLoading(boolean isShow) {
        showDialogLoading(isShow, R.string.loading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        if (mPresent != null) {
            mPresent.onDestroy();
        }
    }
}

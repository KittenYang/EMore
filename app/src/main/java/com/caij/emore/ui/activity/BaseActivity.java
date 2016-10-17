package com.caij.emore.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.caij.emore.EMoreApplication;
import com.caij.emore.R;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.view.BaseView;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;
import com.caij.emore.utils.weibo.ThemeUtils;

/**
 * Created by Caij on 2016/5/27.
 */
public abstract class BaseActivity<P extends BasePresent> extends AppCompatActivity implements BaseView{

    private Dialog mLoadingDialog;
    protected P mPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        ActivityStack.getInstance().push(this);

        mPresent = createPresent();
        if (mPresent != null) {
            mPresent.onCreate();
        }
    }

    protected void setTheme() {
        int themePosition = ThemeUtils.getThemePosition(this);
        setTheme(ThemeUtils.THEME_ARR[themePosition][ThemeUtils.APP_THEME_POSITION]);
    }

    protected abstract P createPresent();

    protected P getPresent() {
        return mPresent;
    }

    @Override
    public void onAuthenticationError() {
        ((EMoreApplication)getApplication()).onAuthenticationError();
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

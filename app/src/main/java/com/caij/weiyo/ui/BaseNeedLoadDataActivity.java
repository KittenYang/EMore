package com.caij.weiyo.ui;

import android.content.Intent;

import com.caij.weiyo.R;
import com.caij.weiyo.present.view.BaseView;
import com.caij.weiyo.utils.ActivityManager;

/**
 * Created by Caij on 2016/5/30.
 */
public abstract class BaseNeedLoadDataActivity extends BaseActivity implements BaseView {

    @Override
    public void onAuthenticationError() {
        ActivityManager.getInstance().remove(this);
        ActivityManager.getInstance().finishAllActivity();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onComnLoadError() {
        showToast(R.string.net_request_error);
    }
}

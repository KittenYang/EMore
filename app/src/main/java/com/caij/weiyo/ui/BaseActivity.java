package com.caij.weiyo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.caij.weiyo.R;
import com.caij.weiyo.present.view.BaseView;
import com.caij.weiyo.utils.ActivityStack;
import com.caij.weiyo.utils.ToastUtil;

/**
 * Created by Caij on 2016/5/27.
 */
public class BaseActivity extends AppCompatActivity implements BaseView{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getInstance().push(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().remove(this);
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onComnLoadError() {
        showToast(R.string.net_request_error);
    }
}

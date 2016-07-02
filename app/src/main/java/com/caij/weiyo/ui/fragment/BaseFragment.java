package com.caij.weiyo.ui.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.caij.weiyo.R;
import com.caij.weiyo.UserPrefs;
import com.caij.weiyo.present.view.BaseView;
import com.caij.weiyo.ui.activity.LoginActivity;
import com.caij.weiyo.utils.ActivityStack;
import com.caij.weiyo.utils.ToastUtil;

/**
 * Created by Caij on 2016/6/3.
 */
public class BaseFragment extends Fragment implements BaseView{

    @Override
    public void onAuthenticationError() {
        showToast("身份信息失效, 需要重新认证");
        ActivityStack.getInstance().remove(getActivity());
        ActivityStack.getInstance().finishAllActivity();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        UserPrefs.get().clear();
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onComnLoadError() {
        showToast(R.string.net_request_error);
    }

    protected void showToast(String msg) {
        ToastUtil.show(getActivity(), msg);
    }

    protected void showToast(int msgId) {
        ToastUtil.show(getActivity(), msgId);
    }

}

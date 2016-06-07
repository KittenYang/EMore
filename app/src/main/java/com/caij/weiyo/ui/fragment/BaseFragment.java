package com.caij.weiyo.ui.fragment;

import android.app.Fragment;
import android.content.Intent;

import com.caij.weiyo.R;
import com.caij.weiyo.present.view.BaseView;
import com.caij.weiyo.ui.LoginActivity;
import com.caij.weiyo.utils.ActivityManager;
import com.caij.weiyo.utils.ToastUtil;

/**
 * Created by Caij on 2016/6/3.
 */
public class BaseFragment extends Fragment implements BaseView{

    @Override
    public void onAuthenticationError() {
        ActivityManager.getInstance().remove(getActivity());
        ActivityManager.getInstance().finishAllActivity();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
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

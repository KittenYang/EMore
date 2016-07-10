package com.caij.emore.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.present.view.BaseView;
import com.caij.emore.ui.activity.login.EMoreLoginActivity;
import com.caij.emore.utils.ActivityStack;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.ToastUtil;

/**
 * Created by Caij on 2016/6/3.
 */
public class BaseFragment extends Fragment implements BaseView{

    private Dialog mLoadingDialog;

    @Override
    public void onAuthenticationError() {
        showToast("身份信息失效, 需要重新认证");
        ActivityStack.getInstance().remove(getActivity());
        ActivityStack.getInstance().finishAllActivity();
        Intent intent = new Intent(getActivity(), EMoreLoginActivity.class);
        UserPrefs.get().clear();
        startActivity(intent);
        getActivity().finish();
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
            mLoadingDialog = DialogUtil.showProgressDialog(getActivity(), null, getString(hintStringId));
        }else {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showDialogLoading(boolean isShow) {
        showDialogLoading(isShow, R.string.loading);
    }

    protected void showToast(String msg) {
        ToastUtil.show(getActivity(), msg);
    }

    protected void showToast(int msgId) {
        ToastUtil.show(getActivity(), msgId);
    }

}

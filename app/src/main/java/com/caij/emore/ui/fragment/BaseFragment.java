package com.caij.emore.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.caij.emore.R;
import com.caij.emore.UserPrefs;
import com.caij.emore.ui.view.BaseView;
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
        UserPrefs userPrefs = UserPrefs.get();
        showToast(getString(R.string.auth_invalid_hint));
        ActivityStack.getInstance().remove(getActivity());
        ActivityStack.getInstance().finishAllActivity();
        Intent intent = EMoreLoginActivity.newEMoreLoginIntent(getActivity(), userPrefs.getAccount().getUsername(),
                userPrefs.getAccount().getPwd());
        startActivity(intent);
        getActivity().finish();
        UserPrefs.get().clear();
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
    public void showHint(String string) {
       ToastUtil.show(getActivity(), string);
    }

    @Override
    public void showDialogLoading(boolean isShow, int hintStringId) {
        if (isShow) {
            mLoadingDialog = DialogUtil.showProgressDialog(getActivity(), null, getString(hintStringId));
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

    protected void showToast(String msg) {
        ToastUtil.show(getActivity(), msg);
    }

    protected void showToast(int msgId) {
        ToastUtil.show(getActivity(), msgId);
    }

}

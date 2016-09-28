package com.caij.emore.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.caij.emore.R;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.present.BasePresent;
import com.caij.emore.ui.activity.login.WeiCoLoginActivity;
import com.caij.emore.ui.view.BaseView;
import com.caij.emore.utils.DialogUtil;
import com.caij.emore.utils.Init;
import com.caij.emore.utils.ToastUtil;

/**
 * Created by Caij on 2016/6/3.
 */
public abstract class BaseFragment<P extends BasePresent> extends Fragment implements BaseView{

    private Dialog mLoadingDialog;
    protected P mPresent;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        UserPrefs userPrefs = UserPrefs.get(getActivity());
        showHint(R.string.auth_invalid_hint);

        Intent intent = WeiCoLoginActivity.newWeiCoLoginIntent(getActivity(), userPrefs.getAccount().getUsername(),
                userPrefs.getAccount().getPwd());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Init.getInstance().stop(getActivity());

        startActivity(intent);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        if (mPresent != null) {
            mPresent.onDestroy();
        }
    }

}

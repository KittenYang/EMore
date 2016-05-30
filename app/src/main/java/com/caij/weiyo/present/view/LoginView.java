package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.AccessToken;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginView extends BaseView{
    public void onLoginSuccess(AccessToken accessToken);
    public void showLoading(boolean isVisible);
}

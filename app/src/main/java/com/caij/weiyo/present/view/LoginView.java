package com.caij.weiyo.present.view;

import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.present.LoginPresent;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginView extends BaseView{
    public void onLoginSuccess(AccessToken accessToken);
}

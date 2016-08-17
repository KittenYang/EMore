package com.caij.emore.ui.view;

import com.caij.emore.bean.AccessToken;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginView extends BaseView{
    public void onLoginSuccess(AccessToken accessToken);
}

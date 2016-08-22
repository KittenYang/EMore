package com.caij.emore.ui.view;


import com.caij.emore.account.Token;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginView extends BaseView{
    public void onLoginSuccess(Token accessToken);
}

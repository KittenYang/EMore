package com.caij.emore.ui.view;

import com.caij.emore.account.Token;
import com.caij.emore.bean.response.WeiCoLoginResponse;

/**
 * Created by Caij on 2016/5/28.
 */
public interface WeiCoLoginView extends BaseView{
    public void onLoginSuccess(WeiCoLoginResponse response);

    void onLoginSuccess(Token accessToken);
}

package com.caij.emore.present.view;

import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.response.WeiCoLoginResponse;

/**
 * Created by Caij on 2016/5/28.
 */
public interface WeiCoLoginView extends BaseView{
    public void onLoginSuccess(WeiCoLoginResponse response);

    void onLoginSuccess(AccessToken accessToken);
}

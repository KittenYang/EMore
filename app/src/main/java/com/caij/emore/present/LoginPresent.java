package com.caij.emore.present;

/**
 * Created by Caij on 2016/5/28.
 */
public interface LoginPresent extends BasePresent{

    public void getAccessToken(String clientId, String clientSecret, String grantType, String redirectUrL );
}

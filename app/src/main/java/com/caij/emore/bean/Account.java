package com.caij.emore.bean;

import com.caij.emore.bean.response.WeiCoLoginResponse;

import java.io.Serializable;

/**
 * Created by Caij on 2016/6/25.
 */
public class Account implements Serializable {

    private String username;
    private String pwd;
    private AccessToken weiyoToken;
    private AccessToken weicoToken;
    private WeiCoLoginResponse weiCoLoginResponse;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public AccessToken getWeiyoToken() {
        return weiyoToken;
    }

    public void setWeiyoToken(AccessToken weiyoToken) {
        this.weiyoToken = weiyoToken;
    }

    public AccessToken getWeicoToken() {
        return weicoToken;
    }

    public void setWeicoToken(AccessToken weicoToken) {
        this.weicoToken = weicoToken;
    }

    public WeiCoLoginResponse getWeiCoLoginResponse() {
        return weiCoLoginResponse;
    }

    public void setWeiCoLoginResponse(WeiCoLoginResponse weiCoLoginResponse) {
        this.weiCoLoginResponse = weiCoLoginResponse;
    }
}

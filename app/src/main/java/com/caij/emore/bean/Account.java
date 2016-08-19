package com.caij.emore.bean;

import java.io.Serializable;

/**
 * Created by Caij on 2016/6/25.
 */
public class Account implements Serializable {

    private String username;
    private String pwd;
    private AccessToken emoreToken;
    private AccessToken weicoToken;

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

    public AccessToken getEmoreToken() {
        return emoreToken;
    }

    public void setEmoreToken(AccessToken emoreToken) {
        this.emoreToken = emoreToken;
    }

    public AccessToken getWeicoToken() {
        return weicoToken;
    }

    public void setWeicoToken(AccessToken weicoToken) {
        this.weicoToken = weicoToken;
    }

}

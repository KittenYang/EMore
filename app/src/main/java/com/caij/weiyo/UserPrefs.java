package com.caij.weiyo;

import android.text.TextUtils;

import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.utils.GsonUtils;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.SPUtil;

/**
 * Created by Caij on 2015/11/7.
 */
public class UserPrefs {

    public static final String USER_TOKEN = "user_token";

    private static UserPrefs singleton;

    private AccessToken token;

    public UserPrefs() {
        String jsonTokenStr  = SPUtil.getString(USER_TOKEN, "");
        if (!TextUtils.isEmpty(jsonTokenStr)) {
            token = GsonUtils.fromJson(jsonTokenStr, AccessToken.class);
        }
    }

    public static UserPrefs get() {
        if (singleton == null) {
            synchronized (UserPrefs.class) {
                if (singleton == null) {
                    singleton = new UserPrefs();
                }
            }
        }
        return singleton;
    }

    public void setToken(AccessToken token) {
        this.token = token;
        String jsonTokenStr  = GsonUtils.toJson(token);
        LogUtil.d(this, jsonTokenStr);
        SPUtil.saveString(USER_TOKEN, jsonTokenStr);
    }

    public AccessToken getToken(){
        return token;
    }

}

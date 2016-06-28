package com.caij.weiyo;

import android.text.TextUtils;

import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.bean.Account;
import com.caij.weiyo.utils.GsonUtils;
import com.caij.weiyo.utils.LogUtil;
import com.caij.weiyo.utils.SPUtil;

/**
 * Created by Caij on 2015/11/7.
 */
public class UserPrefs {

    public static final String ACCOUNT = "account";

    private static UserPrefs singleton;

    private Account mAccount;

    public UserPrefs() {
        String jsonTokenStr  = SPUtil.getString(ACCOUNT, "");
        if (!TextUtils.isEmpty(jsonTokenStr)) {
            mAccount = GsonUtils.fromJson(jsonTokenStr, Account.class);
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

    public AccessToken getWeiYoToken(){
        if (mAccount != null) {
            return mAccount.getWeiyoToken();
        }
        return null;
    }


    public AccessToken getWeiCoToken(){
        if (mAccount != null) {
            return mAccount.getWeicoToken();
        }
        return null;
    }

    public void setAccount(Account account) {
        this.mAccount = account;
        String json = GsonUtils.toJson(mAccount);
        LogUtil.d(this, json);
        SPUtil.saveString(ACCOUNT, json);
    }

    public Account getAccount() {
        return mAccount;
    }

}

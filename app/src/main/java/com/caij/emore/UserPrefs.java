package com.caij.emore;

import android.text.TextUtils;

import com.caij.emore.bean.AccessToken;
import com.caij.emore.bean.Account;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.SPUtil;

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

    public AccessToken getEMoreToken(){
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

    private void saveAccount(Account account) {
        this.mAccount = account;
        String json = GsonUtils.toJson(mAccount);
        LogUtil.d(this, json);
        SPUtil.saveString(ACCOUNT, json);
    }

    public void setUsername(String name) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.setUsername(name);
        saveAccount(mAccount);
    }

    public void setPwd(String pwd) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.setPwd(pwd);
        saveAccount(mAccount);
    }

    public void setEMoreToken(AccessToken token) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.setWeiyoToken(token);
        saveAccount(mAccount);
    }

    public void setWeiCoToken(AccessToken token) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.setWeicoToken(token);
        saveAccount(mAccount);
    }

    public void setWeiCoLoginInfo(WeiCoLoginResponse response) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.setWeiCoLoginResponse(response);
        saveAccount(mAccount);
    }

    public Account getAccount() {
        return mAccount;
    }

    public void clear() {
        mAccount = null;
        SPUtil.saveString(ACCOUNT, "");
    }
}

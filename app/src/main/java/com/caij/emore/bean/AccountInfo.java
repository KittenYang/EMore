package com.caij.emore.bean;

import com.caij.emore.account.Account;
import com.caij.emore.database.bean.User;

/**
 * Created by Caij on 2016/8/22.
 */
public class AccountInfo {

    private Account account;
    private User user;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

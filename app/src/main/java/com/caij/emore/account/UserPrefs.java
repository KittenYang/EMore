package com.caij.emore.account;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.caij.emore.utils.LogUtil;

import java.util.List;

import de.greenrobot.dao.identityscope.IdentityScopeType;

/**
 * Created by Caij on 2015/11/7.
 */
public class UserPrefs {

    public static final String ACCOUNT = "account";

    private static UserPrefs singleton;

    private AccountDao mAccountDao;
    private TokenDao mTokenDao;

    private Account mAccount;

    public UserPrefs(Context context) {
        init(context);
        initAccount();
    }

    public void init(Context context) {
        DBHelp dbHelp = new DBHelp(context, ACCOUNT, null);
        DaoMaster daoMaster = new DaoMaster(dbHelp.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession(IdentityScopeType.None);
        mAccountDao = daoSession.getAccountDao();
        mTokenDao = daoSession.getTokenDao();
    }

    private void initAccount() {
        List<Account> accounts = mAccountDao.queryBuilder().
                where(AccountDao.Properties.Status.eq(Account.STATUS_USING)).list();
        if (accounts != null && accounts.size() > 0) { //如果存在多个正在使用的账号 直接清除掉 只留一个正在使用的账号
            if (accounts.size() > 1) {
//                throw new IllegalStateException("have multiple account using");
                for (int i = 1; i < accounts.size(); i ++) {
                    Account account = accounts.get(i);
                    account.setStatus(Account.STATUS_BACKGROUND);
                    save(account);
                }
            }

            mAccount = accounts.get(0);
            Token token = mTokenDao.load(String.valueOf(mAccount.getUid()));
            mAccount.setToken(token);
        }
    }

    public static UserPrefs get(Context context) {
        if (singleton == null) {
            synchronized (UserPrefs.class) {
                if (singleton == null) {
                    singleton = new UserPrefs(context);
                }
            }
        }
        return singleton;
    }

    public Token getToken(){
        if (mAccount != null) {
            return mAccount.getToken();
        }
        return null;
    }

    private void save(Account account) {
        mAccountDao.insertOrReplace(account);
        if (account.getToken() != null) {
            mTokenDao.insertOrReplace(account.getToken());
        }
    }

    public Account getAccount() {
        return mAccount;
    }

    public List<Account> getAccounts() {
        List<Account> accounts = mAccountDao.queryBuilder().orderDesc(AccountDao.Properties.Status).list();
        for (Account account : accounts) {
            Token token = mTokenDao.load(String.valueOf(account.getUid()));
            account.setToken(token);
        }
        return accounts;
    }

    public void changeAccount(Account account) {
        if (mAccount != null) {
            mAccount.setStatus(Account.STATUS_BACKGROUND);
            save(mAccount);
        }

        mAccount = account;
        mAccount.setStatus(Account.STATUS_USING);
        save(mAccount);
    }

    public void deleteAccount(Account account) {
        mAccountDao.delete(account);

        if (mAccount.getToken() != null) {
            mTokenDao.delete(account.getToken());
        }

        if (account.equals(mAccount)) {
            mAccount = null;
        }
    }

    private static class DBHelp extends DaoMaster.OpenHelper {

        DBHelp(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

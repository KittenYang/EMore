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
        if (accounts != null && accounts.size() > 0) {
            if (accounts.size() > 1) {
                throw new IllegalStateException("have multiple account using");
            }else {
                mAccount = accounts.get(0);
                Token emoreToken = mTokenDao.load(mAccount.getUid() + "_emore");
                Token weicoToken = mTokenDao.load(mAccount.getUid() + "_weico");
                mAccount.setEmoreToken(emoreToken);
                mAccount.setWeiCoToken(weicoToken);
            }
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

    public Token getEMoreToken(){
        if (mAccount != null) {
            return mAccount.getEmoreToken();
        }
        return null;
    }


    public Token getWeiCoToken(){
        if (mAccount != null) {
            return mAccount.getWeiCoToken();
        }
        return null;
    }

    public void commit(Account account) {
        this.mAccount = account;
        if (account != null) {
            for (Account account1 : getAccounts()) {
                account1.setStatus(Account.STATUS_BACKGROUND);
                save(account1);
            }

            account.setStatus(Account.STATUS_USING);
            save(account);
        }
    }

    private void save(Account account) {
        mAccountDao.insertOrReplace(account);
        if (account.getEmoreToken() != null) {
            mTokenDao.insertOrReplace(account.getEmoreToken());
        }

        if (account.getWeiCoToken() != null) {
            mTokenDao.insertOrReplace(account.getWeiCoToken());
        }
    }

    public Account getAccount() {
        return mAccount;
    }

    public List<Account> getAccounts() {
        List<Account> accounts = mAccountDao.queryBuilder().orderDesc(AccountDao.Properties.Status).list();
        for (Account account : accounts) {
            Token emoreToken = mTokenDao.load(account.getUid() + "_emore");
            Token weicoToken = mTokenDao.load(account.getUid() + "_weico");
            account.setEmoreToken(emoreToken);
            account.setWeiCoToken(weicoToken);
        }
        return accounts;
    }

    public void changeAccount(Account account) {
        commit(account);
    }

    public void deleteAccount(Account account) {
        mAccountDao.delete(account);
        if (mAccount.getEmoreToken() != null) {
            mTokenDao.delete(account.getEmoreToken());
        }

        if (mAccount.getWeiCoToken() != null) {
            mTokenDao.delete(account.getWeiCoToken());
        }

        if (account.equals(mAccount)) {
            mAccount = null;
        }
    }

    public static class DBHelp extends DaoMaster.OpenHelper {

        public DBHelp(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

package com.caij.emore.account;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

    public void save() {
        if (mAccount != null) {
            mAccountDao.insertOrReplace(mAccount);
            if (mAccount.getEmoreToken() != null) {
                mTokenDao.insertOrReplace(mAccount.getEmoreToken());
            }

            if (mAccount.getWeiCoToken() != null) {
                mTokenDao.insertOrReplace(mAccount.getWeiCoToken());
            }
        }
    }

    public void setUsername(String name) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.setUsername(name);
    }

    public void setPwd(String pwd) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        mAccount.setPwd(pwd);
    }

    public void setEMoreToken(Token token) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        token.setKey(token.getUid() + "_emore");
        mAccount.setUid(token.getUid());
        mAccount.setEmoreToken(token);
    }

    public void setWeiCoToken(Token token) {
        if (mAccount == null) {
            mAccount = new Account();
        }
        token.setKey(token.getUid() + "_weico");
        mAccount.setUid(token.getUid());
        mAccount.setWeiCoToken(token);
    }

    public Account getAccount() {
        return mAccount;
    }

    public void clear() {
        mAccountDao.delete(mAccount);
        if (mAccount.getEmoreToken() != null) {
            mTokenDao.delete(mAccount.getEmoreToken());
        }

        if (mAccount.getWeiCoToken() != null) {
            mTokenDao.delete(mAccount.getWeiCoToken());
        }

        mAccount = null;
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

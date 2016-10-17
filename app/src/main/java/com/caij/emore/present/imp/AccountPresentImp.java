package com.caij.emore.present.imp;

import android.os.AsyncTask;

import com.caij.emore.EMApplication;
import com.caij.emore.account.Account;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.AccountInfo;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.dao.DaoSession;
import com.caij.emore.present.AccountPresent;
import com.caij.emore.ui.view.AccountView;
import com.caij.emore.utils.ExecutorServiceUtil;
import com.caij.emore.utils.db.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caij on 2016/8/22.
 */
public class AccountPresentImp extends AbsBasePresent implements AccountPresent {

    private AccountView mAccountView;

    public AccountPresentImp(AccountView accountView) {
        mAccountView = accountView;
    }

    @Override
    public void userFirstVisible() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void onCreate() {
        ExecutorServiceUtil.executeAsyncTask(new AsyncTask<Object, Object, List<AccountInfo>>() {
            @Override
            protected List<AccountInfo> doInBackground(Object... params) {
                List<Account> accounts = UserPrefs.get(EMApplication.getInstance()).getAccounts();
                List<AccountInfo> accountInfos = new ArrayList<AccountInfo>();
                for (Account account : accounts) {
                    AccountInfo accountInfo = new AccountInfo();
                    DaoSession daoSession = DBManager.newDaoSession(EMApplication.getInstance(),
                           account.getUid(), false);
                    User user = daoSession.getUserDao().load(account.getUid());
                    accountInfo.setAccount(account);
                    accountInfo.setUser(user);
                    daoSession.getDatabase().close();
                    accountInfos.add(accountInfo);
                }
                return accountInfos;
            }

            @Override
            protected void onPostExecute(List<AccountInfo> accountInfos) {
                super.onPostExecute(accountInfos);
                if (accountInfos != null) {
                    mAccountView.setEntities(accountInfos);
                }else {
                    mAccountView.onDefaultLoadError();
                }
            }
        });

    }

}

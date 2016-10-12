package com.caij.emore.manager.imp;

import com.caij.emore.manager.UserManager;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.dao.UserDao;
import com.caij.emore.utils.db.DBManager;

import java.util.List;

/**
 * Created by Caij on 2016/10/9.
 */

public class UserManagerImp implements UserManager {

    private UserDao mUserDao;

    public UserManagerImp() {
        mUserDao = DBManager.getDaoSession().getUserDao();
    }

    @Override
    public User getUserByName(String name) {
        List<User> localUsers = mUserDao.queryBuilder().where(UserDao.Properties.Name.eq(name)).list();
        if (localUsers.size() > 0 ) {
            return localUsers.get(0);
        }
        return null;
    }

    @Override
    public User getUserByUid(long uid) {
        return mUserDao.load(uid);
    }

    @Override
    public void saveUser(User user) {
        mUserDao.insertOrReplace(user);
    }
}

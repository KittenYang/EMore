package com.caij.emore.manager.imp;

import com.caij.emore.database.bean.Group;
import com.caij.emore.database.dao.GroupDao;
import com.caij.emore.manager.GroupManager;
import com.caij.emore.utils.db.DBManager;

import java.util.List;

/**
 * Created by Caij on 2016/11/2.
 */

public class GroupManagerImp implements GroupManager {

    private GroupDao mGroupDao;

    public GroupManagerImp() {
        mGroupDao = DBManager.getDaoSession().getGroupDao();
    }

    @Override
    public List<Group> getGroups() {
        return mGroupDao.queryBuilder().list();
    }

    @Override
    public void saveGroups(List<Group> groups) {
        mGroupDao.insertOrReplaceInTx(groups);
    }

    @Override
    public void clear() {
        mGroupDao.deleteAll();
    }
}

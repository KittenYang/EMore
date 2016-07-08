package com.caij.emore.database.dao;

import android.content.Context;

/**
 * Created by Caij on 2016/6/2.
 */
public class DBManager {

    private static DaoSession sDaoSession;

    public static void initDB(Context context, String dbName){
        DaoMaster.OpenHelper helper =
                new DaoMaster.DevOpenHelper(context, dbName, null);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        sDaoSession =  daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
    }
}

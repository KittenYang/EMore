package com.caij.emore.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.caij.emore.database.dao.DaoMaster;
import com.caij.emore.database.dao.DaoSession;

/**
 * Created by Caij on 2016/7/13.
 */
public class DBManager {

    private static DaoSession sDaoSession;

    public static void initDB(Context content, String dbName) {
        DBHelp dbHelp = new DBHelp(content, dbName, null);
        DaoMaster daoMaster = new DaoMaster(dbHelp.getWritableDatabase());
        sDaoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return sDaoSession;
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

package com.caij.emore.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.caij.emore.Key;
import com.caij.emore.database.dao.DaoMaster;
import com.caij.emore.database.dao.DaoSession;
import com.caij.emore.database.dao.GroupDao;

import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Caij on 2016/7/13.
 */
public class DBManager {

    private static DaoSession sDaoSession;

    public static void initDB(Context content, long uid, boolean isDebug) {
        sDaoSession = newDaoSession(content, uid, isDebug);
    }

    public static void close() {
        if (sDaoSession != null && sDaoSession.getDatabase().isOpen()) {
            sDaoSession.getDatabase().close();
        }
    }

    public static DaoSession newDaoSession(Context content, long uid, boolean isDebug) {
        String dbName =  Key.DB_NAME + uid;

        SQLiteDatabase db = null;
        if (isDebug) {
            DaoMaster.DevOpenHelper dbHelp = new DaoMaster.DevOpenHelper(content, dbName, null);
            db = dbHelp.getWritableDatabase();
        }else {
            DBHelp dbHelp = new DBHelp(content, dbName, null);
            db = dbHelp.getWritableDatabase();
        }
        DaoMaster daoMaster = new DaoMaster(db);
//        QueryBuilder.LOG_SQL = isDebug;
//        QueryBuilder.LOG_VALUES = isDebug;
        //这里参数为None 意思是数据不缓存在内存中
        return daoMaster.newSession(IdentityScopeType.None);
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

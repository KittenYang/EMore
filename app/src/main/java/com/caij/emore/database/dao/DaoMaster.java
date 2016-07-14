package com.caij.emore.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import com.caij.emore.database.dao.UserDao;
import com.caij.emore.database.dao.LocakImageDao;
import com.caij.emore.database.dao.LocalFileDao;
import com.caij.emore.database.dao.LikeBeanDao;
import com.caij.emore.database.dao.GeoDao;
import com.caij.emore.database.dao.WeiboDao;
import com.caij.emore.database.dao.PicUrlDao;
import com.caij.emore.database.dao.VisibleDao;
import com.caij.emore.database.dao.DirectMessageDao;
import com.caij.emore.database.dao.MessageImageDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 3): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 3;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        UserDao.createTable(db, ifNotExists);
        LocakImageDao.createTable(db, ifNotExists);
        LocalFileDao.createTable(db, ifNotExists);
        LikeBeanDao.createTable(db, ifNotExists);
        GeoDao.createTable(db, ifNotExists);
        WeiboDao.createTable(db, ifNotExists);
        PicUrlDao.createTable(db, ifNotExists);
        VisibleDao.createTable(db, ifNotExists);
        DirectMessageDao.createTable(db, ifNotExists);
        MessageImageDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        UserDao.dropTable(db, ifExists);
        LocakImageDao.dropTable(db, ifExists);
        LocalFileDao.dropTable(db, ifExists);
        LikeBeanDao.dropTable(db, ifExists);
        GeoDao.dropTable(db, ifExists);
        WeiboDao.dropTable(db, ifExists);
        PicUrlDao.dropTable(db, ifExists);
        VisibleDao.dropTable(db, ifExists);
        DirectMessageDao.dropTable(db, ifExists);
        MessageImageDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UserDao.class);
        registerDaoClass(LocakImageDao.class);
        registerDaoClass(LocalFileDao.class);
        registerDaoClass(LikeBeanDao.class);
        registerDaoClass(GeoDao.class);
        registerDaoClass(WeiboDao.class);
        registerDaoClass(PicUrlDao.class);
        registerDaoClass(VisibleDao.class);
        registerDaoClass(DirectMessageDao.class);
        registerDaoClass(MessageImageDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}

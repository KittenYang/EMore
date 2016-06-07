package com.caij.weiyo.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.caij.weiyo.database.bean.LocakImage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LOCAK_IMAGE".
*/
public class LocakImageDao extends AbstractDao<LocakImage, String> {

    public static final String TABLENAME = "LOCAK_IMAGE";

    /**
     * Properties of entity LocakImage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Url = new Property(0, String.class, "url", true, "URL");
        public final static Property Width = new Property(1, Integer.class, "width", false, "WIDTH");
        public final static Property Height = new Property(2, Integer.class, "height", false, "HEIGHT");
    };


    public LocakImageDao(DaoConfig config) {
        super(config);
    }
    
    public LocakImageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOCAK_IMAGE\" (" + //
                "\"URL\" TEXT PRIMARY KEY NOT NULL ," + // 0: url
                "\"WIDTH\" INTEGER," + // 1: width
                "\"HEIGHT\" INTEGER);"); // 2: height
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOCAK_IMAGE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, LocakImage entity) {
        stmt.clearBindings();
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(1, url);
        }
 
        Integer width = entity.getWidth();
        if (width != null) {
            stmt.bindLong(2, width);
        }
 
        Integer height = entity.getHeight();
        if (height != null) {
            stmt.bindLong(3, height);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public LocakImage readEntity(Cursor cursor, int offset) {
        LocakImage entity = new LocakImage( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // url
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // width
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2) // height
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, LocakImage entity, int offset) {
        entity.setUrl(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setWidth(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setHeight(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(LocakImage entity, long rowId) {
        return entity.getUrl();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(LocakImage entity) {
        if(entity != null) {
            return entity.getUrl();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

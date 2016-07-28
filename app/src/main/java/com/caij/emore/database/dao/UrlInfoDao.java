package com.caij.emore.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.caij.emore.database.bean.UrlInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "URL_INFO".
*/
public class UrlInfoDao extends AbstractDao<UrlInfo, String> {

    public static final String TABLENAME = "URL_INFO";

    /**
     * Properties of entity UrlInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property ShortUrl = new Property(0, String.class, "shortUrl", true, "SHORT_URL");
        public final static Property Url_info_json = new Property(1, String.class, "url_info_json", false, "URL_INFO_JSON");
    };


    public UrlInfoDao(DaoConfig config) {
        super(config);
    }
    
    public UrlInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"URL_INFO\" (" + //
                "\"SHORT_URL\" TEXT PRIMARY KEY NOT NULL ," + // 0: shortUrl
                "\"URL_INFO_JSON\" TEXT);"); // 1: url_info_json
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"URL_INFO\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UrlInfo entity) {
        stmt.clearBindings();
 
        String shortUrl = entity.getShortUrl();
        if (shortUrl != null) {
            stmt.bindString(1, shortUrl);
        }
 
        String url_info_json = entity.getUrl_info_json();
        if (url_info_json != null) {
            stmt.bindString(2, url_info_json);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public UrlInfo readEntity(Cursor cursor, int offset) {
        UrlInfo entity = new UrlInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // shortUrl
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1) // url_info_json
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UrlInfo entity, int offset) {
        entity.setShortUrl(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setUrl_info_json(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(UrlInfo entity, long rowId) {
        return entity.getShortUrl();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(UrlInfo entity) {
        if(entity != null) {
            return entity.getShortUrl();
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

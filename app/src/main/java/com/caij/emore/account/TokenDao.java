package com.caij.emore.account;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.caij.emore.account.Token;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TOKEN".
*/
public class TokenDao extends AbstractDao<Token, String> {

    public static final String TABLENAME = "TOKEN";

    /**
     * Properties of entity Token.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Key = new Property(0, String.class, "key", true, "KEY");
        public final static Property Uid = new Property(1, String.class, "uid", false, "UID");
        public final static Property Access_token = new Property(2, String.class, "access_token", false, "ACCESS_TOKEN");
        public final static Property Expires_in = new Property(3, Long.class, "expires_in", false, "EXPIRES_IN");
        public final static Property Create_at = new Property(4, Long.class, "create_at", false, "CREATE_AT");
    };


    public TokenDao(DaoConfig config) {
        super(config);
    }
    
    public TokenDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TOKEN\" (" + //
                "\"KEY\" TEXT PRIMARY KEY NOT NULL ," + // 0: key
                "\"UID\" TEXT," + // 1: uid
                "\"ACCESS_TOKEN\" TEXT," + // 2: access_token
                "\"EXPIRES_IN\" INTEGER," + // 3: expires_in
                "\"CREATE_AT\" INTEGER);"); // 4: create_at
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TOKEN\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Token entity) {
        stmt.clearBindings();
 
        String key = entity.getKey();
        if (key != null) {
            stmt.bindString(1, key);
        }
 
        String uid = entity.getUid();
        if (uid != null) {
            stmt.bindString(2, uid);
        }
 
        String access_token = entity.getAccess_token();
        if (access_token != null) {
            stmt.bindString(3, access_token);
        }
 
        Long expires_in = entity.getExpires_in();
        if (expires_in != null) {
            stmt.bindLong(4, expires_in);
        }
 
        Long create_at = entity.getCreate_at();
        if (create_at != null) {
            stmt.bindLong(5, create_at);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Token readEntity(Cursor cursor, int offset) {
        Token entity = new Token( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // key
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // access_token
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // expires_in
            cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4) // create_at
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Token entity, int offset) {
        entity.setKey(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setUid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAccess_token(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setExpires_in(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setCreate_at(cursor.isNull(offset + 4) ? null : cursor.getLong(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Token entity, long rowId) {
        return entity.getKey();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Token entity) {
        if(entity != null) {
            return entity.getKey();
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
package com.caij.emore.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.caij.emore.database.bean.Draft;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DRAFT".
*/
public class DraftDao extends AbstractDao<Draft, Long> {

    public static final String TABLENAME = "DRAFT";

    /**
     * Properties of entity Draft.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property Create_at = new Property(1, Long.class, "create_at", false, "CREATE_AT");
        public final static Property Status = new Property(2, Integer.class, "status", false, "STATUS");
        public final static Property Type = new Property(3, Integer.class, "type", false, "TYPE");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
        public final static Property Image_paths = new Property(5, String.class, "image_paths", false, "IMAGE_PATHS");
    };


    public DraftDao(DaoConfig config) {
        super(config);
    }
    
    public DraftDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DRAFT\" (" + //
                "\"ID\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CREATE_AT\" INTEGER," + // 1: create_at
                "\"STATUS\" INTEGER," + // 2: status
                "\"TYPE\" INTEGER," + // 3: type
                "\"CONTENT\" TEXT," + // 4: content
                "\"IMAGE_PATHS\" TEXT);"); // 5: image_paths
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DRAFT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Draft entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long create_at = entity.getCreate_at();
        if (create_at != null) {
            stmt.bindLong(2, create_at);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(3, status);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(4, type);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
 
        String image_paths = entity.getImage_paths();
        if (image_paths != null) {
            stmt.bindString(6, image_paths);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Draft readEntity(Cursor cursor, int offset) {
        Draft entity = new Draft( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // create_at
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // status
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // content
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // image_paths
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Draft entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCreate_at(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setStatus(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setType(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setImage_paths(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Draft entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Draft entity) {
        if(entity != null) {
            return entity.getId();
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
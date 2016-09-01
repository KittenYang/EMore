package com.caij.emore.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.caij.emore.database.bean.Weibo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WEIBO".
*/
public class WeiboDao extends AbstractDao<Weibo, Long> {

    public static final String TABLENAME = "WEIBO";

    /**
     * Properties of entity Weibo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Created_at = new Property(0, java.util.Date.class, "created_at", false, "CREATED_AT");
        public final static Property Id = new Property(1, Long.class, "id", true, "ID");
        public final static Property Mid = new Property(2, Long.class, "mid", false, "MID");
        public final static Property Idstr = new Property(3, String.class, "idstr", false, "IDSTR");
        public final static Property Text = new Property(4, String.class, "text", false, "TEXT");
        public final static Property Source = new Property(5, String.class, "source", false, "SOURCE");
        public final static Property Favorited = new Property(6, Boolean.class, "favorited", false, "FAVORITED");
        public final static Property Truncated = new Property(7, Boolean.class, "truncated", false, "TRUNCATED");
        public final static Property In_reply_to_status_id = new Property(8, String.class, "in_reply_to_status_id", false, "IN_REPLY_TO_STATUS_ID");
        public final static Property In_reply_to_user_id = new Property(9, String.class, "in_reply_to_user_id", false, "IN_REPLY_TO_USER_ID");
        public final static Property In_reply_to_screen_name = new Property(10, String.class, "in_reply_to_screen_name", false, "IN_REPLY_TO_SCREEN_NAME");
        public final static Property Thumbnail_pic = new Property(11, String.class, "thumbnail_pic", false, "THUMBNAIL_PIC");
        public final static Property Bmiddle_pic = new Property(12, String.class, "bmiddle_pic", false, "BMIDDLE_PIC");
        public final static Property Original_pic = new Property(13, String.class, "original_pic", false, "ORIGINAL_PIC");
        public final static Property Reposts_count = new Property(14, Integer.class, "reposts_count", false, "REPOSTS_COUNT");
        public final static Property Comments_count = new Property(15, Integer.class, "comments_count", false, "COMMENTS_COUNT");
        public final static Property Attitudes_count = new Property(16, Integer.class, "attitudes_count", false, "ATTITUDES_COUNT");
        public final static Property Mlevel = new Property(17, Integer.class, "mlevel", false, "MLEVEL");
        public final static Property Update_time = new Property(18, Long.class, "update_time", false, "UPDATE_TIME");
        public final static Property Attitudes_status = new Property(19, Integer.class, "attitudes_status", false, "ATTITUDES_STATUS");
        public final static Property IsLongText = new Property(20, Boolean.class, "isLongText", false, "IS_LONG_TEXT");
        public final static Property Geo_json_string = new Property(21, String.class, "geo_json_string", false, "GEO_JSON_STRING");
        public final static Property Visible_json_string = new Property(22, String.class, "visible_json_string", false, "VISIBLE_JSON_STRING");
        public final static Property Url_struct_json_string = new Property(23, String.class, "url_struct_json_string", false, "URL_STRUCT_JSON_STRING");
        public final static Property Pic_ids_json_string = new Property(24, String.class, "pic_ids_json_string", false, "PIC_IDS_JSON_STRING");
        public final static Property Pic_infos_json_string = new Property(25, String.class, "pic_infos_json_string", false, "PIC_INFOS_JSON_STRING");
        public final static Property Long_text_json_string = new Property(26, String.class, "long_text_json_string", false, "LONG_TEXT_JSON_STRING");
        public final static Property Page_info_json_string = new Property(27, String.class, "page_info_json_string", false, "PAGE_INFO_JSON_STRING");
        public final static Property Retweeted_status_id = new Property(28, Long.class, "retweeted_status_id", false, "RETWEETED_STATUS_ID");
        public final static Property User_id = new Property(29, Long.class, "user_id", false, "USER_ID");
    };


    public WeiboDao(DaoConfig config) {
        super(config);
    }
    
    public WeiboDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WEIBO\" (" + //
                "\"CREATED_AT\" INTEGER," + // 0: created_at
                "\"ID\" INTEGER PRIMARY KEY ," + // 1: id
                "\"MID\" INTEGER," + // 2: mid
                "\"IDSTR\" TEXT," + // 3: idstr
                "\"TEXT\" TEXT," + // 4: text
                "\"SOURCE\" TEXT," + // 5: source
                "\"FAVORITED\" INTEGER," + // 6: favorited
                "\"TRUNCATED\" INTEGER," + // 7: truncated
                "\"IN_REPLY_TO_STATUS_ID\" TEXT," + // 8: in_reply_to_status_id
                "\"IN_REPLY_TO_USER_ID\" TEXT," + // 9: in_reply_to_user_id
                "\"IN_REPLY_TO_SCREEN_NAME\" TEXT," + // 10: in_reply_to_screen_name
                "\"THUMBNAIL_PIC\" TEXT," + // 11: thumbnail_pic
                "\"BMIDDLE_PIC\" TEXT," + // 12: bmiddle_pic
                "\"ORIGINAL_PIC\" TEXT," + // 13: original_pic
                "\"REPOSTS_COUNT\" INTEGER," + // 14: reposts_count
                "\"COMMENTS_COUNT\" INTEGER," + // 15: comments_count
                "\"ATTITUDES_COUNT\" INTEGER," + // 16: attitudes_count
                "\"MLEVEL\" INTEGER," + // 17: mlevel
                "\"UPDATE_TIME\" INTEGER," + // 18: update_time
                "\"ATTITUDES_STATUS\" INTEGER," + // 19: attitudes_status
                "\"IS_LONG_TEXT\" INTEGER," + // 20: isLongText
                "\"GEO_JSON_STRING\" TEXT," + // 21: geo_json_string
                "\"VISIBLE_JSON_STRING\" TEXT," + // 22: visible_json_string
                "\"URL_STRUCT_JSON_STRING\" TEXT," + // 23: url_struct_json_string
                "\"PIC_IDS_JSON_STRING\" TEXT," + // 24: pic_ids_json_string
                "\"PIC_INFOS_JSON_STRING\" TEXT," + // 25: pic_infos_json_string
                "\"LONG_TEXT_JSON_STRING\" TEXT," + // 26: long_text_json_string
                "\"PAGE_INFO_JSON_STRING\" TEXT," + // 27: page_info_json_string
                "\"RETWEETED_STATUS_ID\" INTEGER," + // 28: retweeted_status_id
                "\"USER_ID\" INTEGER);"); // 29: user_id
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WEIBO\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Weibo entity) {
        stmt.clearBindings();
 
        java.util.Date created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindLong(1, created_at.getTime());
        }
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id);
        }
 
        Long mid = entity.getMid();
        if (mid != null) {
            stmt.bindLong(3, mid);
        }
 
        String idstr = entity.getIdstr();
        if (idstr != null) {
            stmt.bindString(4, idstr);
        }
 
        String text = entity.getText();
        if (text != null) {
            stmt.bindString(5, text);
        }
 
        String source = entity.getSource();
        if (source != null) {
            stmt.bindString(6, source);
        }
 
        Boolean favorited = entity.getFavorited();
        if (favorited != null) {
            stmt.bindLong(7, favorited ? 1L: 0L);
        }
 
        Boolean truncated = entity.getTruncated();
        if (truncated != null) {
            stmt.bindLong(8, truncated ? 1L: 0L);
        }
 
        String in_reply_to_status_id = entity.getIn_reply_to_status_id();
        if (in_reply_to_status_id != null) {
            stmt.bindString(9, in_reply_to_status_id);
        }
 
        String in_reply_to_user_id = entity.getIn_reply_to_user_id();
        if (in_reply_to_user_id != null) {
            stmt.bindString(10, in_reply_to_user_id);
        }
 
        String in_reply_to_screen_name = entity.getIn_reply_to_screen_name();
        if (in_reply_to_screen_name != null) {
            stmt.bindString(11, in_reply_to_screen_name);
        }
 
        String thumbnail_pic = entity.getThumbnail_pic();
        if (thumbnail_pic != null) {
            stmt.bindString(12, thumbnail_pic);
        }
 
        String bmiddle_pic = entity.getBmiddle_pic();
        if (bmiddle_pic != null) {
            stmt.bindString(13, bmiddle_pic);
        }
 
        String original_pic = entity.getOriginal_pic();
        if (original_pic != null) {
            stmt.bindString(14, original_pic);
        }
 
        Integer reposts_count = entity.getReposts_count();
        if (reposts_count != null) {
            stmt.bindLong(15, reposts_count);
        }
 
        Integer comments_count = entity.getComments_count();
        if (comments_count != null) {
            stmt.bindLong(16, comments_count);
        }
 
        Integer attitudes_count = entity.getAttitudes_count();
        if (attitudes_count != null) {
            stmt.bindLong(17, attitudes_count);
        }
 
        Integer mlevel = entity.getMlevel();
        if (mlevel != null) {
            stmt.bindLong(18, mlevel);
        }
 
        Long update_time = entity.getUpdate_time();
        if (update_time != null) {
            stmt.bindLong(19, update_time);
        }
 
        Integer attitudes_status = entity.getAttitudes_status();
        if (attitudes_status != null) {
            stmt.bindLong(20, attitudes_status);
        }
 
        Boolean isLongText = entity.getIsLongText();
        if (isLongText != null) {
            stmt.bindLong(21, isLongText ? 1L: 0L);
        }
 
        String geo_json_string = entity.getGeo_json_string();
        if (geo_json_string != null) {
            stmt.bindString(22, geo_json_string);
        }
 
        String visible_json_string = entity.getVisible_json_string();
        if (visible_json_string != null) {
            stmt.bindString(23, visible_json_string);
        }
 
        String url_struct_json_string = entity.getUrl_struct_json_string();
        if (url_struct_json_string != null) {
            stmt.bindString(24, url_struct_json_string);
        }
 
        String pic_ids_json_string = entity.getPic_ids_json_string();
        if (pic_ids_json_string != null) {
            stmt.bindString(25, pic_ids_json_string);
        }
 
        String pic_infos_json_string = entity.getPic_infos_json_string();
        if (pic_infos_json_string != null) {
            stmt.bindString(26, pic_infos_json_string);
        }
 
        String long_text_json_string = entity.getLong_text_json_string();
        if (long_text_json_string != null) {
            stmt.bindString(27, long_text_json_string);
        }
 
        String page_info_json_string = entity.getPage_info_json_string();
        if (page_info_json_string != null) {
            stmt.bindString(28, page_info_json_string);
        }
 
        Long retweeted_status_id = entity.getRetweeted_status_id();
        if (retweeted_status_id != null) {
            stmt.bindLong(29, retweeted_status_id);
        }
 
        Long user_id = entity.getUser_id();
        if (user_id != null) {
            stmt.bindLong(30, user_id);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1);
    }    

    /** @inheritdoc */
    @Override
    public Weibo readEntity(Cursor cursor, int offset) {
        Weibo entity = new Weibo( //
            cursor.isNull(offset + 0) ? null : new java.util.Date(cursor.getLong(offset + 0)), // created_at
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // mid
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // idstr
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // text
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // source
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // favorited
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0, // truncated
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // in_reply_to_status_id
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // in_reply_to_user_id
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // in_reply_to_screen_name
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // thumbnail_pic
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // bmiddle_pic
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // original_pic
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // reposts_count
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15), // comments_count
            cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16), // attitudes_count
            cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17), // mlevel
            cursor.isNull(offset + 18) ? null : cursor.getLong(offset + 18), // update_time
            cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19), // attitudes_status
            cursor.isNull(offset + 20) ? null : cursor.getShort(offset + 20) != 0, // isLongText
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // geo_json_string
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // visible_json_string
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // url_struct_json_string
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // pic_ids_json_string
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // pic_infos_json_string
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // long_text_json_string
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // page_info_json_string
            cursor.isNull(offset + 28) ? null : cursor.getLong(offset + 28), // retweeted_status_id
            cursor.isNull(offset + 29) ? null : cursor.getLong(offset + 29) // user_id
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Weibo entity, int offset) {
        entity.setCreated_at(cursor.isNull(offset + 0) ? null : new java.util.Date(cursor.getLong(offset + 0)));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setMid(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setIdstr(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setText(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setSource(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFavorited(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setTruncated(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
        entity.setIn_reply_to_status_id(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setIn_reply_to_user_id(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIn_reply_to_screen_name(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setThumbnail_pic(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setBmiddle_pic(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setOriginal_pic(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setReposts_count(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setComments_count(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
        entity.setAttitudes_count(cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16));
        entity.setMlevel(cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17));
        entity.setUpdate_time(cursor.isNull(offset + 18) ? null : cursor.getLong(offset + 18));
        entity.setAttitudes_status(cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19));
        entity.setIsLongText(cursor.isNull(offset + 20) ? null : cursor.getShort(offset + 20) != 0);
        entity.setGeo_json_string(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setVisible_json_string(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setUrl_struct_json_string(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setPic_ids_json_string(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setPic_infos_json_string(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setLong_text_json_string(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setPage_info_json_string(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setRetweeted_status_id(cursor.isNull(offset + 28) ? null : cursor.getLong(offset + 28));
        entity.setUser_id(cursor.isNull(offset + 29) ? null : cursor.getLong(offset + 29));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Weibo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Weibo entity) {
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

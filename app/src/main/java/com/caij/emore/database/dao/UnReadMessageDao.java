package com.caij.emore.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.caij.emore.database.bean.UnReadMessage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "UN_READ_MESSAGE".
*/
public class UnReadMessageDao extends AbstractDao<UnReadMessage, Long> {

    public static final String TABLENAME = "UN_READ_MESSAGE";

    /**
     * Properties of entity UnReadMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Uid = new Property(0, Long.class, "uid", true, "UID");
        public final static Property Cmt = new Property(1, Integer.class, "cmt", false, "CMT");
        public final static Property Dm = new Property(2, Integer.class, "dm", false, "DM");
        public final static Property Chat_group_client = new Property(3, Integer.class, "chat_group_client", false, "CHAT_GROUP_CLIENT");
        public final static Property Mention_status = new Property(4, Integer.class, "mention_status", false, "MENTION_STATUS");
        public final static Property Mention_cmt = new Property(5, Integer.class, "mention_cmt", false, "MENTION_CMT");
        public final static Property Invite = new Property(6, Integer.class, "invite", false, "INVITE");
        public final static Property Attitude = new Property(7, Integer.class, "attitude", false, "ATTITUDE");
        public final static Property Msgbox = new Property(8, Integer.class, "msgbox", false, "MSGBOX");
        public final static Property Common_attitude = new Property(9, Integer.class, "common_attitude", false, "COMMON_ATTITUDE");
        public final static Property Page_follower = new Property(10, Integer.class, "page_follower", false, "PAGE_FOLLOWER");
        public final static Property All_mention_status = new Property(11, Integer.class, "all_mention_status", false, "ALL_MENTION_STATUS");
        public final static Property Attention_mention_status = new Property(12, Integer.class, "attention_mention_status", false, "ATTENTION_MENTION_STATUS");
        public final static Property All_mention_cmt = new Property(13, Integer.class, "all_mention_cmt", false, "ALL_MENTION_CMT");
        public final static Property Attention_mention_cmt = new Property(14, Integer.class, "attention_mention_cmt", false, "ATTENTION_MENTION_CMT");
        public final static Property All_cmt = new Property(15, Integer.class, "all_cmt", false, "ALL_CMT");
        public final static Property Attention_cmt = new Property(16, Integer.class, "attention_cmt", false, "ATTENTION_CMT");
        public final static Property Attention_follower = new Property(17, Integer.class, "attention_follower", false, "ATTENTION_FOLLOWER");
        public final static Property Chat_group_notice = new Property(18, Integer.class, "chat_group_notice", false, "CHAT_GROUP_NOTICE");
        public final static Property Hot_status = new Property(19, Integer.class, "hot_status", false, "HOT_STATUS");
        public final static Property Status = new Property(20, Integer.class, "status", false, "STATUS");
        public final static Property Follower = new Property(21, Integer.class, "follower", false, "FOLLOWER");
        public final static Property Dm_single = new Property(22, Integer.class, "dm_single", false, "DM_SINGLE");
    };


    public UnReadMessageDao(DaoConfig config) {
        super(config);
    }
    
    public UnReadMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"UN_READ_MESSAGE\" (" + //
                "\"UID\" INTEGER PRIMARY KEY ," + // 0: uid
                "\"CMT\" INTEGER," + // 1: cmt
                "\"DM\" INTEGER," + // 2: dm
                "\"CHAT_GROUP_CLIENT\" INTEGER," + // 3: chat_group_client
                "\"MENTION_STATUS\" INTEGER," + // 4: mention_status
                "\"MENTION_CMT\" INTEGER," + // 5: mention_cmt
                "\"INVITE\" INTEGER," + // 6: invite
                "\"ATTITUDE\" INTEGER," + // 7: attitude
                "\"MSGBOX\" INTEGER," + // 8: msgbox
                "\"COMMON_ATTITUDE\" INTEGER," + // 9: common_attitude
                "\"PAGE_FOLLOWER\" INTEGER," + // 10: page_follower
                "\"ALL_MENTION_STATUS\" INTEGER," + // 11: all_mention_status
                "\"ATTENTION_MENTION_STATUS\" INTEGER," + // 12: attention_mention_status
                "\"ALL_MENTION_CMT\" INTEGER," + // 13: all_mention_cmt
                "\"ATTENTION_MENTION_CMT\" INTEGER," + // 14: attention_mention_cmt
                "\"ALL_CMT\" INTEGER," + // 15: all_cmt
                "\"ATTENTION_CMT\" INTEGER," + // 16: attention_cmt
                "\"ATTENTION_FOLLOWER\" INTEGER," + // 17: attention_follower
                "\"CHAT_GROUP_NOTICE\" INTEGER," + // 18: chat_group_notice
                "\"HOT_STATUS\" INTEGER," + // 19: hot_status
                "\"STATUS\" INTEGER," + // 20: status
                "\"FOLLOWER\" INTEGER," + // 21: follower
                "\"DM_SINGLE\" INTEGER);"); // 22: dm_single
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"UN_READ_MESSAGE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UnReadMessage entity) {
        stmt.clearBindings();
 
        Long uid = entity.getUid();
        if (uid != null) {
            stmt.bindLong(1, uid);
        }
 
        Integer cmt = entity.getCmt();
        if (cmt != null) {
            stmt.bindLong(2, cmt);
        }
 
        Integer dm = entity.getDm();
        if (dm != null) {
            stmt.bindLong(3, dm);
        }
 
        Integer chat_group_client = entity.getChat_group_client();
        if (chat_group_client != null) {
            stmt.bindLong(4, chat_group_client);
        }
 
        Integer mention_status = entity.getMention_status();
        if (mention_status != null) {
            stmt.bindLong(5, mention_status);
        }
 
        Integer mention_cmt = entity.getMention_cmt();
        if (mention_cmt != null) {
            stmt.bindLong(6, mention_cmt);
        }
 
        Integer invite = entity.getInvite();
        if (invite != null) {
            stmt.bindLong(7, invite);
        }
 
        Integer attitude = entity.getAttitude();
        if (attitude != null) {
            stmt.bindLong(8, attitude);
        }
 
        Integer msgbox = entity.getMsgbox();
        if (msgbox != null) {
            stmt.bindLong(9, msgbox);
        }
 
        Integer common_attitude = entity.getCommon_attitude();
        if (common_attitude != null) {
            stmt.bindLong(10, common_attitude);
        }
 
        Integer page_follower = entity.getPage_follower();
        if (page_follower != null) {
            stmt.bindLong(11, page_follower);
        }
 
        Integer all_mention_status = entity.getAll_mention_status();
        if (all_mention_status != null) {
            stmt.bindLong(12, all_mention_status);
        }
 
        Integer attention_mention_status = entity.getAttention_mention_status();
        if (attention_mention_status != null) {
            stmt.bindLong(13, attention_mention_status);
        }
 
        Integer all_mention_cmt = entity.getAll_mention_cmt();
        if (all_mention_cmt != null) {
            stmt.bindLong(14, all_mention_cmt);
        }
 
        Integer attention_mention_cmt = entity.getAttention_mention_cmt();
        if (attention_mention_cmt != null) {
            stmt.bindLong(15, attention_mention_cmt);
        }
 
        Integer all_cmt = entity.getAll_cmt();
        if (all_cmt != null) {
            stmt.bindLong(16, all_cmt);
        }
 
        Integer attention_cmt = entity.getAttention_cmt();
        if (attention_cmt != null) {
            stmt.bindLong(17, attention_cmt);
        }
 
        Integer attention_follower = entity.getAttention_follower();
        if (attention_follower != null) {
            stmt.bindLong(18, attention_follower);
        }
 
        Integer chat_group_notice = entity.getChat_group_notice();
        if (chat_group_notice != null) {
            stmt.bindLong(19, chat_group_notice);
        }
 
        Integer hot_status = entity.getHot_status();
        if (hot_status != null) {
            stmt.bindLong(20, hot_status);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(21, status);
        }
 
        Integer follower = entity.getFollower();
        if (follower != null) {
            stmt.bindLong(22, follower);
        }
 
        Integer dm_single = entity.getDm_single();
        if (dm_single != null) {
            stmt.bindLong(23, dm_single);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public UnReadMessage readEntity(Cursor cursor, int offset) {
        UnReadMessage entity = new UnReadMessage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // uid
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // cmt
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // dm
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // chat_group_client
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // mention_status
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // mention_cmt
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // invite
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // attitude
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // msgbox
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // common_attitude
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // page_follower
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // all_mention_status
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // attention_mention_status
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // all_mention_cmt
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // attention_mention_cmt
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15), // all_cmt
            cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16), // attention_cmt
            cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17), // attention_follower
            cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // chat_group_notice
            cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19), // hot_status
            cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20), // status
            cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21), // follower
            cursor.isNull(offset + 22) ? null : cursor.getInt(offset + 22) // dm_single
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UnReadMessage entity, int offset) {
        entity.setUid(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCmt(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setDm(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setChat_group_client(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setMention_status(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setMention_cmt(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setInvite(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setAttitude(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setMsgbox(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setCommon_attitude(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setPage_follower(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setAll_mention_status(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setAttention_mention_status(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setAll_mention_cmt(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setAttention_mention_cmt(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setAll_cmt(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
        entity.setAttention_cmt(cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16));
        entity.setAttention_follower(cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17));
        entity.setChat_group_notice(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setHot_status(cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19));
        entity.setStatus(cursor.isNull(offset + 20) ? null : cursor.getInt(offset + 20));
        entity.setFollower(cursor.isNull(offset + 21) ? null : cursor.getInt(offset + 21));
        entity.setDm_single(cursor.isNull(offset + 22) ? null : cursor.getInt(offset + 22));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(UnReadMessage entity, long rowId) {
        entity.setUid(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(UnReadMessage entity) {
        if(entity != null) {
            return entity.getUid();
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

package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.content.Intent;

/**
 * Entity mapped to table "UN_READ_MESSAGE".
 */
public class UnReadMessage {

    private Long uid;
    private Integer cmt;
    private Integer dm;
    private Integer chat_group_client;
    private Integer mention_status;
    private Integer mention_cmt;
    private Integer invite;
    private Integer attitude;
    private Integer msgbox;
    private Integer common_attitude;
    private Integer page_follower;
    private Integer all_mention_status;
    private Integer attention_mention_status;
    private Integer all_mention_cmt;
    private Integer attention_mention_cmt;
    private Integer all_cmt;
    private Integer attention_cmt;
    private Integer attention_follower;
    private Integer chat_group_notice;
    private Integer hot_status;
    private Integer status;
    private Integer follower;
    private Integer dm_single;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public UnReadMessage() {
    }

    public UnReadMessage(Long uid) {
        this.uid = uid;
    }

    public UnReadMessage(Long uid, Integer cmt, Integer dm, Integer chat_group_client, Integer mention_status,
                         Integer mention_cmt, Integer invite, Integer attitude, Integer msgbox,
                         Integer common_attitude, Integer page_follower, Integer all_mention_status,
                         Integer attention_mention_status, Integer all_mention_cmt, Integer attention_mention_cmt,
                         Integer all_cmt, Integer attention_cmt, Integer attention_follower, Integer chat_group_notice,
                         Integer hot_status, Integer status, Integer follower, Integer dm_single) {
        this.uid = uid;
        this.cmt = cmt;
        this.dm = dm;
        this.chat_group_client = chat_group_client;
        this.mention_status = mention_status;
        this.mention_cmt = mention_cmt;
        this.invite = invite;
        this.attitude = attitude;
        this.msgbox = msgbox;
        this.common_attitude = common_attitude;
        this.page_follower = page_follower;
        this.all_mention_status = all_mention_status;
        this.attention_mention_status = attention_mention_status;
        this.all_mention_cmt = all_mention_cmt;
        this.attention_mention_cmt = attention_mention_cmt;
        this.all_cmt = all_cmt;
        this.attention_cmt = attention_cmt;
        this.attention_follower = attention_follower;
        this.chat_group_notice = chat_group_notice;
        this.hot_status = hot_status;
        this.status = status;
        this.follower = follower;
        this.dm_single = dm_single;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getCmt() {
        if (cmt == null || cmt < 0) {
            return 0;
        }
        return cmt;
    }

    public void setCmt(Integer cmt) {
        this.cmt = cmt;
    }

    public Integer getDm() {
        if (dm == null || dm < 0) {
            return 0;
        }
        return dm;
    }

    public void setDm(Integer dm) {
        this.dm = dm;
    }

    public Integer getChat_group_client() {
        return chat_group_client;
    }

    public void setChat_group_client(Integer chat_group_client) {
        this.chat_group_client = chat_group_client;
    }

    public Integer getMention_status() {
        if (mention_status == null || mention_status < 0) {
            return 0;
        }
        return mention_status;
    }

    public void setMention_status(Integer mention_status) {
        this.mention_status = mention_status;
    }

    public Integer getMention_cmt() {
        if (mention_cmt == null || mention_cmt < 0) {
            return 0;
        }
        return mention_cmt;
    }

    public void setMention_cmt(Integer mention_cmt) {
        this.mention_cmt = mention_cmt;
    }

    public Integer getInvite() {
        return invite;
    }

    public void setInvite(Integer invite) {
        this.invite = invite;
    }

    public Integer getAttitude() {
        if (attitude == null || attitude < 0) {
            return 0;
        }
        return attitude;
    }

    public void setAttitude(Integer attitude) {
        this.attitude = attitude;
    }

    public Integer getMsgbox() {
        return msgbox;
    }

    public void setMsgbox(Integer msgbox) {
        this.msgbox = msgbox;
    }

    public Integer getCommon_attitude() {
        return common_attitude;
    }

    public void setCommon_attitude(Integer common_attitude) {
        this.common_attitude = common_attitude;
    }

    public Integer getPage_follower() {
        return page_follower;
    }

    public void setPage_follower(Integer page_follower) {
        this.page_follower = page_follower;
    }

    public Integer getAll_mention_status() {
        return all_mention_status;
    }

    public void setAll_mention_status(Integer all_mention_status) {
        this.all_mention_status = all_mention_status;
    }

    public Integer getAttention_mention_status() {
        return attention_mention_status;
    }

    public void setAttention_mention_status(Integer attention_mention_status) {
        this.attention_mention_status = attention_mention_status;
    }

    public Integer getAll_mention_cmt() {
        return all_mention_cmt;
    }

    public void setAll_mention_cmt(Integer all_mention_cmt) {
        this.all_mention_cmt = all_mention_cmt;
    }

    public Integer getAttention_mention_cmt() {
        return attention_mention_cmt;
    }

    public void setAttention_mention_cmt(Integer attention_mention_cmt) {
        this.attention_mention_cmt = attention_mention_cmt;
    }

    public Integer getAll_cmt() {
        return all_cmt;
    }

    public void setAll_cmt(Integer all_cmt) {
        this.all_cmt = all_cmt;
    }

    public Integer getAttention_cmt() {
        return attention_cmt;
    }

    public void setAttention_cmt(Integer attention_cmt) {
        this.attention_cmt = attention_cmt;
    }

    public Integer getAttention_follower() {
        return attention_follower;
    }

    public void setAttention_follower(Integer attention_follower) {
        this.attention_follower = attention_follower;
    }

    public Integer getChat_group_notice() {
        return chat_group_notice;
    }

    public void setChat_group_notice(Integer chat_group_notice) {
        this.chat_group_notice = chat_group_notice;
    }

    public Integer getHot_status() {
        return hot_status;
    }

    public void setHot_status(Integer hot_status) {
        this.hot_status = hot_status;
    }

    public Integer getStatus() {
        if (status == null || status < 0) {
            return 0;
        }
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getFollower() {
        return follower;
    }

    public void setFollower(Integer follower) {
        this.follower = follower;
    }

    public Integer getDm_single() {
        if (dm_single == null || dm_single < 0) {
            return 0;
        }
        return dm_single;
    }

    public void setDm_single(Integer dm_single) {
        this.dm_single = dm_single;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

    public static final String TYPE_MENTION_STATUS = "mention_status";
    public static final String TYPE_MENTION_CMT = "mention_cmt";
    public static final String TYPE_CMT = "cmt";
    public static final String TYPE_STATUS = "ststus";
    public static final String TYPE_DM = "dm";

}

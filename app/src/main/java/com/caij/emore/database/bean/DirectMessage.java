package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import java.util.List;

/**
 * Entity mapped to table "DIRECT_MESSAGE".
 */
public class DirectMessage {

    private Long id;
    private String idstr;
    private String created_at;
    private String text;
    private Integer sys_type;
    private Integer msg_status;
    private Long sender_id;
    private Long recipient_id;
    private String sender_screen_name;
    private String recipient_screen_name;
    private String mid;
    private Boolean isLargeDm;
    private String source;
    private Long status_id;
    private Integer dm_type;
    private Integer media_type;
    private Long ip;
    private Long burn_time;
    private Boolean matchKeyword;
    private Boolean topublic;
    private Boolean pushToMPS;
    private Long oriImageId;
    private Long geo_id;
    private Integer local_status = STATUS_SERVER;
    private Long created_at_long;
    private String att_ids_json;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public DirectMessage() {
    }

    public DirectMessage(Long id) {
        this.id = id;
    }

    public DirectMessage(Long id, String idstr, String created_at, String text, Integer sys_type, Integer msg_status, Long sender_id, Long recipient_id, String sender_screen_name, String recipient_screen_name, String mid, Boolean isLargeDm, String source, Long status_id, Integer dm_type, Integer media_type, Long ip, Long burn_time, Boolean matchKeyword, Boolean topublic, Boolean pushToMPS, Long oriImageId, Long geo_id, Integer local_status, Long created_at_long, String att_ids_json) {
        this.id = id;
        this.idstr = idstr;
        this.created_at = created_at;
        this.text = text;
        this.sys_type = sys_type;
        this.msg_status = msg_status;
        this.sender_id = sender_id;
        this.recipient_id = recipient_id;
        this.sender_screen_name = sender_screen_name;
        this.recipient_screen_name = recipient_screen_name;
        this.mid = mid;
        this.isLargeDm = isLargeDm;
        this.source = source;
        this.status_id = status_id;
        this.dm_type = dm_type;
        this.media_type = media_type;
        this.ip = ip;
        this.burn_time = burn_time;
        this.matchKeyword = matchKeyword;
        this.topublic = topublic;
        this.pushToMPS = pushToMPS;
        this.oriImageId = oriImageId;
        this.geo_id = geo_id;
        this.local_status = local_status;
        this.created_at_long = created_at_long;
        this.att_ids_json = att_ids_json;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getSys_type() {
        return sys_type;
    }

    public void setSys_type(Integer sys_type) {
        this.sys_type = sys_type;
    }

    public Integer getMsg_status() {
        return msg_status;
    }

    public void setMsg_status(Integer msg_status) {
        this.msg_status = msg_status;
    }

    public Long getSender_id() {
        return sender_id;
    }

    public void setSender_id(Long sender_id) {
        this.sender_id = sender_id;
    }

    public Long getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(Long recipient_id) {
        this.recipient_id = recipient_id;
    }

    public String getSender_screen_name() {
        return sender_screen_name;
    }

    public void setSender_screen_name(String sender_screen_name) {
        this.sender_screen_name = sender_screen_name;
    }

    public String getRecipient_screen_name() {
        return recipient_screen_name;
    }

    public void setRecipient_screen_name(String recipient_screen_name) {
        this.recipient_screen_name = recipient_screen_name;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Boolean getIsLargeDm() {
        return isLargeDm;
    }

    public void setIsLargeDm(Boolean isLargeDm) {
        this.isLargeDm = isLargeDm;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Long status_id) {
        this.status_id = status_id;
    }

    public Integer getDm_type() {
        return dm_type;
    }

    public void setDm_type(Integer dm_type) {
        this.dm_type = dm_type;
    }

    public Integer getMedia_type() {
        return media_type;
    }

    public void setMedia_type(Integer media_type) {
        this.media_type = media_type;
    }

    public Long getIp() {
        return ip;
    }

    public void setIp(Long ip) {
        this.ip = ip;
    }

    public Long getBurn_time() {
        return burn_time;
    }

    public void setBurn_time(Long burn_time) {
        this.burn_time = burn_time;
    }

    public Boolean getMatchKeyword() {
        return matchKeyword;
    }

    public void setMatchKeyword(Boolean matchKeyword) {
        this.matchKeyword = matchKeyword;
    }

    public Boolean getTopublic() {
        return topublic;
    }

    public void setTopublic(Boolean topublic) {
        this.topublic = topublic;
    }

    public Boolean getPushToMPS() {
        return pushToMPS;
    }

    public void setPushToMPS(Boolean pushToMPS) {
        this.pushToMPS = pushToMPS;
    }

    public Long getOriImageId() {
        return oriImageId;
    }

    public void setOriImageId(Long oriImageId) {
        this.oriImageId = oriImageId;
    }

    public Long getGeo_id() {
        return geo_id;
    }

    public void setGeo_id(Long geo_id) {
        this.geo_id = geo_id;
    }

    public Integer getLocal_status() {
        return local_status;
    }

    public void setLocal_status(Integer local_status) {
        this.local_status = local_status;
    }

    public Long getCreated_at_long() {
        return created_at_long;
    }

    public void setCreated_at_long(Long created_at_long) {
        this.created_at_long = created_at_long;
    }

    public String getAtt_ids_json() {
        return att_ids_json;
    }

    public void setAtt_ids_json(String att_ids_json) {
        this.att_ids_json = att_ids_json;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL = 2;
    public static final int STATUS_SEND = 3;
    public static final int STATUS_SERVER = 4;

    private List<Long> att_ids;

    private User sender;
    private User recipient;
    private Geo geo;
    private LocakImage locakImage;

    public List<Long> getAtt_ids() {
        return att_ids;
    }

    public void setAtt_ids(List<Long> att_ids) {
        this.att_ids = att_ids;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public LocakImage getLocakImage() {
        return locakImage;
    }

    public void setLocakImage(LocakImage locakImage) {
        this.locakImage = locakImage;
    }

    @Override
    public boolean equals(Object o) {
        DirectMessage message = (DirectMessage) o;
        return message.id.equals(id);
    }
}

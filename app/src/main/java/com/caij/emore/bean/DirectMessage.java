//package com.caij.emore.bean;
//
//import com.caij.emore.database.bean.Geo;
//import com.caij.emore.database.bean.User;
//
//import java.util.List;
//
///**
// * Created by Caij on 2016/7/10.
// */
//public class DirectMessage {
//
//    public static final int STATUS_SUCCESS = 1;
//    public static final int STATUS_FAIL = 2;
//    public static final int STATUS_SEND = 3;
//    public static final int STATUS_SERVER = 4;
//
//    private long id;
//    private String idstr;
//    private String created_at;
//    private String text;
//    private int sys_type;
//    private int msg_status;
//    private long sender_id;
//    private long recipient_id;
//    private String sender_screen_name;
//    private String recipient_screen_name;
//
//    private String mid;
//    private boolean isLargeDm;
//    private String source;
//    private long status_id;
//    private int dm_type;
//    private int media_type;
//    private long ip;
//    private long burn_time;
//    private boolean matchKeyword;
//    private boolean topublic;
//    private boolean pushToMPS;
//    private long oriImageId;
//
//    private List<Long> att_ids;
//
//    private User sender;
//    private User recipient;
//    private Geo geo;
//    private int status = STATUS_SERVER;
//    private String imagePath;
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getIdstr() {
//        return idstr;
//    }
//
//    public void setIdstr(String idstr) {
//        this.idstr = idstr;
//    }
//
//    public String getCreated_at() {
//        return created_at;
//    }
//
//    public void setCreated_at(String created_at) {
//        this.created_at = created_at;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public int getSys_type() {
//        return sys_type;
//    }
//
//    public void setSys_type(int sys_type) {
//        this.sys_type = sys_type;
//    }
//
//    public int getMsg_status() {
//        return msg_status;
//    }
//
//    public void setMsg_status(int msg_status) {
//        this.msg_status = msg_status;
//    }
//
//    public long getSender_id() {
//        return sender_id;
//    }
//
//    public void setSender_id(long sender_id) {
//        this.sender_id = sender_id;
//    }
//
//    public long getRecipient_id() {
//        return recipient_id;
//    }
//
//    public void setRecipient_id(long recipient_id) {
//        this.recipient_id = recipient_id;
//    }
//
//    public String getSender_screen_name() {
//        return sender_screen_name;
//    }
//
//    public void setSender_screen_name(String sender_screen_name) {
//        this.sender_screen_name = sender_screen_name;
//    }
//
//    public String getRecipient_screen_name() {
//        return recipient_screen_name;
//    }
//
//    public void setRecipient_screen_name(String recipient_screen_name) {
//        this.recipient_screen_name = recipient_screen_name;
//    }
//
//    public User getSender() {
//        return sender;
//    }
//
//    public void setSender(User sender) {
//        this.sender = sender;
//    }
//
//    public User getRecipient() {
//        return recipient;
//    }
//
//    public void setRecipient(User recipient) {
//        this.recipient = recipient;
//    }
//
//    public String getMid() {
//        return mid;
//    }
//
//    public void setMid(String mid) {
//        this.mid = mid;
//    }
//
//    public boolean isIsLargeDm() {
//        return isLargeDm;
//    }
//
//    public void setIsLargeDm(boolean isLargeDm) {
//        this.isLargeDm = isLargeDm;
//    }
//
//    public String getSource() {
//        return source;
//    }
//
//    public void setSource(String source) {
//        this.source = source;
//    }
//
//    public long getStatus_id() {
//        return status_id;
//    }
//
//    public void setStatus_id(long status_id) {
//        this.status_id = status_id;
//    }
//
//    public Geo getGeo() {
//        return geo;
//    }
//
//    public void setGeo(Geo geo) {
//        this.geo = geo;
//    }
//
//    public int getDm_type() {
//        return dm_type;
//    }
//
//    public void setDm_type(int dm_type) {
//        this.dm_type = dm_type;
//    }
//
//    public int getMedia_type() {
//        return media_type;
//    }
//
//    public void setMedia_type(int media_type) {
//        this.media_type = media_type;
//    }
//
//    public long getIp() {
//        return ip;
//    }
//
//    public void setIp(long ip) {
//        this.ip = ip;
//    }
//
//    public long getBurn_time() {
//        return burn_time;
//    }
//
//    public void setBurn_time(long burn_time) {
//        this.burn_time = burn_time;
//    }
//
//    public boolean isMatchKeyword() {
//        return matchKeyword;
//    }
//
//    public void setMatchKeyword(boolean matchKeyword) {
//        this.matchKeyword = matchKeyword;
//    }
//
//    public boolean isTopublic() {
//        return topublic;
//    }
//
//    public void setTopublic(boolean topublic) {
//        this.topublic = topublic;
//    }
//
//    public boolean isPushToMPS() {
//        return pushToMPS;
//    }
//
//    public void setPushToMPS(boolean pushToMPS) {
//        this.pushToMPS = pushToMPS;
//    }
//
//    public long getOriImageId() {
//        return oriImageId;
//    }
//
//    public void setOriImageId(long oriImageId) {
//        this.oriImageId = oriImageId;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        DirectMessage directMessage = (DirectMessage) o;
//        return directMessage.id == id;
//    }
//
//    public List<Long> getAtt_ids() {
//        return att_ids;
//    }
//
//    public void setAtt_ids(List<Long> att_ids) {
//        this.att_ids = att_ids;
//    }
//
//    public int getStatus() {
//        return status;
//    }
//
//    public void setStatus(int status) {
//        this.status = status;
//    }
//
//    public String getImagePath() {
//        return imagePath;
//    }
//
//    public void setImagePath(String imagePath) {
//        this.imagePath = imagePath;
//    }
//
//}

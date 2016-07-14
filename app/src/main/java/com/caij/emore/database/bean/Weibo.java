package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.text.SpannableString;

import java.io.Serializable;
import java.util.List;

/**
 * Entity mapped to table "WEIBO".
 */
public class Weibo implements Serializable {

    private String created_at;
    private Long id;
    private Long mid;
    private String idstr;
    private String text;
    private String source;
    private Boolean favorited;
    private Boolean truncated;
    private String in_reply_to_status_id;
    private String in_reply_to_user_id;
    private String in_reply_to_screen_name;
    private String thumbnail_pic;
    private String bmiddle_pic;
    private String original_pic;
    private Integer reposts_count;
    private Integer comments_count;
    private Integer attitudes_count;
    private Integer mlevel;
    private Long update_time = System.currentTimeMillis();
    private String geo_id;
    private Long user_id;
    private String visible_id;
    private Long retweeted_status_id;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Weibo() {
    }

    public Weibo(Long id) {
        this.id = id;
    }

    public Weibo(String created_at, Long id, Long mid, String idstr, String text, String source, Boolean favorited, Boolean truncated, String in_reply_to_status_id, String in_reply_to_user_id, String in_reply_to_screen_name, String thumbnail_pic, String bmiddle_pic, String original_pic, Integer reposts_count, Integer comments_count, Integer attitudes_count, Integer mlevel, Long update_time, String geo_id, Long user_id, String visible_id, Long retweeted_status_id) {
        this.created_at = created_at;
        this.id = id;
        this.mid = mid;
        this.idstr = idstr;
        this.text = text;
        this.source = source;
        this.favorited = favorited;
        this.truncated = truncated;
        this.in_reply_to_status_id = in_reply_to_status_id;
        this.in_reply_to_user_id = in_reply_to_user_id;
        this.in_reply_to_screen_name = in_reply_to_screen_name;
        this.thumbnail_pic = thumbnail_pic;
        this.bmiddle_pic = bmiddle_pic;
        this.original_pic = original_pic;
        this.reposts_count = reposts_count;
        this.comments_count = comments_count;
        this.attitudes_count = attitudes_count;
        this.mlevel = mlevel;
        this.update_time = update_time;
        this.geo_id = geo_id;
        this.user_id = user_id;
        this.visible_id = visible_id;
        this.retweeted_status_id = retweeted_status_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public String getIn_reply_to_status_id() {
        return in_reply_to_status_id;
    }

    public void setIn_reply_to_status_id(String in_reply_to_status_id) {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    public String getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(String in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public Integer getReposts_count() {
        return reposts_count;
    }

    public void setReposts_count(Integer reposts_count) {
        this.reposts_count = reposts_count;
    }

    public Integer getComments_count() {
        return comments_count;
    }

    public void setComments_count(Integer comments_count) {
        this.comments_count = comments_count;
    }

    public Integer getAttitudes_count() {
        return attitudes_count;
    }

    public void setAttitudes_count(Integer attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    public Integer getMlevel() {
        return mlevel;
    }

    public void setMlevel(Integer mlevel) {
        this.mlevel = mlevel;
    }

    public Long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Long update_time) {
        this.update_time = update_time;
    }

    public String getGeo_id() {
        return geo_id;
    }

    public void setGeo_id(String geo_id) {
        this.geo_id = geo_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getVisible_id() {
        return visible_id;
    }

    public void setVisible_id(String visible_id) {
        this.visible_id = visible_id;
    }

    public Long getRetweeted_status_id() {
        return retweeted_status_id;
    }

    public void setRetweeted_status_id(Long retweeted_status_id) {
        this.retweeted_status_id = retweeted_status_id;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

    private Visible visible;
    private List<PicUrl> pic_urls;
    private boolean isAttitudes;
    private Geo geo;
    private User user;
    private Weibo retweeted_status;
    private transient SpannableString contentSpannableString;

    public Visible getVisible() {
        return visible;
    }

    public void setVisible(Visible visible) {
        this.visible = visible;
    }

    public List<PicUrl> getPic_urls() {
        return pic_urls;
    }

    public void setPic_urls(List<PicUrl> pic_urls) {
        this.pic_urls = pic_urls;
    }

    public boolean isAttitudes() {
        return isAttitudes;
    }

    public void setAttitudes(boolean attitudes) {
        isAttitudes = attitudes;
    }

    public Geo getGeo() {
        return geo;
    }

    public void setGeo(Geo geo) {
        this.geo = geo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Weibo getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(Weibo retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public SpannableString getContentSpannableString() {
        return contentSpannableString;
    }

    public void setContentSpannableString(SpannableString contentSpannableString) {
        this.contentSpannableString = contentSpannableString;
    }

    @Override
    public boolean equals(Object o) {
        Weibo  weibo = (Weibo) o;
        return weibo.id.equals(id);
    }

}
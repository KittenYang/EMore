package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import java.io.Serializable;

/**
 * Entity mapped to table "USER".
 */
public class User implements Serializable {

    private Long id;
    private String idstr;
    private String screen_name;
    private String name;
    private Integer province;
    private Integer city;
    private String location;
    private String description;
    private String url;
    private String profile_image_url;
    private String profile_url;
    private String domain;
    private String weihao;
    private String gender;
    private Integer followers_count;
    private Integer friends_count;
    private Integer statuses_count;
    private Integer favourites_count;
    private String created_at;
    private Boolean following;
    private Boolean allow_all_act_msg;
    private Boolean geo_enabled;
    private Boolean verified;
    private Integer verified_type;
    private String remark;
    private Boolean allow_all_comment;
    private String avatar_large;
    private String avatar_hd;
    private String verified_reason;
    private Boolean follow_me;
    private Integer online_status;
    private Integer bi_followers_count;
    private String lang;
    private Long fiset_weibo_id;
    private String cover_image_phone;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, String idstr, String screen_name, String name, Integer province, Integer city, String location, String description, String url, String profile_image_url, String profile_url, String domain, String weihao, String gender, Integer followers_count, Integer friends_count, Integer statuses_count, Integer favourites_count, String created_at, Boolean following, Boolean allow_all_act_msg, Boolean geo_enabled, Boolean verified, Integer verified_type, String remark, Boolean allow_all_comment, String avatar_large, String avatar_hd, String verified_reason, Boolean follow_me, Integer online_status, Integer bi_followers_count, String lang, Long fiset_weibo_id, String cover_image_phone) {
        this.id = id;
        this.idstr = idstr;
        this.screen_name = screen_name;
        this.name = name;
        this.province = province;
        this.city = city;
        this.location = location;
        this.description = description;
        this.url = url;
        this.profile_image_url = profile_image_url;
        this.profile_url = profile_url;
        this.domain = domain;
        this.weihao = weihao;
        this.gender = gender;
        this.followers_count = followers_count;
        this.friends_count = friends_count;
        this.statuses_count = statuses_count;
        this.favourites_count = favourites_count;
        this.created_at = created_at;
        this.following = following;
        this.allow_all_act_msg = allow_all_act_msg;
        this.geo_enabled = geo_enabled;
        this.verified = verified;
        this.verified_type = verified_type;
        this.remark = remark;
        this.allow_all_comment = allow_all_comment;
        this.avatar_large = avatar_large;
        this.avatar_hd = avatar_hd;
        this.verified_reason = verified_reason;
        this.follow_me = follow_me;
        this.online_status = online_status;
        this.bi_followers_count = bi_followers_count;
        this.lang = lang;
        this.fiset_weibo_id = fiset_weibo_id;
        this.cover_image_phone = cover_image_phone;
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

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWeihao() {
        return weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(Integer followers_count) {
        this.followers_count = followers_count;
    }

    public Integer getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(Integer friends_count) {
        this.friends_count = friends_count;
    }

    public Integer getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(Integer statuses_count) {
        this.statuses_count = statuses_count;
    }

    public Integer getFavourites_count() {
        return favourites_count;
    }

    public void setFavourites_count(Integer favourites_count) {
        this.favourites_count = favourites_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Boolean getAllow_all_act_msg() {
        return allow_all_act_msg;
    }

    public void setAllow_all_act_msg(Boolean allow_all_act_msg) {
        this.allow_all_act_msg = allow_all_act_msg;
    }

    public Boolean getGeo_enabled() {
        return geo_enabled;
    }

    public void setGeo_enabled(Boolean geo_enabled) {
        this.geo_enabled = geo_enabled;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Integer getVerified_type() {
        return verified_type;
    }

    public void setVerified_type(Integer verified_type) {
        this.verified_type = verified_type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getAllow_all_comment() {
        return allow_all_comment;
    }

    public void setAllow_all_comment(Boolean allow_all_comment) {
        this.allow_all_comment = allow_all_comment;
    }

    public String getAvatar_large() {
        return avatar_large;
    }

    public void setAvatar_large(String avatar_large) {
        this.avatar_large = avatar_large;
    }

    public String getAvatar_hd() {
        return avatar_hd;
    }

    public void setAvatar_hd(String avatar_hd) {
        this.avatar_hd = avatar_hd;
    }

    public String getVerified_reason() {
        return verified_reason;
    }

    public void setVerified_reason(String verified_reason) {
        this.verified_reason = verified_reason;
    }

    public Boolean getFollow_me() {
        return follow_me;
    }

    public void setFollow_me(Boolean follow_me) {
        this.follow_me = follow_me;
    }

    public Integer getOnline_status() {
        return online_status;
    }

    public void setOnline_status(Integer online_status) {
        this.online_status = online_status;
    }

    public Integer getBi_followers_count() {
        return bi_followers_count;
    }

    public void setBi_followers_count(Integer bi_followers_count) {
        this.bi_followers_count = bi_followers_count;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Long getFiset_weibo_id() {
        return fiset_weibo_id;
    }

    public void setFiset_weibo_id(Long fiset_weibo_id) {
        this.fiset_weibo_id = fiset_weibo_id;
    }

    public String getCover_image_phone() {
        return cover_image_phone;
    }

    public void setCover_image_phone(String cover_image_phone) {
        this.cover_image_phone = cover_image_phone;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

    private Weibo status;

    public Weibo getStatus() {
        return status;
    }

    public void setStatus(Weibo status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        User user = (User) o;
        return user.id.equals(id);
    }
}

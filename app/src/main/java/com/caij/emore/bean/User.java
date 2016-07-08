package com.caij.emore.bean;

import com.caij.emore.database.bean.LocalUser;
import com.caij.emore.utils.GsonUtils;

import java.io.Serializable;

/**
 * Created by Caij on 2016/5/31.
 */
public class User implements Serializable{

    public static User localUser2User(LocalUser localUser) {
        if (localUser == null) return null;
        User user = new User();
        user.setUrl(localUser.getUrl());
        user.setAllow_all_act_msg(localUser.getAllow_all_act_msg());
        user.setAllow_all_comment(localUser.getAllow_all_comment());
        user.setAvatar_hd(localUser.getAvatar_hd());
        user.setAvatar_large(localUser.getAvatar_large());
        user.setBi_followers_count(localUser.getBi_followers_count());
        user.setCity(localUser.getCity());
        user.setCreated_at(localUser.getCreated_at());
        user.setDescription(localUser.getDescription());
        user.setDomain(localUser.getDomain());
        user.setFavourites_count(localUser.getFavourites_count());
        user.setFollow_me(localUser.getFollow_me());
        user.setFollowing(localUser.getFollowing());
        user.setFollowers_count(localUser.getFollowers_count());
        user.setGender(localUser.getGender());
        user.setGeo_enabled(localUser.getGeo_enabled());
        user.setLocation(localUser.getLocation());
        user.setOnline_status(localUser.getOnline_status());
        user.setId(localUser.getId());
        user.setName(localUser.getName());
        user.setStatuses_count(localUser.getStatuses_count());
        user.setStatus(GsonUtils.fromJson(localUser.getWeibo_json(), Weibo.class));
        user.setScreen_name(localUser.getScreen_name());
        user.setRemark(localUser.getRemark());
        user.setProvince(localUser.getProvince());
        user.setVerified_type(localUser.getVerified_type());
        user.setIdstr(localUser.getIdstr());
        user.setProfile_image_url(localUser.getProfile_image_url());
        user.setProfile_url(localUser.getProfile_url());
        user.setWeihao(localUser.getWeihao());
        user.setFriends_count(localUser.getFriends_count());
        user.setVerified_reason(localUser.getVerified_reason());
        user.setLang(localUser.getLang());
        return user;
    }

    public static LocalUser user2LocalUser(User user) {
        if (user == null) return  null;
        LocalUser localUser = new LocalUser();
        localUser.setUrl(user.getUrl());
        localUser.setAllow_all_act_msg(user.isAllow_all_act_msg());
        localUser.setAllow_all_comment(user.isAllow_all_comment());
        localUser.setAvatar_hd(user.getAvatar_hd());
        localUser.setAvatar_large(user.getAvatar_large());
        localUser.setBi_followers_count(user.getBi_followers_count());
        localUser.setCity(user.getCity());
        localUser.setCreated_at(user.getCreated_at());
        localUser.setDescription(user.getDescription());
        localUser.setDomain(user.getDomain());
        localUser.setFavourites_count(user.getFavourites_count());
        localUser.setFollow_me(user.isFollow_me());
        localUser.setFollowing(user.isFollowing());
        localUser.setFollowers_count(user.getFollowers_count());
        localUser.setGender(user.getGender());
        localUser.setGeo_enabled(user.isGeo_enabled());
        localUser.setLocation(user.getLocation());
        localUser.setOnline_status(user.getOnline_status());
        localUser.setId(user.getId());
        localUser.setName(user.getName());
        localUser.setStatuses_count(user.getStatuses_count());
        localUser.setWeibo_json(GsonUtils.toJson(user.getStatus()));
        localUser.setScreen_name(user.getScreen_name());
        localUser.setRemark(user.getRemark());
        localUser.setProvince(user.getProvince());
        localUser.setVerified_type(user.getVerified_type());
        localUser.setIdstr(user.getIdstr());
        localUser.setProfile_image_url(user.getProfile_image_url());
        localUser.setProfile_url(user.getProfile_url());
        localUser.setWeihao(user.getWeihao());
        localUser.setFriends_count(user.getFriends_count());
        localUser.setVerified_reason(user.getVerified_reason());
        localUser.setLang(user.getLang());
        return localUser;
    }


    private long id;
    private String idstr;
    private String screen_name;
    private String name;
    private int province;
    private int city;
    private String location;
    private String description;
    private String url;
    private String profile_image_url;
    private String profile_url;
    private String domain;
    private String weihao;
    private String gender;
    private int followers_count;
    private int friends_count;
    private int statuses_count;
    private int favourites_count;
    private String created_at;
    private boolean following;
    private boolean allow_all_act_msg;
    private boolean geo_enabled;
    private boolean verified;
    private int verified_type;
    private String remark;
    private Weibo status;
    private boolean allow_all_comment;
    private String avatar_large;
    private String avatar_hd;
    private String verified_reason;
    private boolean follow_me;
    private int online_status;
    private int bi_followers_count;
    private String lang;
    private String cover_image_phone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
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

    public int getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(int followers_count) {
        this.followers_count = followers_count;
    }

    public int getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(int friends_count) {
        this.friends_count = friends_count;
    }

    public int getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(int statuses_count) {
        this.statuses_count = statuses_count;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public void setFavourites_count(int favourites_count) {
        this.favourites_count = favourites_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isAllow_all_act_msg() {
        return allow_all_act_msg;
    }

    public void setAllow_all_act_msg(boolean allow_all_act_msg) {
        this.allow_all_act_msg = allow_all_act_msg;
    }

    public boolean isGeo_enabled() {
        return geo_enabled;
    }

    public void setGeo_enabled(boolean geo_enabled) {
        this.geo_enabled = geo_enabled;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getVerified_type() {
        return verified_type;
    }

    public void setVerified_type(int verified_type) {
        this.verified_type = verified_type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Weibo getStatus() {
        return status;
    }

    public void setStatus(Weibo status) {
        this.status = status;
    }

    public boolean isAllow_all_comment() {
        return allow_all_comment;
    }

    public void setAllow_all_comment(boolean allow_all_comment) {
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

    public boolean isFollow_me() {
        return follow_me;
    }

    public void setFollow_me(boolean follow_me) {
        this.follow_me = follow_me;
    }

    public int getOnline_status() {
        return online_status;
    }

    public void setOnline_status(int online_status) {
        this.online_status = online_status;
    }

    public int getBi_followers_count() {
        return bi_followers_count;
    }

    public void setBi_followers_count(int bi_followers_count) {
        this.bi_followers_count = bi_followers_count;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCover_image_phone() {
        return cover_image_phone;
    }

    public void setCover_image_phone(String cover_image_phone) {
        this.cover_image_phone = cover_image_phone;
    }
}

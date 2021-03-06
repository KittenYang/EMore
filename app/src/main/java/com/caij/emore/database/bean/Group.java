package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "GROUP".
 */
public class Group {

    private java.util.Date created_at;
    private String description;
    private Long id;
    private String idstr;
    private Integer like_count;
    private Integer member_count;
    private String mode;
    private String name;
    private String profile_image_url;
    private Integer sysgroup;
    private Integer visible;
    private Long uid;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Group() {
    }

    public Group(Long id) {
        this.id = id;
    }

    public Group(java.util.Date created_at, String description, Long id, String idstr, Integer like_count, Integer member_count, String mode, String name, String profile_image_url, Integer sysgroup, Integer visible, Long uid) {
        this.created_at = created_at;
        this.description = description;
        this.id = id;
        this.idstr = idstr;
        this.like_count = like_count;
        this.member_count = member_count;
        this.mode = mode;
        this.name = name;
        this.profile_image_url = profile_image_url;
        this.sysgroup = sysgroup;
        this.visible = visible;
        this.uid = uid;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getLike_count() {
        return like_count;
    }

    public void setLike_count(Integer like_count) {
        this.like_count = like_count;
    }

    public Integer getMember_count() {
        return member_count;
    }

    public void setMember_count(Integer member_count) {
        this.member_count = member_count;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public Integer getSysgroup() {
        return sysgroup;
    }

    public void setSysgroup(Integer sysgroup) {
        this.sysgroup = sysgroup;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}

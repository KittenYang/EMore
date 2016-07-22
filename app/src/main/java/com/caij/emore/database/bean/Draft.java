package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import java.io.Serializable;
import java.util.List;

/**
 * Entity mapped to table "DRAFT".
 */
public class Draft implements Serializable {

    private Long id;
    private Long create_at;
    private Integer status;
    private Integer type;
    private String content;
    private String image_paths;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Draft() {
    }

    public Draft(Long id) {
        this.id = id;
    }

    public Draft(Long id, Long create_at, Integer status, Integer type, String content, String image_paths) {
        this.id = id;
        this.create_at = create_at;
        this.status = status;
        this.type = type;
        this.content = content;
        this.image_paths = image_paths;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Long create_at) {
        this.create_at = create_at;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage_paths() {
        return image_paths;
    }

    public void setImage_paths(String image_paths) {
        this.image_paths = image_paths;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

    public static final int STATUS_FAIL = 3;
    public static final int STATUS_SAVE = 2;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_SENDING = 4;

    public static final int TYPE_WEIBO = 1;

    private List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        Draft draft = (Draft) o;
        return draft.getId().longValue() == id.longValue();
    }
}
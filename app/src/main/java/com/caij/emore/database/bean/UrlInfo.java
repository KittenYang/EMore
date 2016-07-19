package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Entity mapped to table "URL_INFO".
 */
public class UrlInfo {

    private String shortUrl;
    private String longUrl;
    private String description;
    private String object_type;
    private String title;
    private String display_name;
    private Integer transcode;
    private Integer type;
    private Boolean shortUrlAvailable;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public UrlInfo() {
    }

    public UrlInfo(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public UrlInfo(String shortUrl, String longUrl, String description, String object_type, String title, String display_name, Integer transcode, Integer type, Boolean shortUrlAvailable) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.description = description;
        this.object_type = object_type;
        this.title = title;
        this.display_name = display_name;
        this.transcode = transcode;
        this.type = type;
        this.shortUrlAvailable = shortUrlAvailable;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Integer getTranscode() {
        return transcode;
    }

    public void setTranscode(Integer transcode) {
        this.transcode = transcode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getShortUrlAvailable() {
        return shortUrlAvailable;
    }

    public void setShortUrlAvailable(Boolean shortUrlAvailable) {
        this.shortUrlAvailable = shortUrlAvailable;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END


    public static final int TYPE_WEB = 0;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_MUSIC = 2;
    public static final int TYPE_IMAGE = Integer.MAX_VALUE;
    public static final int TYPE_ARTICLE = Integer.MAX_VALUE - 1;
    public static final int TYPE_WEB_PAGE = Integer.MAX_VALUE - 2;
    public static final int TYPE_FULL_TEXT = Integer.MAX_VALUE - 3;

    public UrlInfo(JSONObject paramJSONObject) {
        if (paramJSONObject != null) {
            this.title = paramJSONObject.optString("title");
            this.description = paramJSONObject.optString("description");
            this.shortUrl = paramJSONObject.optString("url_short");
            this.longUrl = paramJSONObject.optString("url_long");
            this.type = paramJSONObject.optInt("type", -1);
            this.shortUrlAvailable = paramJSONObject.optBoolean("result");
            this.transcode = paramJSONObject.optInt("transcode");
            JSONArray localJSONArray = paramJSONObject.optJSONArray("annotations");
            if ((localJSONArray != null) && (localJSONArray.length() > 0)) {
                JSONObject localJSONObject = localJSONArray.optJSONObject(0);
                if (localJSONObject != null) {
                    this.object_type = localJSONObject.optString("object_type");
                    JSONObject jsonObject = localJSONObject.optJSONObject("object");
                    if (jsonObject != null) {
                        this.display_name = jsonObject.optString("display_name");
                    }
                }
            }
        }
    }

    public int getUrlType() {
        if ("video".equalsIgnoreCase(this.object_type)) {
            return TYPE_VIDEO;
        }else if ("collection".equalsIgnoreCase(object_type)) {
            return TYPE_IMAGE;
        }else if ("article".equalsIgnoreCase(object_type)) {
            return TYPE_WEB_PAGE;
        }else if ("webpage".equalsIgnoreCase(object_type)) {
            return TYPE_WEB_PAGE;
        }
        return type;
    }

    public static boolean isShortUrl(String url) {
        return url.contains("t.cn");
    }

}

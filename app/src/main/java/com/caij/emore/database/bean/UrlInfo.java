package com.caij.emore.database.bean;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table "URL_INFO".
 */
public class UrlInfo {

    private String shortUrl;
    private String url_info_json;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public UrlInfo() {
    }

    public UrlInfo(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public UrlInfo(String shortUrl, String url_info_json) {
        this.shortUrl = shortUrl;
        this.url_info_json = url_info_json;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getUrl_info_json() {
        return url_info_json;
    }

    public void setUrl_info_json(String url_info_json) {
        this.url_info_json = url_info_json;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END



}

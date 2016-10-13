package com.caij.emore.bean;

import java.io.Serializable;

/**
 * Created by Caij on 2016/10/13.
 */

public class MessageAttachInfo implements Serializable {


    /**
     * fid : 4030058266473334
     * uid : 2813584522
     * name : 1476324227000.jpg
     * ctime : 1476324228
     * dir_id : 3
     * size : 63526
     * type : image/jpeg
     * url : null
     * thumbnail : http://upload.api.weibo.com/2/mss/msget_thumbnail?fid=4030058266473334&high=60&width=60&size=34,60
     * thumbnail_240 : http://upload.api.weibo.com/2/mss/msget_thumbnail?fid=4030058266473334&high=240&width=240&size=136,240
     * s3_url : http://upload.api.weibo.com/2/mss/msget?fid=4030058266473334
     */

    private String fid;
    private String uid;
    private String name;
    private String ctime;
    private String dir_id;
    private String size;
    private String type;
    private String url;
    private String thumbnail;
    private String thumbnail_240;
    private String s3_url;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getDir_id() {
        return dir_id;
    }

    public void setDir_id(String dir_id) {
        this.dir_id = dir_id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail_240() {
        return thumbnail_240;
    }

    public void setThumbnail_240(String thumbnail_240) {
        this.thumbnail_240 = thumbnail_240;
    }

    public String getS3_url() {
        return s3_url;
    }

    public void setS3_url(String s3_url) {
        this.s3_url = s3_url;
    }
}

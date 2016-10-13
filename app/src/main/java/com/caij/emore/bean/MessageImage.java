package com.caij.emore.bean;

/**
 * Created by Caij on 2016/10/13.
 */

public class MessageImage {


    /**
     * fid : 4030127245476890
     * vfid : 4030127245476890
     * tovfid : 4030127245476890
     * filename : 29e86ba59a2fdf4f4ff207b3bd146eb3.compress
     * thumbnail_60 : http://upload.api.weibo.com/2/mss/msget_thumbnail?fid=4030127245476890&high=60&width=60&size=60,60
     * thumbnail_100 : http://upload.api.weibo.com/2/mss/msget_thumbnail?fid=4030127245476890&high=100&width=100&size=100,100
     * thumbnail_120 : http://upload.api.weibo.com/2/mss/msget_thumbnail?fid=4030127245476890&high=120&width=120&size=120,120
     * thumbnail_240 : http://upload.api.weibo.com/2/mss/msget_thumbnail?fid=4030127245476890&high=240&width=240&size=240,240
     * thumbnail_600 : http://upload.api.weibo.com/2/mss/msget_thumbnail?fid=4030127245476890&high=600&width=600&size=600,600
     * http_code : 200
     */

    private long fid;
    private long vfid;
    private long tovfid;
    private String filename;
    private String thumbnail_60;
    private String thumbnail_100;
    private String thumbnail_120;
    private String thumbnail_240;
    private String thumbnail_600;
    private int http_code;

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getVfid() {
        return vfid;
    }

    public void setVfid(long vfid) {
        this.vfid = vfid;
    }

    public long getTovfid() {
        return tovfid;
    }

    public void setTovfid(long tovfid) {
        this.tovfid = tovfid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getThumbnail_60() {
        return thumbnail_60;
    }

    public void setThumbnail_60(String thumbnail_60) {
        this.thumbnail_60 = thumbnail_60;
    }

    public String getThumbnail_100() {
        return thumbnail_100;
    }

    public void setThumbnail_100(String thumbnail_100) {
        this.thumbnail_100 = thumbnail_100;
    }

    public String getThumbnail_120() {
        return thumbnail_120;
    }

    public void setThumbnail_120(String thumbnail_120) {
        this.thumbnail_120 = thumbnail_120;
    }

    public String getThumbnail_240() {
        return thumbnail_240;
    }

    public void setThumbnail_240(String thumbnail_240) {
        this.thumbnail_240 = thumbnail_240;
    }

    public String getThumbnail_600() {
        return thumbnail_600;
    }

    public void setThumbnail_600(String thumbnail_600) {
        this.thumbnail_600 = thumbnail_600;
    }

    public int getHttp_code() {
        return http_code;
    }

    public void setHttp_code(int http_code) {
        this.http_code = http_code;
    }
}

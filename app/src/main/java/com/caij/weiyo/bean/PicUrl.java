package com.caij.weiyo.bean;

/**
 * Created by Caij on 2016/6/6.
 */
public class PicUrl {

    private String thumbnail_pic;

    private int width = -1;

    private int height = -1;

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

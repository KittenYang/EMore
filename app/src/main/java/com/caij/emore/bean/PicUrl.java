package com.caij.emore.bean;

import java.io.Serializable;

/**
 * Created by Caij on 2016/6/6.
 */
public class PicUrl implements Serializable{

    private String thumbnail_pic;

    private int width = -1;

    private int height = -1;

    private String original_pic;

    private String bmiddle_pic;

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

    public boolean hasSize() {
        return width > 0 && height > 0;
    }

    public String getOriginal_pic() {
        if (original_pic == null) {
            original_pic = new String(thumbnail_pic).replace("thumbnail", "large");
        }
        return original_pic;
    }

    public String getBmiddle_pic() {
        if (bmiddle_pic == null) {
            bmiddle_pic = new String(thumbnail_pic).replace("thumbnail", "bmiddle");
        }

        return bmiddle_pic;
    }

    public boolean isBigImage() {
        return this.getWidth() / this.getHeight() > 3 || this.getHeight() / this.getWidth() > 3;
    }

    public boolean isBigImageAndHeightBtWidth() {
        return this.getHeight() / this.getWidth() > 3;
    }
}

package com.caij.emore.bean.weibo;

import java.io.Serializable;

/**
 * Created by Caij on 2016/9/29.
 */

public class Title implements Serializable {

    private String icon_url;
    private String text;
    private int base_color;

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBase_color() {
        return base_color;
    }

    public void setBase_color(int base_color) {
        this.base_color = base_color;
    }
}

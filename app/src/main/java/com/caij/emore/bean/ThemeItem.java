package com.caij.emore.bean;

/**
 * Created by Caij on 2016/9/18.
 */
public class ThemeItem {

    private boolean isSelect;
    private int color;

    public ThemeItem(int color, boolean isSelect) {
        this.isSelect = isSelect;
        this.color = color;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

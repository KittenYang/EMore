package com.caij.weiyo.bean;

import io.realm.RealmObject;

/**
 * Created by Caij on 2016/5/31.
 */
public class RealmString extends RealmObject{

    private String val;

    public RealmString(){

    }

    public RealmString(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}

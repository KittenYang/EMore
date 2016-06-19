package com.caij.weiyo.bean;

import java.io.Serializable;

/**
 * Created by Caij on 2016/6/18.
 */
public class Emotion implements Serializable{

    public String key;
    public int drawableId;

    public Emotion(String key, int drawableId){
        this.key = key;
        this.drawableId = drawableId;
    }
}

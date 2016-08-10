package com.caij.emore.service;

import java.io.Serializable;

/**
 * Created by Caij on 2016/8/9.
 */
public class PipeEvent implements Serializable {

    public Object tag;
    public Object content;

    public PipeEvent(Object tag, Object content) {
        this.tag = tag;
        this.content = content;
    }

}

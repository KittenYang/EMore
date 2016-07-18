package com.caij.emore.bean.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Caij on 2016/7/18.
 */
public class QueryUrlResponse implements Serializable {

    private List<Object> urls;

    public List<Object> getUrls() {
        return urls;
    }

    public void setUrls(List<Object> urls) {
        this.urls = urls;
    }
}

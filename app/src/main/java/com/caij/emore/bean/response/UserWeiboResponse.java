package com.caij.emore.bean.response;

import com.caij.emore.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserWeiboResponse extends Response {

    private List<Weibo> statuses;

    public List<Weibo> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Weibo> statuses) {
        this.statuses = statuses;
    }
}

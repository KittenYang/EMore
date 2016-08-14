package com.caij.emore.bean.response;


import com.caij.emore.database.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryRepostWeiboResponse extends Response {

    private List<Weibo> reposts;
    private int total_number;

    public List<Weibo> getReposts() {
        return reposts;
    }

    public void setReposts(List<Weibo> reposts) {
        this.reposts = reposts;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }
}

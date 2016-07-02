package com.caij.weiyo.bean.response;

import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.Weibo;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryRepostWeiboResponse extends Response {

    private List<Weibo> reposts;

    public List<Weibo> getReposts() {
        return reposts;
    }

    public void setReposts(List<Weibo> reposts) {
        this.reposts = reposts;
    }
}

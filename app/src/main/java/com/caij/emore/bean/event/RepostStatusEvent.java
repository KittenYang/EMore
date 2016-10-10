package com.caij.emore.bean.event;

import com.caij.emore.database.bean.Weibo;

/**
 * Created by Caij on 2016/10/10.
 */

public class RepostStatusEvent extends Event {

    public Weibo weibo;
    public long weiboId;

    public RepostStatusEvent(String type, Weibo weibo, long weiboId) {
        super(type);
        this.weibo = weibo;
        this.weiboId = weiboId;
    }
}

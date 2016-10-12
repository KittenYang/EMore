package com.caij.emore.bean.event;

import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/10/10.
 */

public class RelayStatusEvent extends Event {

    public Status weibo;
    public long weiboId;

    public RelayStatusEvent(String type, Status weibo, long weiboId) {
        super(type);
        this.weibo = weibo;
        this.weiboId = weiboId;
    }
}

package com.caij.emore.bean.event;

/**
 * Created by Caij on 2016/10/10.
 */

public class StatusActionCountUpdateEvent extends Event {

    public long statusId;
    public int count;

    public StatusActionCountUpdateEvent(String type, long statusId, int count) {
        this.type = type;
        this.statusId = statusId;
        this.count = count;
    }
}

package com.caij.emore.bean.event;

/**
 * Created by Caij on 2016/10/10.
 */

public class StatusRefreshEvent extends Event {

    public long statusId;

    public StatusRefreshEvent(String type, long statusId) {
        super(type);
        this.statusId = statusId;
    }
}

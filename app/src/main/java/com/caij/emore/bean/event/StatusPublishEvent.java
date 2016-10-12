package com.caij.emore.bean.event;

import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/10/11.
 */

public class StatusPublishEvent extends Event {

    public Status status;

    public StatusPublishEvent(String type, Status status) {
        super(type);
        this.status = status;
    }
}

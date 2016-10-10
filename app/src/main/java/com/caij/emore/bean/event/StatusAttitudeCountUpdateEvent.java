package com.caij.emore.bean.event;

import java.io.Serializable;

/**
 * Created by Caij on 2016/10/10.
 */

public class StatusAttitudeCountUpdateEvent extends Event {

    public long statusId;
    public int count;

    public StatusAttitudeCountUpdateEvent(String type, long statusId, int count) {
        this.type = type;
        this.statusId = statusId;
        this.count = count;
    }
}

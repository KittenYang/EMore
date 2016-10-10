package com.caij.emore.bean.event;

import com.caij.emore.database.bean.User;


/**
 * Created by Caij on 2016/10/10.
 */

public class StatusAttitudeEvent extends Event {

    public long statusId;
    public boolean isAttitude;
    public User user;

    public StatusAttitudeEvent(String type, long statusId, boolean isAttitude, User user) {
        this.type = type;
        this.statusId = statusId;
        this.isAttitude = isAttitude;
        this.user = user;
    }
}

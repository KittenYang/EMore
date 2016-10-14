package com.caij.emore.bean.response;


import com.caij.emore.database.bean.Status;

import java.util.List;

/**
 * Created by Caij on 2016/6/29.
 */
public class UserStatusesResponse extends Response {

    private List<Status> statuses;

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }
}

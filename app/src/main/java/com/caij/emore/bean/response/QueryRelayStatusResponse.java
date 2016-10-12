package com.caij.emore.bean.response;


import com.caij.emore.database.bean.Status;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryRelayStatusResponse extends Response {

    private List<Status> reposts;
    private int total_number;

    private long next_cursor;

    public List<Status> getReposts() {
        return reposts;
    }

    public void setReposts(List<Status> reposts) {
        this.reposts = reposts;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public long getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(long next_cursor) {
        this.next_cursor = next_cursor;
    }
}

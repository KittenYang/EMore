package com.caij.emore.bean.response;

import com.caij.emore.database.bean.Status;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryStatusResponse extends Response {

    private long since_id;

    private long max_id;

    private long next_cursor;

    private long has_unread;

    private long interval;

    private List<Status> statuses;

    private long total_number;

    private List<String> advertises;

    public long getSince_id() {
        return since_id;
    }

    public void setSince_id(long since_id) {
        this.since_id = since_id;
    }

    public long getMax_id() {
        return max_id;
    }

    public void setMax_id(long max_id) {
        this.max_id = max_id;
    }

    public long getHas_unread() {
        return has_unread;
    }

    public void setHas_unread(long has_unread) {
        this.has_unread = has_unread;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public long getTotal_number() {
        return total_number;
    }

    public void setTotal_number(long total_number) {
        this.total_number = total_number;
    }

    public long getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(long next_cursor) {
        this.next_cursor = next_cursor;
    }

    public List<String> getAdvertises() {
        return advertises;
    }

    public void setAdvertises(List<String> advertises) {
        this.advertises = advertises;
    }
}

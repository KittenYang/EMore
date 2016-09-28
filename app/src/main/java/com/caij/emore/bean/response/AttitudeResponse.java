package com.caij.emore.bean.response;

import com.caij.emore.bean.Attitude;

import java.util.List;

/**
 * Created by Caij on 2016/7/21.
 */
public class AttitudeResponse extends Response{

    private boolean hasvisible;
    private long previous_cursor;
    private long next_cursor;
    private int total_number;
    private List<Attitude> attitudes;

    public boolean isHasvisible() {
        return hasvisible;
    }

    public void setHasvisible(boolean hasvisible) {
        this.hasvisible = hasvisible;
    }

    public long getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(long previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public long getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(long next_cursor) {
        this.next_cursor = next_cursor;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public List<Attitude> getAttitudes() {
        return attitudes;
    }

    public void setAttitudes(List<Attitude> attitudes) {
        this.attitudes = attitudes;
    }
}

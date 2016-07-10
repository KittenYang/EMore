package com.caij.emore.bean.response;

import com.caij.emore.bean.DirectMessage;

import java.util.List;

/**
 * Created by Caij on 2016/7/10.
 */
public class UserMessageResponse {

    /**
     * direct_messages : []
     * next_cursor : 0
     * previous_cursor : 0
     * private_time : 0
     * total_number : 5
     * last_read_mid : 3816802088035198
     */

    private int next_cursor;
    private int previous_cursor;
    private int private_time;
    private int total_number;
    private long last_read_mid;
    private List<DirectMessage> direct_messages;

    public int getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(int next_cursor) {
        this.next_cursor = next_cursor;
    }

    public int getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(int previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public int getPrivate_time() {
        return private_time;
    }

    public void setPrivate_time(int private_time) {
        this.private_time = private_time;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public long getLast_read_mid() {
        return last_read_mid;
    }

    public void setLast_read_mid(long last_read_mid) {
        this.last_read_mid = last_read_mid;
    }

    public List<DirectMessage> getDirect_messages() {
        return direct_messages;
    }

    public void setDirect_messages(List<DirectMessage> direct_messages) {
        this.direct_messages = direct_messages;
    }
}

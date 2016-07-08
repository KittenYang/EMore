package com.caij.emore.bean.response;

import com.caij.emore.bean.User;

import java.util.List;

/**
 * Created by Caij on 2016/7/3.
 */
public class FriendshipResponse {
    private List<User> users;

    private long next_cursor;

    private long previous_cursor;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public long getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(long next_cursor) {
        this.next_cursor = next_cursor;
    }

    public long getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(long previous_cursor) {
        this.previous_cursor = previous_cursor;
    }
}

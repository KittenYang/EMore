package com.caij.emore.bean;

import java.util.List;

/**
 * Created by Caij on 2016/7/10.
 */
public class MessageUser {


    private long next_cursor;
    private long previous_cursor;
    private int totalNumber;

    private List<UserListBean> user_list;

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

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public List<UserListBean> getUser_list() {
        return user_list;
    }

    public void setUser_list(List<UserListBean> user_list) {
        this.user_list = user_list;
    }

    public static class UserListBean {

        private User user;
        private DirectMessage direct_message;
        private int unread_count;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public DirectMessage getDirect_message() {
            return direct_message;
        }

        public void setDirect_message(DirectMessage direct_message) {
            this.direct_message = direct_message;
        }

        public int getUnread_count() {
            return unread_count;
        }

        public void setUnread_count(int unread_count) {
            this.unread_count = unread_count;
        }
    }
}

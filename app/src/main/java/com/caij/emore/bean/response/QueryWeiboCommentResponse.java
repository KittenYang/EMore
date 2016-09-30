package com.caij.emore.bean.response;

import com.caij.emore.bean.Comment;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryWeiboCommentResponse extends Response {

    private List<Comment> comments;
    private List<Comment> hot_comments;
    private int total_number;

    private long next_cursor;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Integer getTotal_number() {
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

    public List<Comment> getHot_comments() {
        return hot_comments;
    }

    public void setHot_comments(List<Comment> hot_comments) {
        this.hot_comments = hot_comments;
    }
}

package com.caij.emore.bean.response;

import com.caij.emore.bean.Comment;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryWeiboCommentResponse extends Response {

    private List<Comment> comments;
    private int total_number;

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
}

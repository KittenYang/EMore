package com.caij.weiyo.bean.response;

import com.caij.weiyo.bean.Comment;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryWeiboCommentResponse {

    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

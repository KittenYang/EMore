package com.caij.emore.bean.response;

import com.caij.emore.bean.Comment;

import java.util.List;

/**
 * Created by Caij on 2016/5/31.
 */
public class QueryWeiboCommentResponse extends Response {

    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

package com.caij.emore.bean.event;

import com.caij.emore.bean.Comment;

/**
 * Created by Caij on 2016/10/10.
 */

public class CommentEvent extends Event {

    public Comment comment;
    public long statusId;

    public CommentEvent(String type, Comment comment, long statusId) {
        super(type);
        this.comment = comment;
        this.statusId = statusId;
    }
}

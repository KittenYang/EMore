package com.caij.emore.bean;

import android.text.Spannable;

import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.User;

import java.io.Serializable;

/**
 * Created by Caij on 2016/6/16.
 */
public class Comment extends Response implements Serializable {

    private String created_at;
    private long id;
    private String text;
    private String source;
    private User user;
    private Comment reply_comment;
    private Status status;

    private transient Spannable textSpannableString;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getReply_comment() {
        return reply_comment;
    }

    public void setReply_comment(Comment reply_comment) {
        this.reply_comment = reply_comment;
    }

    public Spannable getTextSpannableString() {
        return textSpannableString;
    }

    public void setTextSpannableString(Spannable textSpannableString) {
        this.textSpannableString = textSpannableString;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        Comment comment = (Comment) o;
        return id == comment.getId();
    }
}

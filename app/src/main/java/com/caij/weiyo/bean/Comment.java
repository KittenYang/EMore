package com.caij.weiyo.bean;

import android.text.SpannableString;

import java.io.Serializable;

/**
 * Created by Caij on 2016/6/16.
 */
public class Comment implements Serializable {

    private String created_at;
    private long id;
    private String text;
    private String source;
    private User user;
    private Comment reply_comment;
    private Weibo status;

    private transient SpannableString textSpannableString;

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

    public SpannableString getTextSpannableString() {
        return textSpannableString;
    }

    public void setTextSpannableString(SpannableString textSpannableString) {
        this.textSpannableString = textSpannableString;
    }

    public Weibo getStatus() {
        return status;
    }

    public void setStatus(Weibo status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        Comment comment = (Comment) o;
        return id == comment.getId();
    }
}

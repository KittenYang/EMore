package com.caij.emore.bean;

import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;

/**
 * Created by Caij on 2016/7/21.
 */
public class Attitude {
//    "id": 3658318092579477,
//            "created_at": "Sun Dec 22 14:41:04 +0800 2013",
//            "attitude": "heart",
//            "last_attitude": "",
//            "source": "<a href=\"http://weibo.com/\" rel=\"nofollow\">新浪微博</a>",
    private User user;
    private Weibo status;
    private long id;
    private String created_at;
    private String attitude;
    private String last_attitude;
    private String source;
    private String text;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Weibo getStatus() {
        return status;
    }

    public void setStatus(Weibo status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getAttitude() {
        return attitude;
    }

    public void setAttitude(String attitude) {
        this.attitude = attitude;
    }

    public String getLast_attitude() {
        return last_attitude;
    }

    public void setLast_attitude(String last_attitude) {
        this.last_attitude = last_attitude;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
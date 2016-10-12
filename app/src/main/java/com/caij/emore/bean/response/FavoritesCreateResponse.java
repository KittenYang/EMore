package com.caij.emore.bean.response;


import com.caij.emore.database.bean.Status;

/**
 * Created by Caij on 2016/7/2.
 */
public class FavoritesCreateResponse extends Response {

    private Status status;

    private String favorited_time;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getFavorited_time() {
        return favorited_time;
    }

    public void setFavorited_time(String favorited_time) {
        this.favorited_time = favorited_time;
    }
}

package com.caij.emore.bean.response;

import com.caij.emore.bean.Weibo;

/**
 * Created by Caij on 2016/7/2.
 */
public class FavoritesCreateResponse {
    private Weibo status;

    private String favorited_time;

    public Weibo getStatus() {
        return status;
    }

    public void setStatus(Weibo status) {
        this.status = status;
    }

    public String getFavorited_time() {
        return favorited_time;
    }

    public void setFavorited_time(String favorited_time) {
        this.favorited_time = favorited_time;
    }
}

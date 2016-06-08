package com.caij.weiyo.source;

import com.caij.weiyo.bean.Weibo;

import java.util.List;

import rx.Observable;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboSource {


    /**
     * @param accessToken
     * @param since_id
     * @param max_id
     * @param count
     * @param page 查询页，最低从1开始
     * @return
     */
    public Observable<List<Weibo>> getFriendWeibo(String accessToken, long since_id, long max_id,
                                                  int count, int page);

    public void saveFriendWeibo(String accessToken, List<Weibo> weibos);

}

package com.caij.weiyo.source;

import com.caij.weiyo.bean.Weibo;

import java.util.List;

import rx.Observable;

/**
 * Created by Caij on 2016/5/31.
 */
public interface WeiboSource {

    public Observable<List<Weibo>> getFriendWeibo(String accessToken, long since_id, long max_id,
                                                  int count, int page, int feature);
}

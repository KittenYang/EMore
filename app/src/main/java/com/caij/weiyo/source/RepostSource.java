package com.caij.weiyo.source;

import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.QueryRepostWeiboResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/6/27.
 */
public interface RepostSource {

    public Observable<Weibo> commentForWeibo(String accessToken, String status, long weiboId);

    public Observable<QueryRepostWeiboResponse> getRepostWeibos(String accessToken, long id,
                                                                long since_id, long max_id,
                                                                int count, int page);
}

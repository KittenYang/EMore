package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.QueryRepostWeiboResponse;
import com.caij.weiyo.source.RepostSource;

import rx.Observable;

/**
 * Created by Caij on 2016/6/27.
 */
public class ServerRepostSource implements RepostSource {

    private WeiBoService mWeiBoService;

    public ServerRepostSource() {
        mWeiBoService = WeiBoService.Factory.create();
    }

    @Override
    public Observable<Weibo> commentForWeibo(String accessToken, String status, long weiboId) {
        return mWeiBoService.repostWeibo(accessToken, weiboId, status);
    }

    @Override
    public Observable<QueryRepostWeiboResponse> getRepostWeibos(String accessToken, long id, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getRepostWeibos(accessToken, id, since_id, max_id, count, page);
    }
}

package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.QueryWeiboResponse;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.source.WeiboSource;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class ServerWeiboSource implements WeiboSource{

    @Override
    public Observable<List<Weibo>> getFriendWeibo(String accessToken, long sinceId, long maxId,
                                                  int count, int page) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getFrientWeibo(accessToken, sinceId, maxId, count, page)
                .flatMap(new Func1<QueryWeiboResponse, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.just(queryWeiboResponse.getStatuses());
                    }
                });
    }

    @Override
    public void saveFriendWeibo(String accessToken, List<Weibo> weibos) {

    }
}

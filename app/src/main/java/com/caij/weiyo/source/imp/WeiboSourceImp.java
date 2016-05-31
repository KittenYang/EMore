package com.caij.weiyo.source.imp;

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
public class WeiboSourceImp implements WeiboSource{

    @Override
    public Observable<List<Weibo>> getFriendWeibo(String accessToken, long sinceId, long maxId,
                                                  int count, int page, int feature) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getFrientWeibo(accessToken, sinceId, maxId, count, page, feature)
                .flatMap(new Func1<QueryWeiboResponse, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.just(queryWeiboResponse.getStatuses());
                    }
                });
    }
}

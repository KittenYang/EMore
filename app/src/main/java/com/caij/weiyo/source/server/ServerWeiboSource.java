package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.response.QueryWeiboResponse;
import com.caij.weiyo.bean.Weibo;
import com.caij.weiyo.bean.response.UserWeiboResponse;
import com.caij.weiyo.source.WeiboSource;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class ServerWeiboSource implements WeiboSource{

    private WeiBoService mWeiBoService;

    public ServerWeiboSource() {
        mWeiBoService = WeiBoService.Factory.create();
    }

    @Override
    public Observable<List<Weibo>> getFriendWeibo(String accessToken, long sinceId, long maxId,
                                                  int count, int page) {
        return mWeiBoService.getFriendsWeibo(accessToken, sinceId, maxId, count, page)
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

    @Override
    public Observable<UserWeiboResponse> getUseWeibo(String accessToken, String name,  int feature, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getUserWeibos(accessToken, name, feature, since_id, max_id, count, page);
    }
}

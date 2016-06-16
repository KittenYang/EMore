package com.caij.weiyo.source.server;

import com.caij.weiyo.api.WeiBoService;
import com.caij.weiyo.bean.Comment;
import com.caij.weiyo.bean.response.QueryWeiboCommentResponse;
import com.caij.weiyo.source.CommentSource;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/6/16.
 */
public class ServerCommentSource implements CommentSource{

    @Override
    public Observable<List<Comment>> getCommentsByWeibo(String accessToken, long id, long since_id, long max_id, int count, int page) {
        WeiBoService service = WeiBoService.Factory.create();
        return service.getCommentsByWeibo(accessToken, id, since_id, max_id, count, page)
                .flatMap(new Func1<QueryWeiboCommentResponse, Observable<List<Comment>>>() {
                    @Override
                    public Observable<List<Comment>> call(QueryWeiboCommentResponse queryWeiboCommentResponse) {
                        return Observable.just(queryWeiboCommentResponse.getComments());
                    }
                });
    }
}

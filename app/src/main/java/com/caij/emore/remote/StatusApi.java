package com.caij.emore.remote;

import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.Weibo;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public interface StatusApi {

    public Observable<QueryWeiboResponse> getFriendWeibo(long uid, long sinceId, long maxId,
                                                         int count, int page);

    public Observable<UserWeiboResponse> getUseWeibo(long uid, int feature, long since_id, long max_id, int count, int page);

    public Observable<Weibo> publishWeiboOfText(String content);

    public Observable<Weibo> publishWeiboOfOneImage(final String content, final String imagePath);

    public Observable<UploadImageResponse> uploadWeiboOfOneImage(String imagePath);

    public Observable<Weibo> publishWeiboOfMultiImage(String status, String picIds);

    public Observable<Weibo> deleteWeibo(long id);

    public Observable<FavoritesCreateResponse> collectWeibo(long id);

    public Observable<FavoritesCreateResponse> uncollectWeibo(long id);

    public Observable<Weibo> repostWeibo(String status, long weiboId);

    public Observable<QueryWeiboResponse> getWeiboMentions(long since_id, long max_id, int count, int page);

    public Observable<Weibo> getWeiboById(int isGetLongText, long id);

    public Observable<QueryWeiboResponse> getWeibosByIds(String ids);

    public Observable<QueryWeiboResponse> getTopicsByKey(String q, int page, int count);

    public Observable<QueryWeiboResponse> getSearchWeibo(String q, int page, int count);

    public Observable<QueryRepostWeiboResponse> getRepostWeibos(long id, long since_id, long max_id, int count, int page);

}

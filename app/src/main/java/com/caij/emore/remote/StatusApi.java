package com.caij.emore.remote;

import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRelayStatusResponse;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.bean.response.UserStatusesResponse;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.UploadImageResponse;

import rx.Observable;

/**
 * Created by Caij on 2016/10/9.
 */

public interface StatusApi {

    public Observable<QueryStatusResponse> getFriendWeibo(long uid, long sinceId, long maxId,
                                                          int count, int page);

    public Observable<UserStatusesResponse> getUseWeibo(long uid, int feature, long since_id, long max_id, int count, int page);

    public Observable<Status> publishWeiboOfText(String content);

    public Observable<Status> publishWeiboOfOneImage(final String content, final String imagePath);

    public Observable<UploadImageResponse> uploadWeiboOfOneImage(String imagePath);

    public Observable<Status> publishWeiboOfMultiImage(String status, String picIds);

    public Observable<Status> deleteWeibo(long id);

    public Observable<FavoritesCreateResponse> collectWeibo(long id);

    public Observable<FavoritesCreateResponse> uncollectWeibo(long id);

    public Observable<Status> repostWeibo(String status, long weiboId);

    public Observable<QueryStatusResponse> getWeiboMentions(long since_id, long max_id, int count, int page);

    public Observable<Status> getWeiboById(int isGetLongText, long id);

    public Observable<QueryStatusResponse> getWeibosByIds(String ids);

    public Observable<QueryStatusResponse> getTopicsByKey(String q, int page, int count);

    public Observable<QueryStatusResponse> getSearchWeibo(String q, int page, int count);

    public Observable<QueryRelayStatusResponse> getRepostWeibos(long id, long since_id, long max_id, int count, int page);

}

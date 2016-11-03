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

    public Observable<QueryStatusResponse> getFriendStatus(long uid, long sinceId, long maxId,
                                                           int count, int page);

    public Observable<UserStatusesResponse> getUseStatus(long uid, int feature, long since_id, long max_id, int count, int page);

    public Observable<Status> publishStatusOfText(String content);

    public Observable<Status> publishStatusOfOneImage(final String content, final String imagePath);

    public Observable<UploadImageResponse> uploadStatusOfOneImage(String imagePath);

    public Observable<Status> publishStatusOfMultiImage(String status, String picIds);

    public Observable<Status> deleteStatusById(long id);

    public Observable<FavoritesCreateResponse> collectStatus(long id);

    public Observable<FavoritesCreateResponse> unCollectStatus(long id);

    public Observable<Status> relayStatus(String status, long weiboId);

    public Observable<QueryStatusResponse> getStatusMentions(long since_id, long max_id, int count, int page);

    public Observable<Status> getStatusById(int isGetLongText, long id);

    public Observable<QueryStatusResponse> getStatusByIds(String ids);

    public Observable<QueryStatusResponse> getTopicsByKey(String q, int page, int count);

    public Observable<QueryStatusResponse> getSearchStatus(String q, int page, int count);

    public Observable<QueryRelayStatusResponse> getRelayStatus(long id, long since_id, long max_id, int count, int page);

    public Observable<QueryStatusResponse> getGroupStatus(long list_id, long sinceId, long maxId,
                                                           int count, int page);

}

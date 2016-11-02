package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRelayStatusResponse;
import com.caij.emore.bean.response.QueryStatusResponse;
import com.caij.emore.bean.response.UserStatusesResponse;
import com.caij.emore.database.bean.Status;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;
import com.caij.emore.utils.rxjava.RxUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/10/9.
 */

public class StatusApiImp implements StatusApi {

    private WeiBoService mWeiBoService;
    private WeiCoService mWeiCoService;

    public StatusApiImp() {
        mWeiBoService = WeiBoService.Factory.create();
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<QueryStatusResponse> getFriendStatus(long uid, long sinceId, long maxId, int count, int page) {
        return mWeiCoService.getFriendsWeibo(sinceId, maxId, count, page);
    }

    @Override
    public Observable<UserStatusesResponse> getUseStatus(long uid, int feature, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getUserWeibos(uid, feature, since_id, max_id, count, page);
    }

    @Override
    public Observable<Status> publishStatusOfText(String content) {
        return mWeiCoService.publishWeiboOfOnlyText(content);
    }

    @Override
    public Observable<Status> publishStatusOfOneImage(final String content, String imagePath) {
        final File file = new File(imagePath);
        return RxUtil.createDataObservable(new RxUtil.Provider<String>() {
                @Override
                public String getData() throws Exception {
                    return ImageUtil.getImageType(file).getValue();
                }
            }).flatMap(new Func1<String, Observable<Status>>() {
                @Override
                public Observable<Status> call(String type) {
                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse("image/" + type), file);
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("pic", file.getName(), requestFile);
                    return mWeiCoService.publishWeiboOfOneImage(content, body);
                }
            });
    }

    @Override
    public Observable<UploadImageResponse> uploadStatusOfOneImage(String imagePath) {
        final File file = new File(imagePath);
        return RxUtil.createDataObservable(new RxUtil.Provider<String>() {
                @Override
                public String getData() throws Exception {
                    return ImageUtil.getImageType(file).getValue();
                }
            }).flatMap(new Func1<String, Observable<UploadImageResponse>>() {
                @Override
                public Observable<UploadImageResponse> call(String type) {
                    RequestBody fileBody =
                            RequestBody.create(MediaType.parse("image/" + type), file);
                    MultipartBody.Part filePart =
                            MultipartBody.Part.createFormData("pic", file.getName(), fileBody);
                    return mWeiBoService.uploadWeiboOfOneImage(filePart);
                }
            });
    }

    @Override
    public Observable<Status> publishStatusOfMultiImage(String status, String picIds) {
        return mWeiCoService.publishWeiboOfMultiImage(status, picIds);
    }

    @Override
    public Observable<Status> deleteStatusById(long id) {
        return mWeiCoService.statusesDestroy(id);
    }

    @Override
    public Observable<FavoritesCreateResponse> collectStatus(long id) {
        return mWeiCoService.favoritesCreate(id);
    }

    @Override
    public Observable<FavoritesCreateResponse> unCollectStatus(long id) {
        return mWeiCoService.favoritesDestroy(id);
    }

    @Override
    public Observable<Status> relayStatus(String status, long statusId) {
        return mWeiCoService.repostWeibo(statusId, status);
    }

    @Override
    public Observable<QueryStatusResponse> getStatusMentions(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getWeiboMentions(since_id, max_id, count, page);
    }

    @Override
    public Observable<Status> getStatusById(int isGetLongText, long id) {
        return mWeiCoService.getWeiboById(id, isGetLongText);
    }

    @Override
    public Observable<QueryStatusResponse> getStatusByIds(String ids) {
        return mWeiCoService.getWeibsoByIds(ids);
    }

    @Override
    public Observable<QueryStatusResponse> getTopicsByKey(String q, int page, int count) {
        return mWeiCoService.searchStatus(q, count, page);
    }

    @Override
    public Observable<QueryStatusResponse> getSearchStatus(String q, int page, int count) {
        return mWeiCoService.searchStatus(q, count, page);
    }

    @Override
    public Observable<QueryRelayStatusResponse> getRelayStatus(long id, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getRepostWeibos(id, since_id, max_id, count, page);
    }

}

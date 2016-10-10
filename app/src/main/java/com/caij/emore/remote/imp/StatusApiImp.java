package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.remote.StatusApi;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;

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
    public Observable<QueryWeiboResponse> getFriendWeibo(long uid, long sinceId, long maxId, int count, int page) {
        return mWeiCoService.getFriendsWeibo(sinceId, maxId, count, page);
    }

    @Override
    public Observable<UserWeiboResponse> getUseWeibo(long uid, int feature, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getUserWeibos(uid, feature, since_id, max_id, count, page);
    }

    @Override
    public Observable<Weibo> publishWeiboOfText(String content) {
        return mWeiCoService.publishWeiboOfOnlyText(content);
    }

    @Override
    public Observable<Weibo> publishWeiboOfOneImage(final String content, String imagePath) {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file).getValue();
                    subscriber.onNext(type);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    LogUtil.d(StatusApiImp.this, e.getMessage());
                    subscriber.onError(e);
                }
            }
        }).flatMap(new Func1<String, Observable<Weibo>>() {
            @Override
            public Observable<Weibo> call(String type) {
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("image/" + type), file);
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("pic", file.getName(), requestFile);
                return mWeiCoService.publishWeiboOfOneImage(content, body);
            }
        });
    }

    @Override
    public Observable<UploadImageResponse> uploadWeiboOfOneImage(String imagePath) {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file).getValue();
                    subscriber.onNext(type);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    LogUtil.d(StatusApiImp.this, e.getMessage());
                    subscriber.onError(e);
                }
            }
        })
                .flatMap(new Func1<String, Observable<UploadImageResponse>>() {
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
    public Observable<Weibo> publishWeiboOfMultiImage(String status, String picIds) {
        return mWeiCoService.publishWeiboOfMultiImage(status, picIds);
    }

    @Override
    public Observable<Weibo> deleteWeibo(long id) {
        return mWeiCoService.statusesDestroy(id);
    }

    @Override
    public Observable<FavoritesCreateResponse> collectWeibo(long id) {
        return mWeiCoService.favoritesCreate(id);
    }

    @Override
    public Observable<FavoritesCreateResponse> uncollectWeibo(long id) {
        return mWeiCoService.favoritesDestroy(id);
    }

    @Override
    public Observable<Weibo> repostWeibo(String status, long weiboId) {
        return mWeiCoService.repostWeibo(weiboId, status);
    }

    @Override
    public Observable<QueryWeiboResponse> getWeiboMentions(long since_id, long max_id, int count, int page) {
        return mWeiCoService.getWeiboMentions(since_id, max_id, count, page);
    }

    @Override
    public Observable<Weibo> getWeiboById(int isGetLongText, long id) {
        return mWeiCoService.getWeiboById(id, isGetLongText);
    }

    @Override
    public Observable<QueryWeiboResponse> getWeibosByIds(String ids) {
        return mWeiCoService.getWeibsoByIds(ids);
    }

    @Override
    public Observable<QueryWeiboResponse> getTopicsByKey(String q, int page, int count) {
        return mWeiCoService.searchStatus(q, count, page);
    }

    @Override
    public Observable<QueryWeiboResponse> getSearchWeibo(String q, int page, int count) {
        return mWeiCoService.searchStatus(q, count, page);
    }

    @Override
    public Observable<QueryRepostWeiboResponse> getRepostWeibos(long id, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getRepostWeibos(id, since_id, max_id, count, page);
    }

}

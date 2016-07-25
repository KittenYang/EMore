package com.caij.emore.source.server;

import com.caij.emore.Key;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.source.WeiboSource;
import com.caij.emore.utils.ImageUtil;
import com.caij.emore.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Caij on 2016/5/31.
 */
public class ServerWeiboSource implements WeiboSource{

    private WeiBoService mWeiBoService;
    private WeiCoService mWeiCoService;

    public ServerWeiboSource() {
        mWeiBoService = WeiBoService.Factory.create();
        mWeiCoService = WeiCoService.WeiCoFactory.create();
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
    public void saveWeibos(String accessToken, List<Weibo> weibos) {

    }

    @Override
    public Observable<UserWeiboResponse> getUseWeibo(String accessToken, String name,  int feature, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getUserWeibos(accessToken, name, feature, since_id, max_id, count, page);
    }


    @Override
    public Observable<Weibo> publishWeiboOfText(String token, String content) {
        return mWeiBoService.publishWeiboOfOnlyText(token, content);
    }

    @Override
    public Observable<Weibo> publishWeiboOfOneImage(final String token,
                                                    final String content, final String imagePath) {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file);
                    subscriber.onNext(type);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    LogUtil.d(ServerWeiboSource.this, e.getMessage());
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
                return mWeiBoService.publishWeiboOfOneImage("OAuth2 " + token, content, body);
            }
        });
    }

    @Override
    public Observable<UploadImageResponse> uploadWeiboOfOneImage(final String access_token, String imagePath) throws IOException {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file);
                    subscriber.onNext(type);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    LogUtil.d(ServerWeiboSource.this, e.getMessage());
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
                RequestBody tokenBody =
                        RequestBody.create(null, access_token);
                return mWeiBoService.uploadWeiboOfOneImage(tokenBody, filePart);
            }
        });
    }

    @Override
    public Observable<Weibo> publishWeiboOfMultiImage(String accessToken, String status, String picIds) {
        return mWeiBoService.publishWeiboOfMultiImage(accessToken, status, picIds);
    }

    @Override
    public Observable<Weibo> deleteWeibo(String accessToken, long id) {
        return mWeiBoService.statusesDestroy(accessToken, id);
    }

    @Override
    public Observable<FavoritesCreateResponse> collectWeibo(String accessToken, long id) {
        return mWeiBoService.favoritesCreate(accessToken, id);
    }

    @Override
    public Observable<FavoritesCreateResponse> uncollectWeibo(String accessToken, long id) {
        return mWeiBoService.favoritesDestroy(accessToken, id);
    }

    @Override
    public Observable<List<Comment>> getCommentsByWeibo(String accessToken, long id, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getCommentsByWeibo(accessToken, id, since_id, max_id, count, page)
                .flatMap(new Func1<QueryWeiboCommentResponse, Observable<List<Comment>>>() {
                    @Override
                    public Observable<List<Comment>> call(QueryWeiboCommentResponse queryWeiboCommentResponse) {
                        return Observable.just(queryWeiboCommentResponse.getComments());
                    }
                });
    }

    @Override
    public Observable<Comment> commentForWeibo(String accessToken, String comment, long weiboId) {
        return mWeiBoService.createCommentForWeibo(accessToken, comment, weiboId);
    }

    @Override
    public Observable<Comment> deleteComment(String accessToken, long cid) {
        return mWeiBoService.deleteComment(accessToken, cid);
    }

    @Override
    public Observable<Comment> replyComment(String accessToken, String comment, long cid, long weiboId) {
        return mWeiBoService.replyComment(accessToken, cid, weiboId, comment);
    }

    @Override
    public Observable<Weibo> repostWeibo(String accessToken, String status, long weiboId) {
        return mWeiBoService.repostWeibo(accessToken, weiboId, status);
    }

    @Override
    public Observable<QueryWeiboResponse> getWeiboMentions(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getWeiboMentions(accessToken, since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getCommentsMentions(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getCommentsMentions(accessToken, since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getPublishComments(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getPublishComments(accessToken, since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getAcceptComments(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getAcceptComments(accessToken, since_id, max_id, count, page);
    }

    @Override
    public Observable<Attitude> attitudesWeibo(String token, String source, String attitude, long weiboId) {
        return mWeiCoService.attitudesWeibo(token, source, attitude, weiboId);
    }

    @Override
    public Observable<Response> destoryAttitudesWeibo(String token, String source, String attitude, long weiboId) {
        return mWeiCoService.destoryAttitudesWeibo(token, source, attitude, weiboId);
    }

    @Override
    public boolean getAttitudes(long id) {
        return false;
    }

    @Override
    public Observable<Weibo> getWeiboById(String token, String source, int isGetLongText, long id) {
        return mWeiCoService.getWeiboById(token, isGetLongText, source, id);
    }

    @Override
    public Observable<Weibo> getWeiboById(String token, long id) {
        return mWeiBoService.getWeiboById(token, id);
    }

    @Override
    public Observable<List<Attitude>> getWeiboAttiyudes(String token, long id, int page, int count) {
        return mWeiBoService.getWeiboAttitudes(token, id, page, count)
                .flatMap(new Func1<QueryWeiboAttitudeResponse, Observable<List<Attitude>>>() {
                    @Override
                    public Observable<List<Attitude>> call(QueryWeiboAttitudeResponse queryWeiboAttitudeResponse) {
                        return Observable.just(queryWeiboAttitudeResponse.getAttitudes());
                    }
                });
    }

    @Override
    public void saveWeibo(String mToken, Weibo weibo) {

    }

    @Override
    public void saveUploadImageResponse(UploadImageResponse uploadImageResponse) {

    }

    @Override
    public Observable<List<Attitude>> getToMeAttiyudes(String token, long maxId, long sinceId,
                                                       String source, String from, int page, int count) {
        return mWeiCoService.getToMeAttitudes(token, sinceId, maxId, source, from, page, count)
                .flatMap(new Func1<QueryWeiboAttitudeResponse, Observable<List<Attitude>>>() {
                    @Override
                    public Observable<List<Attitude>> call(QueryWeiboAttitudeResponse queryWeiboAttitudeResponse) {
                        return Observable.just(queryWeiboAttitudeResponse.getAttitudes());
                    }
                });
    }

    @Override
    public Observable<List<Weibo>> getWeibosByIds(String access_token, String ids) {
        return mWeiBoService.getWeibsoByIds(access_token, ids)
                .flatMap(new Func1<QueryWeiboResponse, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.just(queryWeiboResponse.getStatuses());
                    }
                });
    }

    @Override
    public Observable<WeiboIds> getHotWeibosIds(String access_token, int page) {
        return mWeiCoService.getHotWeiboIds(Key.WEICO_API_URL, "get_cat_list", "default", "102803", page);
    }

    @Override
    public Observable<List<Weibo>> getTopicsByKey(String access_token, String q, int page, int count) {
        return mWeiBoService.getTopicsByKey(access_token, q, count, page)
                .flatMap(new Func1<QueryWeiboResponse, Observable<List<Weibo>>>() {
                    @Override
                    public Observable<List<Weibo>> call(QueryWeiboResponse queryWeiboResponse) {
                        return Observable.just(queryWeiboResponse.getStatuses());
                    }
                });
    }

    @Override
    public Observable<QueryRepostWeiboResponse> getRepostWeibos(String accessToken, long id, long since_id, long max_id, int count, int page) {
        return mWeiBoService.getRepostWeibos(accessToken, id, since_id, max_id, count, page);
    }
}

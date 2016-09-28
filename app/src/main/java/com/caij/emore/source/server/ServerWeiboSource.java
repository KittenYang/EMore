package com.caij.emore.source.server;

import com.caij.emore.Key;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.AttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.bean.response.WeiboAttitudeResponse;
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
    public Observable<QueryWeiboResponse> getFriendWeibo(String accessToken, long uid, long sinceId, long maxId,
                                                         int count, int page) {
        return mWeiCoService.getFriendsWeibo(sinceId, maxId, count, page);
    }

    @Override
    public void saveWeibos(String accessToken, List<Weibo> weibos) {

    }

    @Override
    public Observable<UserWeiboResponse> getUseWeibo(String accessToken, long uid, int feature, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getUserWeibos(uid, feature, since_id, max_id, count, page);
    }


    @Override
    public Observable<Weibo> publishWeiboOfText(String token, String content) {
        return mWeiCoService.publishWeiboOfOnlyText(content);
    }

    @Override
    public Observable<Weibo> publishWeiboOfOneImage(final String token,
                                                    final String content, final String imagePath) {
        final File file = new File(imagePath);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String type  = ImageUtil.getImageType(file).getValue();
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
                return mWeiCoService.publishWeiboOfOneImage(content, body);
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
                    String type  = ImageUtil.getImageType(file).getValue();
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
                return mWeiBoService.uploadWeiboOfOneImage(filePart);
            }
        });
    }

    @Override
    public Observable<Weibo> publishWeiboOfMultiImage(String accessToken, String status, String picIds) {
        return mWeiCoService.publishWeiboOfMultiImage(status, picIds);
    }

    @Override
    public Observable<Weibo> deleteWeibo(String accessToken, long id) {
        return mWeiCoService.statusesDestroy(id);
    }

    @Override
    public Observable<FavoritesCreateResponse> collectWeibo(String accessToken, long id) {
        return mWeiCoService.favoritesCreate(id);
    }

    @Override
    public Observable<FavoritesCreateResponse> uncollectWeibo(String accessToken, long id) {
        return mWeiCoService.favoritesDestroy(id);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getCommentsByWeibo(String accessToken, long id, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getCommentsByWeibo(id, since_id, max_id, count, page);
    }

    @Override
    public Observable<Comment> commentForWeibo(String accessToken, String comment, long weiboId) {
        return mWeiCoService.createCommentForWeibo(comment, weiboId);
    }

    @Override
    public Observable<Comment> deleteComment(String accessToken, long cid) {
        return mWeiCoService.deleteComment(cid);
    }

    @Override
    public Observable<Comment> replyComment(String accessToken, String comment, long cid, long weiboId) {
        return mWeiCoService.replyComment(cid, weiboId, comment);
    }

    @Override
    public Observable<Weibo> repostWeibo(String accessToken, String status, long weiboId) {
        return mWeiCoService.repostWeibo(weiboId, status);
    }

    @Override
    public Observable<QueryWeiboResponse> getWeiboMentions(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getWeiboMentions(since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getCommentsMentions(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getCommentsMentions(since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getPublishComments(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getPublishComments(since_id, max_id, count, page);
    }

    @Override
    public Observable<QueryWeiboCommentResponse> getAcceptComments(String accessToken, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getAcceptComments(since_id, max_id, count, page);
    }

    @Override
    public Observable<Attitude> attitudesWeibo(String token, String attitude, long weiboId) {
        return mWeiCoService.attitudesWeibo(attitude, weiboId);
    }

    @Override
    public Observable<Response> destoryAttitudesWeibo(String token, String attitude, long weiboId) {
        return mWeiCoService.destoryAttitudesWeibo(attitude, weiboId);
    }

    @Override
    public Observable<Weibo> getWeiboById(String token, int isGetLongText, long id) {
        return mWeiCoService.getWeiboById(id, isGetLongText);
    }

    @Override
    public Observable<Weibo> getWeiboById(String token, long id) {
        return mWeiCoService.getWeiboById(id, 0);
    }

    @Override
    public Observable<WeiboAttitudeResponse> getWeiboAttiyudes(String token, long id, int page, int count) {
        return mWeiCoService.getWeiboAttitudes(id, page, count);
    }

    @Override
    public void saveWeibo(String mToken, Weibo weibo) {

    }

    @Override
    public void saveUploadImageResponse(UploadImageResponse uploadImageResponse) {

    }

    @Override
    public Observable<AttitudeResponse> getToMeAttiyudes(String token, long maxId, long sinceId,
                                                         int page, int count) {
        return mWeiCoService.getToMeAttitudes(sinceId, maxId, page, count);
    }

    @Override
    public Observable<QueryWeiboResponse> getWeibosByIds(String access_token, String ids) {
        return mWeiCoService.getWeibsoByIds(ids);
    }

    @Override
    public Observable<WeiboIds> getHotWeibosIds(String access_token, int page) {
        return mWeiCoService.getHotWeiboIds(Key.WEICO_API_URL, "get_cat_list", "default", "102803", page);
    }

    @Override
    public Observable<QueryWeiboResponse> getTopicsByKey(String access_token, String q, int page, int count) {
        return mWeiBoService.getTopicsByKey(q, count, page);
    }

    @Override
    public Observable<QueryWeiboResponse> getSearchWeibo(String access_token, String q, int page, int count) {
        return mWeiCoService.searchStatus(q, count, page);
    }

    @Override
    public Observable<QueryRepostWeiboResponse> getRepostWeibos(String accessToken, long id, long since_id, long max_id, int count, int page) {
        return mWeiCoService.getRepostWeibos(id, since_id, max_id, count, page);
    }
}

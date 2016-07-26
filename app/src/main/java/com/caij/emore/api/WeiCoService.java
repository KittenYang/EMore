package com.caij.emore.api;

import com.caij.emore.Key;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Caij on 2016/6/15.
 */
public interface WeiCoService {

    public static class WeiCoFactory {

        static WeiCoService sWeiCoService;

        static public WeiCoService create() {
            if (sWeiCoService == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Key.WEICO_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .callFactory(OkHttpClientProvider.getDefaultOkHttpClient())
                        .build();
                sWeiCoService = retrofit.create(WeiCoService.class);
            }
            return sWeiCoService;
        }
    }

    @FormUrlEncoded
    @POST("/2/account/login")
    public abstract Observable<WeiCoLoginResponse> loginForGsid(@Field("access_token") String access_token,
                                                                @Field("source") String source, @Field("i") String i,
                                                                @Field("getcookie") String getcookie);

    @FormUrlEncoded
    @POST("/2/like/set_like")
    public Observable<Attitude> attitudesWeibo(@Field("access_token") String access_token,
                                               @Field("source") String source,
                                               @Field("attitude") String attitude, @Field("id") long weiboId);

    @FormUrlEncoded
    @POST("/2/like/cancel_like")
    public Observable<Response> destoryAttitudesWeibo(@Field("access_token") String access_token,
                                                      @Field("source") String source,
                                               @Field("attitude") String attitude, @Field("id") long weiboId);

    @GET("/2/statuses/show")
    public Observable<Weibo> getWeiboById(@Query("access_token") String access_token,
                                          @Query("isGetLongText") int isGetLongText,
                                          @Query("source") String source, @Query("id") long weiboId);

    @FormUrlEncoded
    @POST("/2/like/update")
    Observable<Response> toAttitudeComment(@Field("access_token") String accessToken,
                                           @Field("object_id") long id,
                                           @Field("object_type") String type,
                                           @Field("source") String source);

    @FormUrlEncoded
    @POST("/2/like/destroy")
    Observable<Response> destoryAttitudeComment(@Field("access_token") String accessToken,
                                                @Field("object_id") long id,
                                                @Field("object_type") String type,
                                                @Field("source") String source);

    @GET("/2/like/to_me")
    Observable<QueryWeiboAttitudeResponse> getToMeAttitudes(@Query("access_token") String accessToken,
                                                            @Query("since_id") long since_id,
                                                            @Query("max_id") long max_id,
                                                            @Query("source") String source,
                                                            @Query("from") String from,
                                                            @Query("page") int page,
                                                            @Query("count") int count);

    @GET("/2/remind/unread_count")
    Observable<UnReadMessage> getUnreadMessageCount(@Query("access_token") String accessToken,
                                                    @Query("source") String source,
                                                    @Query("from") String from,
                                                    @Query("uid") long uid);

    @FormUrlEncoded
    @POST("/2/remind/set_count")
    public Observable<Response> resetUnReadMsg(@Field("access_token") String accessToken,
                                  @Field("source") String source,
                                  @Field("from") String from,
                                  @Field("type") String type,
                                   @Field("value") int value);

    @GET
    public Observable<WeiboIds> getHotWeiboIds(@Url String url, @Query("a") String a, @Query("c") String c,
                                               @Query("catlog_id") String catlog_id, @Query("page") int page);

    @GET("/2/search/statuses")
    Observable<UnReadMessage> getTopics(@Query("access_token") String accessToken,
                                                    @Query("source") String source,
                                                    @Query("from") String from,
                                                    @Query("uid") long uid);

    @GET("/2/search/statuses")
    public Observable<QueryWeiboResponse> searchStatus(@Query("access_token") String accessToken,
                                                       @Query("source") String source,
                                                       @Query("from") String from,
                                                       @Query("q") String q,
                                                       @Query("count") int count,
                                                       @Query("page") int page);


    @GET("/2/search/users")
    public Observable<FriendshipResponse> searchUsers(@Query("access_token") String accessToken,
                                                      @Query("source") String source,
                                                      @Query("from") String from,
                                                      @Query("q") String q,
                                                      @Query("count") int count,
                                                      @Query("page") int page);

}

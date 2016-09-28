package com.caij.emore.api;

import com.caij.emore.AppApplication;
import com.caij.emore.Key;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.bean.Article;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.VideoInfo;
import com.caij.emore.bean.WeiboIds;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserWeiboResponse;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.database.bean.UnReadMessage;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;
import com.caij.emore.utils.rxjava.GosnConverterFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Caij on 2016/6/15.
 */
public interface WeiCoService {

    public static final String PARAMETER_SOURCE_NAME = "source";
    public static final String PARAMETER_FROM_NAME = "from";
    public static final String PARAMETER_TOKEN_NAME = "access_token";

    public static class WeiCoFactory {

        private static final long READ_TIMEOUT = 10;
        private static final long WRITE_TIMEOUT = 10;
        private static final long CONNECT_TIMEOUT = 5;
        static WeiCoService sWeiCoService;

        static public WeiCoService create() {
            if (sWeiCoService == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Key.WEICO_BASE_URL)
                        .addConverterFactory(GosnConverterFactory.create(GsonUtils.getGson()))
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .callFactory(createOkHttpClient())
                        .build();
                sWeiCoService = retrofit.create(WeiCoService.class);
            }
            return sWeiCoService;
        }


        static OkHttpClient createOkHttpClient() {
            return OkHttpClientProvider.getDefaultOkHttpClient()
                    .newBuilder()
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();

                            request = interceptRequest(request);

                            return chain.proceed(request);
                        }
                    })
                    .build();
        }

        /**
         * 添加拦截器 添加通用参数
         * @param request
         * @return
         */
        static Request interceptRequest(Request request) {
            RequestBody requestBody = request.body();
            String token = UserPrefs.get(AppApplication.getInstance()).getToken().getAccess_token();
            if (requestBody == null) {  //get
                HttpUrl url = request.url().newBuilder()
                        .addQueryParameter(PARAMETER_SOURCE_NAME, Key.WEICO_APP_ID)
                        .addQueryParameter(PARAMETER_FROM_NAME, Key.WEICO_APP_FROM)
                        .addQueryParameter(PARAMETER_TOKEN_NAME, token)
                        .build();
                request = request.newBuilder().url(url).build();
            }else if (requestBody instanceof FormBody) {
                FormBody formBody = (FormBody) requestBody;
                FormBody.Builder builder = new FormBody.Builder();
                for (int i = 0; i < formBody.size(); i ++) {
                    builder.add(formBody.name(i), formBody.value(i));
                }

                builder.add(PARAMETER_SOURCE_NAME, Key.WEICO_APP_ID);
                builder.add(PARAMETER_FROM_NAME, Key.WEICO_APP_FROM);
                builder.add(PARAMETER_TOKEN_NAME, token);

                formBody = builder.build();

                request = request.newBuilder().post(formBody).build();
            }else if (requestBody instanceof MultipartBody) {
                MultipartBody multipartBody = (MultipartBody) requestBody;
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(multipartBody.type());
                builder.addFormDataPart(PARAMETER_SOURCE_NAME, Key.WEICO_APP_ID);
                builder.addFormDataPart(PARAMETER_FROM_NAME, Key.WEICO_APP_FROM);
                builder.addFormDataPart(PARAMETER_TOKEN_NAME, token);

                for (MultipartBody.Part part : multipartBody.parts()) {
                    builder.addPart(part);
                }

                request = request.newBuilder().post(builder.build()).build();
            }

            return request;
        }
    }

    @FormUrlEncoded
    @POST("/2/account/login")
    public abstract Observable<WeiCoLoginResponse> loginForGsid(@Field("source") String source, @Field("i") String i,
                                                                @Field("getcookie") String getcookie);

    @GET("2/statuses/friends_timeline")
    Observable<QueryWeiboResponse> getFriendsWeibo(@Query("since_id") long since_id, @Query("max_id") long max_id,
                                                   @Query("count") int count, @Query("page") int page);

    @GET("/2/statuses/show")
    Observable<Weibo> getWeiboById(@Query("id") long id, @Query("isGetLongText") int isGetLongText);

    @GET("2/statuses/user_timeline")
    Observable<UserWeiboResponse> getUserWeibos(@Query("uid") long uid, @Query("feature") int feature,
                                                @Query("since_id") long since_id, @Query("max_id") long max_id,
                                                @Query("count") int count, @Query("page") int page);

    @FormUrlEncoded
    @POST("/2/like/set_like")
    public Observable<Attitude> attitudesWeibo(@Field("attitude") String attitude, @Field("id") long weiboId);

    @GET("2/comments/to_me")
    Observable<QueryWeiboCommentResponse> getAcceptComments(@Query("since_id") long since_id, @Query("max_id") long max_id,
                                                            @Query("count") int count, @Query("page") int page);

    @FormUrlEncoded
    @POST("/2/like/cancel_like")
    public Observable<Response> destoryAttitudesWeibo(@Field("attitude") String attitude, @Field("id") long weiboId);

//    @GET("/2/statuses/show")
//    public Observable<Weibo> getWeiboById(@Query("access_token") String access_token,
//                                          @Query("isGetLongText") int isGetLongText,
//                                          @Query("source") String source, @Query("id") long weiboId);

    @FormUrlEncoded
    @POST("/2/like/update")
    Observable<Response> toAttitudeComment(@Field("object_id") long id, @Field("object_type") String type);

    @FormUrlEncoded
    @POST("/2/like/destroy")
    Observable<Response> destoryAttitudeComment(@Field("object_id") long id,
                                                @Field("object_type") String type);

    @GET("2/statuses/mentions")
    Observable<QueryWeiboResponse> getWeiboMentions(@Query("since_id") long since_id, @Query("max_id") long max_id,
                                                    @Query("count") int count, @Query("page") int page);

    @GET("2/statuses/show_batch")
    Observable<QueryWeiboResponse> getWeibsoByIds(@Query("ids") String ids);

    @GET("2/comments/mentions")
    Observable<QueryWeiboCommentResponse> getCommentsMentions(@Query("since_id") long since_id,
                                                              @Query("max_id") long max_id,
                                                              @Query("count") int count,
                                                              @Query("page") int page);

    @GET("/2/like/to_me")
    Observable<QueryWeiboAttitudeResponse> getToMeAttitudes(@Query("since_id") long since_id, @Query("max_id") long max_id,
                                                            @Query("page") int page, @Query("count") int count);

    @GET("/2/remind/unread_count")
    Observable<UnReadMessage> getUnreadMessageCount(@Query("uid") long uid);

    @FormUrlEncoded
    @POST("/2/remind/set_count")
    public Observable<Response> resetUnReadMsg(@Field("type") String type, @Field("value") int value);

    @GET
    public Observable<WeiboIds> getHotWeiboIds(@Url String url, @Query("a") String a, @Query("c") String c,
                                               @Query("catlog_id") String catlog_id, @Query("page") int page);

    @GET("/2/search/statuses")
    public Observable<QueryWeiboResponse> searchStatus(@Query("q") String q, @Query("count") int count,
                                                       @Query("page") int page);


    @GET("/2/search/users")
    public Observable<FriendshipResponse> searchUsers(@Query("q") String q, @Query("count") int count,
                                                      @Query("page") int page);

    @FormUrlEncoded
    @POST
    public Observable<VideoInfo> getVideoInfo(@Url String url, @Query("a") String a, @Query("c") String c,
                                              @FieldMap Map<String, Object> fields);

    @GET("/2/infopage/get_content")
    public Observable<Article> getInfoPageContent(@Query("containerid") String containerid);


    @FormUrlEncoded
    @POST("2/friendships/create")
    Observable<User> followUser(@Field("screen_name") String screen_name);

    @FormUrlEncoded
    @POST("2/friendships/destroy")
    Observable<User> unfollowUser(@Field("screen_name") String screen_name);

    @FormUrlEncoded
    @POST("2/comments/destroy")
    Observable<Comment> deleteComment(@Field("cid") long cid);

    @FormUrlEncoded
    @POST("2/comments/reply")
    Observable<Comment> replyComment(@Field("cid") long cid, @Field("id") long weiboId,
                                     @Field("comment") String comment);


    @GET("2/friendships/friends")
    Observable<FriendshipResponse> getFriends(@Query("uid") long uid, @Query("count") int count,
                                              @Query("trim_status") int trim_status, @Query("cursor") long cursor);

    @GET("2/friendships/followers")
    Observable<FriendshipResponse> getFollowers(@Query("uid") long uid, @Query("count") int count,
                                                @Query("trim_status") int trim_status, @Query("cursor") long cursor);

    @GET("2/comments/by_me")
    Observable<QueryWeiboCommentResponse> getPublishComments(@Query("since_id") long since_id, @Query("max_id") long max_id,
                                                             @Query("count") int count, @Query("page") int page);


    @GET("2/users/show")
    Observable<User> getWeiBoUserInfoByName(@Query("screen_name") String name);

    @GET("2/users/show")
    Observable<User> getWeiBoUserInfoByUid(@Query("uid") long uid);

    /**
     * 评论微博
     * @param comment
     * @param id 微博id
     * @return
     */
    @FormUrlEncoded
    @POST("2/comments/create")
    Observable<Comment> createCommentForWeibo(@Field("comment") String comment, @Field("id") long id);

    @GET("2/comments/show")
    Observable<QueryWeiboCommentResponse> getCommentsByWeibo(@Query("id") long id, @Query("since_id") long since_id,
                                                             @Query("max_id") long max_id, @Query("count") int count,
                                                             @Query("page") int page);

    @FormUrlEncoded
    @POST("2/statuses/repost")
    Observable<Weibo> repostWeibo(@Field("id") long id, @Field("status") String status);

    @GET("2/statuses/repost_timeline")
    Observable<QueryRepostWeiboResponse> getRepostWeibos(@Query("id") long id, @Query("since_id") long since_id,
                                                         @Query("max_id") long max_id, @Query("count") int count,
                                                         @Query("page") int page);

    @FormUrlEncoded
    @POST("2/favorites/create")
    Observable<FavoritesCreateResponse> favoritesCreate(@Field("id") long id);

    @FormUrlEncoded
    @POST("2/favorites/destroy")
    Observable<FavoritesCreateResponse> favoritesDestroy(@Field("id") long id);

    @FormUrlEncoded
    @POST("2/statuses/destroy")
    Observable<Weibo> statusesDestroy(@Field("id") long id);

    //    已经上传的图片pid，多个时使用英文半角逗号符分隔，最多不超过9个。
    @FormUrlEncoded
    @POST("2/statuses/upload_url_text")
    Observable<Weibo> publishWeiboOfMultiImage(@Field("status") String status, @Field("pic_id") String picIds);

    @Multipart
    @POST("2/statuses/upload")
    Observable<Weibo> publishWeiboOfOneImage(@Part("status") String status, @Part MultipartBody.Part file);

    @GET("2/attitudes/show")
    Observable<QueryWeiboAttitudeResponse> getWeiboAttitudes(@Query("id") long id, @Query("page") int page,
                                                             @Query("count") int count);

    @FormUrlEncoded
    @POST("2/statuses/update")
    Observable<Weibo> publishWeiboOfOnlyText(@Field("status") String status);

}

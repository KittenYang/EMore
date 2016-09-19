package com.caij.emore.api;

import com.caij.emore.Key;
import com.caij.emore.account.Token;
import com.caij.emore.bean.Attitude;
import com.caij.emore.bean.Comment;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.bean.response.FavoritesCreateResponse;
import com.caij.emore.bean.response.FriendshipResponse;
import com.caij.emore.bean.response.QueryRepostWeiboResponse;
import com.caij.emore.bean.response.QueryWeiboAttitudeResponse;
import com.caij.emore.bean.response.QueryWeiboCommentResponse;
import com.caij.emore.bean.response.QueryWeiboResponse;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.database.bean.User;
import com.caij.emore.database.bean.Weibo;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;
import com.caij.emore.utils.GsonUtils;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Caij on 2016/5/28.
 */
public interface WeiBoService {



    public class Factory {

        static WeiBoService sWeiBoService;

        static public WeiBoService create() {
            if (sWeiBoService == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Key.WEIBO_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .callFactory(OkHttpClientProvider.getDefaultOkHttpClient())
                        .build();
                sWeiBoService = retrofit.create(WeiBoService.class);
            }
            return sWeiBoService;
        }
    }


    //    https://api.weibo.com/oauth2/access_token
    @FormUrlEncoded
    @POST("oauth2/access_token")
    Observable<Token> getAccessToken(@Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                                     @Field("grant_type") String grantType,
                                     @Field("code") String code,
                                     @Field("redirect_uri") String redirectUrL);

    @GET("2/users/show.json")
    Observable<User> getWeiBoUserInfoByName(@Query("access_token") String accessToken, @Query("screen_name") String name);

    @GET("2/users/show.json")
    Observable<User> getWeiBoUserInfoByUid(@Query("access_token") String accessToken, @Query("uid") long uid);

//    @GET("2/statuses/friends_timeline.json")
//    Observable<QueryWeiboResponse> getFriendsWeibo(@Query("access_token") String accessToken,
//                                                   @Query("since_id") long since_id,
//                                                   @Query("max_id") long max_id,
//                                                   @Query("count") int count, @Query("page") int page);

    /**
     * 评论微博
     * @param accessToken
     * @param comment
     * @param id 微博id
     * @return
     */
    @FormUrlEncoded
    @POST("2/comments/create.json")
    Observable<Comment> createCommentForWeibo(@Field("access_token") String accessToken,
                                               @Field("comment") String comment,
                                               @Field("id") long id);

    @GET("2/comments/show.json")
    Observable<QueryWeiboCommentResponse> getCommentsByWeibo(@Query("access_token") String accessToken,
                                                             @Query("id") long id,
                                                             @Query("since_id") long since_id,
                                                             @Query("max_id") long max_id,
                                                             @Query("count") int count,
                                                             @Query("page") int page);


    @Multipart
    @POST("2/statuses/upload.json")
    Observable<Weibo> publishWeiboOfOneImage(@Header("Authorization") String accessToken,
                                             @Part("status") String status, @Part MultipartBody.Part file);

    @Multipart
    @POST("2/statuses/upload_pic.json")
    Observable<UploadImageResponse> uploadWeiboOfOneImage(@Part("access_token") RequestBody access_token,
                                                          @Part MultipartBody.Part file);

//    已经上传的图片pid，多个时使用英文半角逗号符分隔，最多不超过9个。
    @FormUrlEncoded
    @POST("2/statuses/upload_url_text.json")
    Observable<Weibo> publishWeiboOfMultiImage(@Field("access_token") String accessToken,
                                               @Field("status") String status, @Field("pic_id") String picIds);

    @FormUrlEncoded
    @POST("2/statuses/update.json")
    Observable<Weibo> publishWeiboOfOnlyText(@Field("access_token") String accessToken,
                                               @Field("status") String status);

    @FormUrlEncoded
    @POST("2/statuses/repost.json")
    Observable<Weibo> repostWeibo(@Field("access_token") String accessToken,
                                  @Field("id") long id,
                                  @Field("status") String status);

    @GET("2/statuses/repost_timeline.json")
    Observable<QueryRepostWeiboResponse> getRepostWeibos(@Query("access_token") String accessToken,
                                                            @Query("id") long id,
                                                            @Query("since_id") long since_id,
                                                            @Query("max_id") long max_id,
                                                            @Query("count") int count,
                                                            @Query("page") int page);

//    @GET("2/statuses/user_timeline.json")
//    Observable<UserWeiboResponse> getUserWeibos(@Query("access_token") String accessToken,
//                                                @Query("screen_name") String screen_name,
//                                                @Query("feature") int feature,
//                                                @Query("since_id") long since_id,
//                                                @Query("max_id") long max_id,
//                                                @Query("count") int count,
//                                                @Query("page") int page);

    @FormUrlEncoded
    @POST("2/friendships/create.json")
    Observable<User> followUser(@Field("access_token") String accessToken,
                                              @Field("screen_name") String screen_name);

    @FormUrlEncoded
    @POST("2/friendships/destroy.json")
    Observable<User> unfollowUser(@Field("access_token") String accessToken,
                                @Field("screen_name") String screen_name);

    @FormUrlEncoded
    @POST("2/favorites/create.json")
    Observable<FavoritesCreateResponse> favoritesCreate(@Field("access_token") String accessToken,
                                                        @Field("id") long id);

    @FormUrlEncoded
    @POST("2/favorites/destroy.json")
    Observable<FavoritesCreateResponse> favoritesDestroy(@Field("access_token") String accessToken,
                                    @Field("id") long id);

    @FormUrlEncoded
    @POST("2/statuses/destroy.json")
    Observable<Weibo> statusesDestroy(@Field("access_token") String accessToken,
                                      @Field("id") long id);

    @FormUrlEncoded
    @POST("2/comments/destroy.json")
    Observable<Comment> deleteComment(@Field("access_token") String accessToken,
                                      @Field("cid") long cid);

    @FormUrlEncoded
    @POST("2/comments/reply.json")
    Observable<Comment> replyComment(@Field("access_token") String accessToken,
                                      @Field("cid") long cid, @Field("id") long weiboId,
                                     @Field("comment") String comment);

    @GET("2/friendships/friends.json")
    Observable<FriendshipResponse> getFriends(@Query("access_token") String accessToken,
                                              @Query("uid") long uid,
                                              @Query("count") int count,
                                              @Query("trim_status") int trim_status,
                                              @Query("cursor") long cursor);

    @GET("2/friendships/followers.json")
    Observable<FriendshipResponse> getFollowers(@Query("access_token") String accessToken,
                                              @Query("uid") long uid,
                                              @Query("count") int count,
                                              @Query("trim_status") int trim_status,
                                              @Query("cursor") long cursor);

//    @GET("2/statuses/mentions.json")
//    Observable<QueryWeiboResponse> getWeiboMentions(@Query("access_token") String accessToken,
//                                                         @Query("since_id") long since_id,
//                                                         @Query("max_id") long max_id,
//                                                         @Query("count") int count,
//                                                         @Query("page") int page);

//    @GET("2/comments/mentions.json")
//    Observable<QueryWeiboCommentResponse> getCommentsMentions(@Query("access_token") String accessToken,
//                                                             @Query("since_id") long since_id,
//                                                             @Query("max_id") long max_id,
//                                                             @Query("count") int count,
//                                                             @Query("page") int page);

    @GET("2/comments/by_me.json")
    Observable<QueryWeiboCommentResponse> getPublishComments(@Query("access_token") String accessToken,
                                                              @Query("since_id") long since_id,
                                                              @Query("max_id") long max_id,
                                                              @Query("count") int count,
                                                              @Query("page") int page);

//    @GET("2/comments/to_me.json")
//    Observable<QueryWeiboCommentResponse> getAcceptComments(@Query("access_token") String accessToken,
//                                                             @Query("since_id") long since_id,
//                                                             @Query("max_id") long max_id,
//                                                             @Query("count") int count,
//                                                             @Query("page") int page);

//    @GET
//    Observable<UnreadMessageCount> getUnReadMessage(@Url String url, @Query("access_token") String accessToken,
//                                                    @Query("uid") long uid) ;

    @GET("/2/direct_messages/user_list.json")
    Observable<MessageUser> getMessageUserList(@Query("access_token") String accessToken,
                                               @Query("count") int count,
                                               @Query("cursor") long cursor) ;

    @GET("/2/direct_messages/conversation.json")
    Observable<UserMessageResponse> getUserMessage(@Query("access_token") String accessToken,
                                                   @Query("uid") long uid,
                                                   @Query("since_id") long since_id,
                                                   @Query("max_id") long max_id,
                                                   @Query("count") int count,
                                                   @Query("page") int page);

    @FormUrlEncoded
    @POST("/2/direct_messages/new.json")
    Observable<DirectMessage> createMessage(@Field("access_token") String accessToken,
                                            @Field("text") String text,
                                            @Field("uid") long uid,
                                            @Field("screen_name") String screen_name,
                                            @Field("fids") String fids);

    @Multipart
    @POST
    public Observable<MessageImage> uploadMessageImage(@Url String url,
                                                       @QueryMap Map<String, Object> paramMap,
                                                       @Query("access_token") String accessToken,
                                                       @Query("tuid") long uid,
                                                       @Part MultipartBody.Part file);

    @GET
    public Observable<MessageImage> getMessageImageInfo(@Url String url,
                                                       @Query("access_token") String accessToken,
                                                       @Query("fid") long fid);

//    @GET("/2/statuses/show.json")
//    Observable<Weibo> getWeiboById(@Query("access_token") String accessToken,
//                                   @Query("id") long id, @Query("isGetLongText") int isGetLongText);


    @GET("/2/short_url/info.json")
    Call<ShortUrlInfo> getShortUrlInfo(@Query("access_token") String accessToken,
                                       @Query("url_short") List<String> url_shorts);



    @GET("2/attitudes/show.json")
    Observable<QueryWeiboAttitudeResponse> getWeiboAttitudes(@Query("access_token") String accessToken,
                                                             @Query("id") long id, @Query("page") int page,
                                                             @Query("count") int count);

//    @GET("2/statuses/show_batch.json")
//    Observable<QueryWeiboResponse> getWeibsoByIds(@Query("access_token") String accessToken,
//                                                             @Query("ids") String ids);

    @GET("2/search/topics.json")
    public Observable<QueryWeiboResponse> getTopicsByKey(@Query("access_token") String accessToken,
                             @Query("q") String q,
                             @Query("count") int count,
                             @Query("page") int page);

    @GET
    public Observable<SinaSearchRecommend> getSearchRecommend(@Url String url, @Query("where") String where,
                                                          @Query("type") String type,
                                                          @Query("key") String key);


//        @FormUrlEncoded
//    @POST("2/attitudes/destroy.json")
//    Observable<Response> destoryAttitudesWeibo(@Field("access_token")String token, @Field("attitude")String attitude,
//                                               @Field("id")long weiboId);


//        https://api.weibo.com/2/attitudes/create.json
//        smile,naughty,surprise,sad,heart
//    @FormUrlEncoded
//    @POST("2/attitudes/create.json")
//    Observable<Attitude> attitudeWeibo(@Field("access_token") String accessToken,
//                                      @Field("id") long weiboId,
//                                      @Field("attitude") String attitude);

//
//    @GET
//    Observable<QueryWeiboAttitudeResponse> getToMeAttitudes(@Url String url, @Query("access_token") String accessToken,
//                                                            @Query("since_id") long since_id,
//                                                             @Query("max_id") long max_id,
//                                                            @Query("source") String source,
//                                                            @Query("page") int page,
//                                                             @Query("count") int count);


    @FormUrlEncoded
    @POST("/2/direct_messages/destroy_batch.json")
    Observable<Response> deleteMessageConversation(@Field("access_token") String accessToken,
                                       @Field("uid") long uid);

}

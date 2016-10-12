package com.caij.emore.api;

import com.caij.emore.Key;
import com.caij.emore.account.Token;
import com.caij.emore.bean.MessageUser;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.UserMessageResponse;
import com.caij.emore.database.bean.DirectMessage;
import com.caij.emore.database.bean.MessageImage;
import com.caij.emore.database.bean.UploadImageResponse;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.api.ex.GsonConverterFactory;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
                        .callFactory(WeiCoService.WeiCoFactory.createOkHttpClient())
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

    @Multipart
    @POST("2/statuses/upload_pic.json")
    Observable<UploadImageResponse> uploadWeiboOfOneImage(@Part MultipartBody.Part file);

    @GET("/2/direct_messages/user_list.json")
    Observable<MessageUser> getMessageUserList(@Query("count") int count,
                                               @Query("cursor") long cursor) ;

    @GET("/2/direct_messages/conversation.json")
    Observable<UserMessageResponse> getUserMessage(@Query("uid") long uid,
                                                   @Query("since_id") long since_id,
                                                   @Query("max_id") long max_id,
                                                   @Query("count") int count,
                                                   @Query("page") int page);

    @FormUrlEncoded
    @POST("/2/direct_messages/new.json")
    Observable<DirectMessage> createMessage(@Field("text") String text, @Field("uid") long uid,
                                            @Field("screen_name") String screen_name, @Field("fids") String fids);

    @Multipart
    @POST
    public Observable<MessageImage> uploadMessageImage(@Url String url,
                                                       @QueryMap Map<String, Object> paramMap,
                                                       @Query("tuid") long uid,
                                                       @Part MultipartBody.Part file);

    @GET
    public Observable<MessageImage> getMessageImageInfo(@Url String url, @Query("fid") long fid);


    @GET("/2/short_url/info.json")
    Call<ShortUrlInfo> getShortUrlInfo(@Query("url_short") List<String> url_shorts);



    @GET
    public Observable<SinaSearchRecommend> getSearchRecommend(@Url String url, @Query("where") String where,
                                                          @Query("type") String type,
                                                          @Query("key") String key);



    @FormUrlEncoded
    @POST("/2/direct_messages/destroy_batch.json")
    Observable<Response> deleteMessageConversation(@Field("uid") long uid);

}

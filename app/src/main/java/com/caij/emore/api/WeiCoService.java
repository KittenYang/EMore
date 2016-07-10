package com.caij.emore.api;

import com.caij.emore.Key;
import com.caij.emore.bean.response.Response;
import com.caij.emore.bean.response.WeiCoLoginResponse;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
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
    public Observable<Response> attitudesWeibo(@FieldMap Map<String, Object> paramMap,
                                               @Field("attitude") String attitude, @Field("id") long weiboId);

    @FormUrlEncoded
    @POST("/2/like/cancel_like")
    public Observable<Response> destoryAttitudesWeibo(@FieldMap Map<String, Object> paramMap,
                                               @Field("attitude") String attitude, @Field("id") long weiboId);

}

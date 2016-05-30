package com.caij.weiyo.api;

import com.caij.weiyo.Key;
import com.caij.weiyo.bean.AccessToken;
import com.caij.weiyo.okhttp.OkHttpClientProvider;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
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
                        .addConverterFactory(GsonConverterFactory.create())
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
    Observable<AccessToken> getAccessToken(@Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                                           @Field("grant_type") String grantType,
                                           @Field("code") String code,
                                           @Field("redirect_uri") String redirectUrL);
}

package com.caij.weiyo.api;

import com.caij.weiyo.Key;
import com.caij.weiyo.bean.User;
import com.caij.weiyo.utils.GsonUtils;
import com.caij.weiyo.utils.okhttp.OkHttpClientProvider;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
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

    //    Host: weicoapi.weico.cc
//    /portal.php?a=like_weibo&weibo_id=3986623807526832&user_id=5834785581&ua=Nexus+5_5.0.1_weico_460


    @GET("portal.php")
    Observable<User> doAction(@Query("a") String a, @Query("weibo_id") long weibo_id, @Query("user_id") long user_id,@Query("ua") String ua);
}

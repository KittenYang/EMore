package com.caij.emore.di.module;

import android.app.Application;

import com.caij.emore.EMApplication;
import com.caij.emore.Key;
import com.caij.emore.account.Token;
import com.caij.emore.account.UserPrefs;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.api.ex.GsonConverterFactory;
import com.caij.emore.di.an.ApiClient;
import com.caij.emore.di.an.ImageClient;
import com.caij.emore.utils.GsonUtils;
import com.caij.emore.utils.okhttp.OkHttpClientProvider;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {

    public static final String PARAMETER_SOURCE_NAME = "source";
    public static final String PARAMETER_FROM_NAME = "from";
    public static final String PARAMETER_TOKEN_NAME = "access_token";

    private static final long READ_TIMEOUT = 10;
    private static final long WRITE_TIMEOUT = 10;
    private static final long CONNECT_TIMEOUT = 5;

    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    @ApiClient
    OkHttpClient provideApiOkHttpClient() {
        return createOkHttpClient();
    }

    @Provides
    @Singleton
    @ImageClient
    OkHttpClient provideImageLoadOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .build();
    }

    @Provides
    @Singleton
    WeiBoService provideWeiBoService(@ApiClient OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Key.WEIBO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .callFactory(okHttpClient)
                .build();
        return retrofit.create(WeiBoService.class);
    }

    @Provides
    @Singleton
    WeiCoService provideWeiCoService(@ApiClient OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Key.WEICO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .callFactory(okHttpClient)
                .build();
        return retrofit.create(WeiCoService.class);
    }

    private static OkHttpClient createOkHttpClient() {
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
    private static Request interceptRequest(Request request) {
        RequestBody requestBody = request.body();
        Token token = UserPrefs.get(EMApplication.getInstance()).getToken();
        String accessToken = token == null ? "" : token.getAccess_token();
        if ("GET".equals(request.method())) {  //get
            HttpUrl url = request.url().newBuilder()
                    .addQueryParameter(PARAMETER_SOURCE_NAME, Key.WEICO_APP_ID)
                    .addQueryParameter(PARAMETER_FROM_NAME, Key.WEICO_APP_FROM)
                    .addQueryParameter(PARAMETER_TOKEN_NAME, accessToken)
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
            builder.add(PARAMETER_TOKEN_NAME, accessToken);

            formBody = builder.build();

            request = request.newBuilder().post(formBody).build();
        }else if (requestBody instanceof MultipartBody) {
            MultipartBody multipartBody = (MultipartBody) requestBody;
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(multipartBody.type());
            builder.addFormDataPart(PARAMETER_TOKEN_NAME, accessToken);
            builder.addFormDataPart(PARAMETER_SOURCE_NAME, Key.WEICO_APP_ID);
            builder.addFormDataPart(PARAMETER_FROM_NAME, Key.WEICO_APP_FROM);

            for (MultipartBody.Part part : multipartBody.parts()) {
                builder.addPart(part);
            }

            request = request.newBuilder().post(builder.build()).build();
        }

        return request;
    }

}

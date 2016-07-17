package com.caij.emore.utils;

import android.text.TextUtils;

import com.caij.emore.utils.okhttp.OkHttpClientProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Caij on 2016/7/17.
 */
public class HtmlUtil {

    public static String getHtmlTitleByString(final String s)
    {
        String regex;
        regex = "<title>.*?</title>";
        final Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
        final Matcher ma = pa.matcher(s);
        while (ma.find())
        {
            return ma.group();
        }
        return null;
    }

    public static String getHtmlTitleByUrl(final String url) throws IOException {
        OkHttpClient okHttpClient = OkHttpClientProvider.getDefaultOkHttpClient();
        okHttpClient = okHttpClient.newBuilder().readTimeout(3000, TimeUnit.MILLISECONDS)
                .writeTimeout(3000, TimeUnit.MILLISECONDS).build();
        Request.Builder okHttpRequestBuilder = new Request.Builder();
        okHttpRequestBuilder.url(url);
        Request okHttpRequest = okHttpRequestBuilder.build();
        Call okHttpCall = okHttpClient.newCall(okHttpRequest);
        ResponseBody responseBody = okHttpCall.execute().body();
        MediaType mediaType = responseBody.contentType();
        InputStream inputStream = responseBody.byteStream();
        final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream,
                mediaType.charset(Charset.forName("utf-8"))));// 读取网页全部内容
        String str ;
        final StringBuilder sb = new StringBuilder();
        while ((str = in.readLine()) != null) {
            System.out.println(str);
            sb.append(str);
            String title  = getHtmlTitleByString(sb.toString());
            if (title != null) {
                return title;
            }
        }
        return null;
    }
}

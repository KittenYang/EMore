package com.caij.emore.source.server;

import com.caij.emore.api.WeiBoService;
import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.source.UrlSource;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Caij on 2016/7/18.
 */
public class ServerUrlSource implements UrlSource {

    @Override
    public ShortUrlInfo getShortUrlInfo(String token, List<String> shortUrls) {
        Call<ShortUrlInfo> call = WeiBoService.Factory.create().getShortUrlInfo(token, shortUrls);
        Response<ShortUrlInfo> responseResponse = null;
        try {
            responseResponse = call.execute();
            if (responseResponse.code() == 200) {
                return responseResponse.body();
            }
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    public UrlInfo getShortUrlInfo(String token, String shortUrl) {
        return null;
    }

    @Override
    public void saveUrlInfo(UrlInfo urlInfo) {

    }
}

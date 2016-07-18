package com.caij.emore.source;

import com.caij.emore.bean.response.QueryUrlResponse;
import com.caij.emore.database.bean.UrlInfo;

import java.util.List;

/**
 * Created by Caij on 2016/7/18.
 */
public interface UrlSource {

    QueryUrlResponse getShortUrlInfo(String token, List<String> shortUrls);

    UrlInfo getShortUrlInfo(String token, String shortUrl);

    void saveUrlInfo(UrlInfo urlInfo);
}

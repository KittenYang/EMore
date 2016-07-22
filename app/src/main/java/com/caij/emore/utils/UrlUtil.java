package com.caij.emore.utils;

import com.caij.emore.bean.response.QueryUrlResponse;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.source.UrlSource;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Caij on 2016/6/24.
 */
public class UrlUtil {

    public static Map<String, UrlInfo> getShortUrlInfos(List<String> shortUrls, UrlSource serverUrlSource, UrlSource localUrlSource, String token){
        Map<String, UrlInfo> shortLongLinkMap = new HashMap<String, UrlInfo>();
        if (shortUrls.size() > 0) {
            int size = shortUrls.size() / 20 + 1;
            List<String> params = new ArrayList<String>(20);
            for (int i = 0; i < size; i ++) {
                params.clear();
                for (int j = i * 20; j < Math.min(shortUrls.size(), (i + 1) * 20); j ++) {
                    String shortUrl  = shortUrls.get(j);
                    if (UrlInfo.isShortUrl(shortUrl)) {
                        UrlInfo urlInfo = localUrlSource.getShortUrlInfo(token, shortUrl);
                        if (urlInfo != null) {
                            shortLongLinkMap.put(urlInfo.getShortUrl(), urlInfo);
                        } else {
                            params.add(shortUrl);
                        }
                    }
                }
                if (params.size() > 0) {
                    try {
                        QueryUrlResponse queryUrlResponse = serverUrlSource.getShortUrlInfo(token, params);
                        if (queryUrlResponse != null) {
                            for (Object obj : queryUrlResponse.getUrls()) {
                                UrlInfo shortLongLink = new UrlInfo(new JSONObject(GsonUtils.toJson(obj)));
                                localUrlSource.saveUrlInfo(shortLongLink);
                                shortLongLinkMap.put(shortLongLink.getShortUrl(), shortLongLink);
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        return shortLongLinkMap;
    }
}

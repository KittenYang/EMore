package com.caij.emore.utils;

import com.caij.emore.bean.ShortUrlInfo;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.source.UrlSource;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Caij on 2016/6/24.
 */
public class UrlUtil {

    public static Map<String, ShortUrlInfo.UrlsBean> getShortUrlInfos(List<String> shortUrls, UrlSource serverUrlSource, UrlSource localUrlSource, String token){
        Map<String, ShortUrlInfo.UrlsBean> shortLongLinkMap = new HashMap<String, ShortUrlInfo.UrlsBean>();
        if (shortUrls.size() > 0) {
            int size = shortUrls.size() / 20 + 1;
            List<String> params = new ArrayList<String>(20);
            for (int i = 0; i < size; i ++) {
                params.clear();
                for (int j = i * 20; j < Math.min(shortUrls.size(), (i + 1) * 20); j ++) {
                    String shortUrl  = shortUrls.get(j);
                    if (isShortUrl(shortUrl)) {
                        UrlInfo urlInfo = localUrlSource.getShortUrlInfo(token, shortUrl);
                        if (urlInfo != null) {
                            ShortUrlInfo.UrlsBean urlsBean = GsonUtils.fromJson(urlInfo.getUrl_info_json(), ShortUrlInfo.UrlsBean.class);
                            shortLongLinkMap.put(urlInfo.getShortUrl(), urlsBean);
                        } else {
                            params.add(shortUrl);
                        }
                    }
                }
                if (params.size() > 0) {
                    try {
                        ShortUrlInfo queryUrlResponse = serverUrlSource.getShortUrlInfo(token, params);
                        if (queryUrlResponse != null) {
                            for (ShortUrlInfo.UrlsBean urlsBean : queryUrlResponse.getUrls()) {
                                UrlInfo shortLongLink = new UrlInfo();
                                shortLongLink.setShortUrl(urlsBean.getUrl_short());
                                shortLongLink.setUrl_info_json(GsonUtils.toJson(urlsBean));
                                localUrlSource.saveUrlInfo(shortLongLink);
                                shortLongLinkMap.put(shortLongLink.getShortUrl(), urlsBean);
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.d("URLUTIL", e.getMessage());
                    }
                }
            }
        }
        return shortLongLinkMap;
    }

    private static boolean isShortUrl(String url) {
        return url.contains("t.cn");
    }

}

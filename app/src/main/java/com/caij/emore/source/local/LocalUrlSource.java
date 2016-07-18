package com.caij.emore.source.local;

import com.caij.emore.bean.response.QueryUrlResponse;
import com.caij.emore.database.bean.UrlInfo;
import com.caij.emore.source.UrlSource;
import com.caij.emore.utils.db.DBManager;

import java.util.List;

/**
 * Created by Caij on 2016/7/18.
 */
public class LocalUrlSource implements UrlSource {

    @Override
    public QueryUrlResponse getShortUrlInfo(String token, List<String> shortUrls) {
        return null;
    }

    @Override
    public UrlInfo getShortUrlInfo(String token, String shortUrl) {
        return DBManager.getDaoSession().getUrlInfoDao().load(shortUrl);
    }

    @Override
    public void saveUrlInfo(UrlInfo urlInfo) {
        DBManager.getDaoSession().getUrlInfoDao().insertOrReplace(urlInfo);
    }
}

package com.caij.emore.source.server;

import com.caij.emore.Key;
import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.Article;
import com.caij.emore.source.ArticleSource;

import rx.Observable;

/**
 * Created by Caij on 2016/9/3.
 */
public class SercerArticleSource implements ArticleSource {

    private WeiCoService mWeiCoService;

    public SercerArticleSource() {
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<Article> getArticleInfo(String accessToken, String containerid) {
        return mWeiCoService.getInfoPageContent(accessToken, Key.WEIBO_APP_ID, containerid);
    }
}

package com.caij.emore.remote.imp;

import com.caij.emore.api.WeiCoService;
import com.caij.emore.bean.Article;
import com.caij.emore.remote.ArticleApi;

import rx.Observable;

/**
 * Created by Caij on 2016/9/3.
 */
public class ArticleApiImp implements ArticleApi {

    private WeiCoService mWeiCoService;

    public ArticleApiImp() {
        mWeiCoService = WeiCoService.WeiCoFactory.create();
    }

    @Override
    public Observable<Article> getArticleInfo(String accessToken, String containerId) {
        return mWeiCoService.getInfoPageContent(containerId);
    }
}

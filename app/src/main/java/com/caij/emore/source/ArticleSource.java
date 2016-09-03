package com.caij.emore.source;

import com.caij.emore.bean.Article;

import rx.Observable;

/**
 * Created by Caij on 2016/9/3.
 */
public interface ArticleSource {

    public Observable<Article> getArticleInfo(String accessToken, String containerid);
}

package com.caij.emore.remote;

import com.caij.emore.bean.SinaSearchRecommend;

import rx.Observable;

/**
 * Created by Caij on 2016/7/26.
 */
public interface SearchRecommendSource {

    Observable<SinaSearchRecommend> getSearchRecommend(String key);
}

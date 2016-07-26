package com.caij.emore.source;

import com.caij.emore.bean.SinaSearchRecommend;

import rx.Observable;

/**
 * Created by Caij on 2016/7/26.
 */
public interface SearchSource {

    Observable<SinaSearchRecommend> getSearchRecommend(String key);
}

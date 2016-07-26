package com.caij.emore.source.server;

import com.caij.emore.Key;
import com.caij.emore.api.WeiBoService;
import com.caij.emore.bean.SinaSearchRecommend;
import com.caij.emore.source.SearchSource;

import rx.Observable;

/**
 * Created by Caij on 2016/7/26.
 */
public class ServerSearchSource implements SearchSource {

    private WeiBoService mWeiBoService;

    public ServerSearchSource() {
        mWeiBoService = WeiBoService.Factory.create();
    }

    @Override
    public Observable<SinaSearchRecommend> getSearchRecommend(String key) {
        return mWeiBoService.getSearchRecommend(Key.SEARCH_URL, "gs_weibo", "gs_weibo", key);
    }
}

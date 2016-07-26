package com.caij.emore.present.view;

import com.caij.emore.bean.SinaSearchRecommend;

import java.util.List;

/**
 * Created by Caij on 2016/7/26.
 */
public interface SearchRecommendView {
    void onSearchSuccess(List<SinaSearchRecommend.RecommendData> data);
}
